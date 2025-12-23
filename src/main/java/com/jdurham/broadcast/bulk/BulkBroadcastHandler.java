package com.jdurham.broadcast.bulk;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.*;
import com.jdurham.broadcast.BroadcastHandler;
import com.jdurham.broadcast.BroadcastOkHandler;

import java.util.List;

public class BulkBroadcastHandler implements NodeHandler<
        BulkBroadcastHandler.BulkBroadcastRequest, BulkBroadcastHandler.BulkBroadcastResponse> {

    private final BroadcastHandler broadcastHandler;

    public BulkBroadcastHandler(BroadcastHandler broadcastHandler) {
        this.broadcastHandler = broadcastHandler;
    }

    public static class BulkBroadcastRequest extends Message {
        @JsonProperty
        public List<BroadcastHandler.BroadcastRequest> broadcastRequests;

        public BulkBroadcastRequest(
                List<BroadcastHandler.BroadcastRequest> broadcastRequests,
                String type,
                int msgId,
                int inReplyTo) {
            this.broadcastRequests = broadcastRequests;
            this.type = type;
            this.msgId = msgId;
            this.inReplyTo = inReplyTo;
        }

        public BulkBroadcastRequest() {}
    }

    public static class BulkBroadcastResponse extends Message {
        @JsonProperty
        public List<BroadcastOkHandler.BroadcastOkRequest> broadcastOkResponses;

        public BulkBroadcastResponse(
                List<BroadcastOkHandler.BroadcastOkRequest> broadcastResponses,
                int msgId,
                int inReplyTo) {
            super("bulk_broadcast_ok", msgId, inReplyTo);
            this.broadcastOkResponses = broadcastResponses;
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
        final List<BroadcastOkHandler.BroadcastOkRequest> messages = request.broadcastRequests.stream()
                .map(req -> broadcastHandler.handle(messageContext, req))
                .toList();

        return new BulkBroadcastResponse(messages, request.msgId, request.msgId);
    }
}
