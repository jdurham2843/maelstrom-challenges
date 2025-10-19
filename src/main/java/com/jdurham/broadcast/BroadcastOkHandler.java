package com.jdurham.broadcast;

import com.jdurham.MessageContext;
import com.jdurham.NodeHandler;
import com.jdurham.Request;
import com.jdurham.Response;

public class BroadcastOkHandler implements NodeHandler<
        BroadcastOkHandler.BroadcastOkRequest,
        BroadcastOkHandler.BroadcastOkResponse> {

    private final BroadcastMessageTracker messageTracker;

    public BroadcastOkHandler(BroadcastMessageTracker messageTracker) {
        this.messageTracker = messageTracker;
    }

    public static class BroadcastOkRequest extends Request {
    }

    public static class BroadcastOkResponse extends Response {
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
        System.err.printf("Received broadcast ok for %s from node: %s%n", request.msgId, messageContext.src());
        messageTracker.remove(request.msgId, messageContext.src());
        return null;
    }
}
