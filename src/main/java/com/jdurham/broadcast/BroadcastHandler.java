package com.jdurham.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.*;
import com.jdurham.client.MaelstromClient;
import com.jdurham.client.MaelstromRequest;

import java.util.List;

public class BroadcastHandler implements NodeHandler<
        BroadcastHandler.BroadcastRequest,
        BroadcastHandler.BroadcastResponse> {

    private final MessageStore messageStore;
    private final NodeMetadataStore nodeMetadataStore;
    private final BroadcastMessageTracker messageTracker;
    private final MaelstromClient maelstromClient =  new MaelstromClient();

    public BroadcastHandler(MessageStore messageStore, NodeMetadataStore nodeMetadataStore, BroadcastMessageTracker messageTracker) {
        this.messageStore = messageStore;
        this.nodeMetadataStore = nodeMetadataStore;
        this.messageTracker = messageTracker;

        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000L);

                    System.err.println("Running retry loop now");

                    messageTracker.getAll().forEach(request -> {
                        System.err.printf("Didn't receive a response for %s from node: %s. Resending now%n", request.request().msgId, request.dest());
                        maelstromClient.send(request);
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static class BroadcastRequest extends Request {
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

    public static class BroadcastResponse extends Response {
        public BroadcastResponse(int msgId, int inReplyTo) {
            super("broadcast_ok", msgId, inReplyTo);
        }
    }
    @Override
    public Class<BroadcastRequest> getRequestType() {
        return BroadcastRequest.class;
    }

    @Override
    public Class<BroadcastResponse> getResponseType() {
        return BroadcastResponse.class;
    }

    @Override
    public BroadcastResponse handle(MessageContext messageContext, BroadcastRequest request) {
        if (!messageStore.contains(request.message)) {
            messageStore.add(request.message);

            final List<String> neighbors = nodeMetadataStore.topology.get(nodeMetadataStore.nodeId);
            neighbors.stream()
                    .map(neighbor -> {
                        final int msgId = MsgIdGenerator.getNextId();
                        return new MaelstromRequest(nodeMetadataStore.nodeId, neighbor, new BroadcastRequest(request.message, request.type, msgId, request.inReplyTo), msgId);
                    })
                    .forEach(maelstromRequest -> {
                        messageTracker.track(maelstromRequest.dest(), maelstromRequest);
                        maelstromClient.send(maelstromRequest);
                    });
        }

        return new BroadcastResponse(request.msgId, request.msgId);
    }
}
