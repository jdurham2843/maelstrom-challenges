package com.jdurham.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.*;
import com.jdurham.broadcast.bulk.BulkBroadcastHandler;
import com.jdurham.client.MaelstromClient;
import com.jdurham.client.MaelstromRequest;

import java.util.List;

public class BroadcastHandler implements NodeHandler<
        BroadcastHandler.BroadcastRequest,
        BroadcastOkHandler.BroadcastOkRequest> {

    private final MessageStore messageStore;
    private final NodeMetadataStore nodeMetadataStore;
    private final BroadcastMessageTracker nearImmediateTracker;
    private final MaelstromClient maelstromClient = new MaelstromClient();

    public BroadcastHandler(MessageStore messageStore, NodeMetadataStore nodeMetadataStore, BroadcastMessageTracker retryMessageTracker) {
        this.messageStore = messageStore;
        this.nodeMetadataStore = nodeMetadataStore;
        this.nearImmediateTracker = new BroadcastMessageTracker();

        // near-immediate send
        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    Thread.sleep(10L);

                    nearImmediateTracker.takeAll().forEach(requests -> {
                        if (requests.getValue().isEmpty()) return;

                        final List<BroadcastRequest> broadcastRequests = requests.getValue().values().stream()
                                .map(req -> (BroadcastRequest) req)
                                .toList();

                        if (broadcastRequests.isEmpty()) return;

                        final int bulkRequestMsgId = MsgIdGenerator.getNextId();
                        final var bulkRequest = new BulkBroadcastHandler.BulkBroadcastRequest(broadcastRequests, "bulk_broadcast", bulkRequestMsgId, bulkRequestMsgId);
                        final MaelstromRequest maelstromRequest = new MaelstromRequest(nodeMetadataStore.nodeId, requests.getKey(), bulkRequest, MsgIdGenerator.getNextId());

                        broadcastRequests.forEach(req -> retryMessageTracker.track(requests.getKey(), req));
                        maelstromClient.send(maelstromRequest);
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // retry
        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    Thread.sleep(100L);

                    retryMessageTracker.getAll().forEach(requests -> {
                        if (requests.getValue().isEmpty()) return;

                        final List<BroadcastRequest> broadcastRequests = requests.getValue().values().stream()
                                .map(req -> (BroadcastRequest) req)
                                .toList();

                        System.err.printf("Sending retry messages: %s to %s\n",
                                broadcastRequests.stream().map(req -> req.msgId).map(String::valueOf).toList(),
                                requests.getKey());

                        if (broadcastRequests.isEmpty()) return;

                        final int bulkRequestMsgId = MsgIdGenerator.getNextId();
                        final var bulkRequest = new BulkBroadcastHandler.BulkBroadcastRequest(broadcastRequests, "bulk_broadcast", bulkRequestMsgId, bulkRequestMsgId);
                        final MaelstromRequest maelstromRequest = new MaelstromRequest(nodeMetadataStore.nodeId, requests.getKey(), bulkRequest, MsgIdGenerator.getNextId());

                        maelstromClient.send(maelstromRequest);
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static class BroadcastRequest extends Message {
        @JsonProperty
        int message;

        public BroadcastRequest() {
        }

        public BroadcastRequest(int message, String type, int msgId, int inReplyTo) {
            this.message = message;
            this.type = type;
            this.msgId = msgId;
            this.inReplyTo = inReplyTo;
        }
    }

    public static class BroadcastResponse extends Message {
        public BroadcastResponse(int msgId, int inReplyTo) {
            super("broadcast_ok", msgId, inReplyTo);
        }
    }

    @Override
    public Class<BroadcastRequest> getRequestType() {
        return BroadcastRequest.class;
    }

    @Override
    public Class<BroadcastOkHandler.BroadcastOkRequest> getResponseType() {
        return BroadcastOkHandler.BroadcastOkRequest.class;
    }

    @Override
    public BroadcastOkHandler.BroadcastOkRequest handle(MessageContext messageContext, BroadcastRequest request) {
        if (!messageStore.contains(request.message)) {
            messageStore.add(request.message);

            final List<String> neighbors = nodeMetadataStore.topology.get(nodeMetadataStore.nodeId);
            neighbors.stream().filter(neighbor -> !messageContext.src().equals(neighbor)).forEach(neighbor -> {
                final int msgId = MsgIdGenerator.getNextId();

                final BroadcastRequest broadcastRequest = new BroadcastRequest(request.message, request.type, msgId, request.inReplyTo);

                nearImmediateTracker.track(neighbor, broadcastRequest);
            });
        }

        return new BroadcastOkHandler.BroadcastOkRequest(request.msgId, request.msgId);
    }
}

/*
- if message received not in store
- write to message store
- periodically grab all my messages, and check what messages I haven't observed from neighbor
- bulk send all those messages to neighbor
 */