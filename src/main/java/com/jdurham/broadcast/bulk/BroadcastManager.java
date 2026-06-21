package com.jdurham.broadcast.bulk;

import com.jdurham.MsgIdGenerator;
import com.jdurham.NodeMetadataStore;
import com.jdurham.client.MaelstromClient;
import com.jdurham.client.MaelstromRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class BroadcastManager {
    private final Map<String, Thread> broadcasters = new ConcurrentHashMap<>();
    private final Map<String, Semaphore> threadNotifiers = new ConcurrentHashMap<>();
    private final Map<String, Set<Integer>> msgIdResponsesReceived = new ConcurrentHashMap<>();

    private final NeighborMessageTracker neighborMessageTracker;
    private final NodeMetadataStore nodeMetadataStore;
    private final MaelstromClient maelstromClient;

    public BroadcastManager(
            NeighborMessageTracker neighborMessageTracker,
            NodeMetadataStore nodeMetadataStore,
            MaelstromClient maelstromClient) {
        this.neighborMessageTracker = neighborMessageTracker;
        this.nodeMetadataStore = nodeMetadataStore;
        this.maelstromClient = maelstromClient;
    }

    private static class RequestTracker {
        private final BulkBroadcastHandler.BulkBroadcastRequest broadcastRequest;
        private Instant retryInstant;
        private int retryCount = 0;

        public RequestTracker(BulkBroadcastHandler.BulkBroadcastRequest broadcastRequest) {
            this.broadcastRequest = broadcastRequest;
            this.retryInstant = Instant.now();
        }
    }

    public void startBroadcaster(String neighborId) {
        if (!broadcasters.containsKey(neighborId)) {
            final Semaphore semaphore = new Semaphore(0);
            threadNotifiers.put(neighborId, semaphore);

            broadcasters.put(neighborId, Thread.startVirtualThread(() -> {
                try {
                    final NavigableMap<Integer, RequestTracker> pendingBroadcastRequests = new TreeMap<>();

                    while (true) {
                        final Set<Integer> receivedMsgIds = msgIdResponsesReceived.remove(neighborId);
                        if (receivedMsgIds != null && !receivedMsgIds.isEmpty()) {
                            final int largestMsgId = Collections.max(receivedMsgIds);
                            final RequestTracker requestTracker = pendingBroadcastRequests.remove(largestMsgId);
                            if (requestTracker != null) {
                                neighborMessageTracker.resolve(neighborId, requestTracker.broadcastRequest.messages);
                            }

                            receivedMsgIds.forEach(pendingBroadcastRequests::remove);
                        }

                        final RequestTracker previousRequestTracker = !pendingBroadcastRequests.isEmpty() ?
                                pendingBroadcastRequests.lastEntry().getValue() :
                                null;

                        final Set<Integer> pendingMessages = neighborMessageTracker.takePendingMessages(neighborId);

                        final boolean shouldRetry = (previousRequestTracker != null && (previousRequestTracker.retryInstant.isBefore(Instant.now())));

                        // Create and Track
                        if (!pendingMessages.isEmpty() || shouldRetry) {
                            final int msgId = MsgIdGenerator.getNextId();

                            final Set<Integer> payload = new HashSet<>(pendingMessages);
                            if (shouldRetry) {
                                payload.addAll(previousRequestTracker.broadcastRequest.messages);
                            }

                            System.err.println("Need to communicate the following to node " + neighborId + ": " + payload);

                            final BulkBroadcastHandler.BulkBroadcastRequest broadcastRequest =
                                    new BulkBroadcastHandler.BulkBroadcastRequest(payload, "bulk_broadcast", msgId, msgId);

                            final RequestTracker requestTracker = new RequestTracker(broadcastRequest);
                            requestTracker.retryCount = previousRequestTracker != null ? previousRequestTracker.retryCount + 1 : 0;
                            requestTracker.retryInstant = Instant.now().plus(Duration.ofMillis(100L));

                            pendingBroadcastRequests.put(msgId, requestTracker);

                            final MaelstromRequest maelstromRequest = new MaelstromRequest(nodeMetadataStore.nodeId, neighborId, broadcastRequest);
                            maelstromClient.send(maelstromRequest);
                        }

                        semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }

    public void logMsgReceived(String neighborId, Integer msgId) {
        msgIdResponsesReceived.computeIfAbsent(neighborId, k -> ConcurrentHashMap.newKeySet()).add(msgId);
    }

    public void wakeBroadcaster(String neighborId) {
        threadNotifiers.get(neighborId).release();
    }
}
