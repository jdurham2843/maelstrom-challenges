package com.jdurham.broadcast.bulk;

import com.jdurham.Message;
import com.jdurham.MessageContext;
import com.jdurham.NodeHandler;

public class BulkBroadcastOkHandler implements NodeHandler<
        BulkBroadcastOkHandler.BroadcastOkRequest,
        BulkBroadcastOkHandler.BroadcastOkResponse> {

    private final BroadcastManager broadcastManager;


    public BulkBroadcastOkHandler(BroadcastManager broadcastManager) {
        this.broadcastManager = broadcastManager;
    }

    public static class BroadcastOkRequest extends Message {
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
        broadcastManager.logMsgReceived(messageContext.src(), request.inReplyTo);
        return null;
    }
}
