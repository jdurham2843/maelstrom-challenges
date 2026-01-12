package com.jdurham.broadcast.bulk;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.Message;
import com.jdurham.MessageContext;
import com.jdurham.NodeHandler;
import com.jdurham.broadcast.BroadcastHandler;

import java.util.Collection;

public class BulkBroadcastHandler implements NodeHandler<
        BulkBroadcastHandler.BulkBroadcastRequest, BulkBroadcastHandler.BulkBroadcastResponse> {

    private final BroadcastHandler broadcastHandler;
    private final NeighborMessageTracker neighborMessageTracker;

    public BulkBroadcastHandler(BroadcastHandler broadcastHandler, NeighborMessageTracker neighborMessageTracker) {
        this.broadcastHandler = broadcastHandler;
        this.neighborMessageTracker = neighborMessageTracker;
    }

    public static class BulkBroadcastRequest extends Message {
        @JsonProperty
        public Collection<Integer> messages;

        public BulkBroadcastRequest(
                Collection<Integer> messages,
                String type,
                int msgId,
                int inReplyTo) {
            this.messages = messages;
            this.type = type;
            this.msgId = msgId;
            this.inReplyTo = inReplyTo;
        }

        public BulkBroadcastRequest() {}
    }

    public static class BulkBroadcastResponse extends Message {
        public BulkBroadcastResponse(
                int msgId,
                int inReplyTo) {
            super("bulk_broadcast_ok", msgId, inReplyTo);
        }
    }

    @Override
    public Class<BulkBroadcastRequest> getRequestType() {
        return BulkBroadcastRequest.class;
    }

    @Override
    public Class<BulkBroadcastResponse> getResponseType() {
        return BulkBroadcastResponse.class;
    }

    @Override
    public BulkBroadcastResponse handle(MessageContext messageContext, BulkBroadcastRequest request) {
        neighborMessageTracker.resolve(messageContext.src(),  request.messages);
        request.messages.forEach(message -> broadcastHandler.handle(messageContext.src(), message));
        return new BulkBroadcastResponse(request.msgId, request.msgId);
    }
}
