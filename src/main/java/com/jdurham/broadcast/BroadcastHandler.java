package com.jdurham.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.Message;
import com.jdurham.MessageContext;
import com.jdurham.NodeHandler;
import com.jdurham.NodeMetadataStore;
import com.jdurham.broadcast.bulk.BroadcastManager;
import com.jdurham.broadcast.bulk.NeighborMessageTracker;

import java.util.List;

public class BroadcastHandler implements NodeHandler<
        BroadcastHandler.BroadcastRequest,
        BroadcastOkHandler.BroadcastOkRequest> {

    private final MessageStore messageStore;
    private final NodeMetadataStore nodeMetadataStore;
    private final NeighborMessageTracker neighborMessageTracker;
    private final BroadcastManager broadcastManager;

    public BroadcastHandler(MessageStore messageStore, NodeMetadataStore nodeMetadataStore, NeighborMessageTracker neighborMessageTracker, BroadcastManager broadcastManager) {
        this.messageStore = messageStore;
        this.nodeMetadataStore = nodeMetadataStore;
        this.neighborMessageTracker = neighborMessageTracker;
        this.broadcastManager = broadcastManager;
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
        handle(messageContext.src(), request.message);
        return new BroadcastOkHandler.BroadcastOkRequest(request.msgId, request.msgId);
    }

    public void handle(String src, int message) {
        if (!messageStore.contains(message)) {
            messageStore.add(message);

            nodeMetadataStore.topology.get(nodeMetadataStore.nodeId).stream()
                    .filter(nodeId -> !nodeId.equals(src))
                    .forEach(nodeId -> {
                        neighborMessageTracker.track(nodeId, message);
                    });
        }
    }
}
