package com.jdurham.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.*;
import com.jdurham.client.MaelstromClient;
import com.jdurham.client.MaelstromRequest;

import java.time.Duration;
import java.util.List;

public class BroadcastHandler implements NodeHandler<
        BroadcastHandler.BroadcastRequest,
        BroadcastOkHandler.BroadcastOkRequest> {

    private final MessageStore messageStore;
    private final NodeMetadataStore nodeMetadataStore;
    private final BroadcastMessageTracker broadcastMessageTracker;
    private final MaelstromClient maelstromClient = new MaelstromClient();

    public BroadcastHandler(MessageStore messageStore, NodeMetadataStore nodeMetadataStore, BroadcastMessageTracker broadcastMessageTracker) {
        this.messageStore = messageStore;
        this.nodeMetadataStore = nodeMetadataStore;
        this.broadcastMessageTracker = broadcastMessageTracker;
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

            Thread.startVirtualThread(() -> {
                final List<String> neighbors = nodeMetadataStore.topology.get(nodeMetadataStore.nodeId);
                neighbors.stream().filter(neighbor -> !messageContext.src().equals(neighbor)).forEach(neighbor -> {
                    final int msgId = MsgIdGenerator.getNextId();

                    final BroadcastRequest broadcastRequest = new BroadcastRequest(request.message, request.type, msgId, request.inReplyTo);

                    broadcastMessageTracker.track(neighbor, msgId);
                    sendToNeighbor(neighbor, broadcastRequest);
                });
            });
        }

        return new BroadcastOkHandler.BroadcastOkRequest(request.msgId, request.msgId);
    }

    private void sendToNeighbor(String neighbor, BroadcastRequest broadcastRequest) {
        Thread.startVirtualThread(() -> {
            int retryAttempt = 0;
            while (broadcastMessageTracker.contains(neighbor, broadcastRequest.msgId)) {
                if (retryAttempt > 0) {
                    System.err.println(retryAttempt + " timed out waiting for broadcast response for msgId " + broadcastRequest.msgId + "send to " + neighbor);
                }
                final MaelstromRequest maelstromRequest = new MaelstromRequest(nodeMetadataStore.nodeId, neighbor, broadcastRequest, MsgIdGenerator.getNextId());
                maelstromClient.send(maelstromRequest);

                retryAttempt++;
                try {
                    Thread.sleep(Duration.ofSeconds(retryAttempt));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
