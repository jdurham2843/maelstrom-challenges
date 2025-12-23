package com.jdurham.broadcast.bulk;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.*;
import com.jdurham.broadcast.BroadcastHandler;
import com.jdurham.broadcast.BroadcastMessageTracker;
import com.jdurham.broadcast.BroadcastOkHandler;

import java.util.List;

public class BulkBroadcastOkHandler implements NodeHandler<
        BulkBroadcastOkHandler.BroadcastOkRequest,
        BulkBroadcastOkHandler.BroadcastOkResponse> {

    private final BroadcastOkHandler broadcastOkHandler;

    public BulkBroadcastOkHandler(BroadcastOkHandler broadcastOkHandler) {
        this.broadcastOkHandler = broadcastOkHandler;
    }

    public static class BroadcastOkRequest extends Message {
        @JsonProperty
        public List<BroadcastOkHandler.BroadcastOkRequest> broadcastOkResponses;
    }

    public static class BroadcastOkResponse extends Message {
        public BroadcastOkResponse(String type, int msgId, int inReplyTo) {
            super(type, msgId, inReplyTo);
        }
    }

    @Override
    public Class<BroadcastOkRequest> getRequestType() {
        return BroadcastOkRequest.class;
    }

    @Override
    public Class<BroadcastOkResponse> getResponseType() {
        return BroadcastOkResponse.class;
    }

    @Override
    public BroadcastOkResponse handle(MessageContext messageContext, BroadcastOkRequest request) {
        request.broadcastOkResponses.forEach(req -> broadcastOkHandler.handle(messageContext, req));
        return null;
    }
}
