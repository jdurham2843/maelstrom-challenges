package com.jdurham.broadcast;

import com.jdurham.*;

public class BroadcastOkHandler implements NodeHandler<
        BroadcastOkHandler.BroadcastOkRequest,
        BroadcastOkHandler.BroadcastOkResponse> {

    private final BroadcastMessageTracker messageTracker;

    public BroadcastOkHandler(BroadcastMessageTracker messageTracker) {
        this.messageTracker = messageTracker;
    }

    public static class BroadcastOkRequest extends Message {
        public BroadcastOkRequest(int msgId, int inReplyTo) {
            super("broadcast_ok", msgId, inReplyTo);
        }

        public BroadcastOkRequest() {
        }
    }

    public static class BroadcastOkResponse extends Message {
        public BroadcastOkResponse(String type, int msgId, int inReplyTo) {
            super(type, msgId, inReplyTo);
        }

        public BroadcastOkResponse() {
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
        System.err.printf("Received OK for %s from %s\n", request.inReplyTo, messageContext.src());

        messageTracker.remove(request.inReplyTo, messageContext.src());
        return null;
    }
}
