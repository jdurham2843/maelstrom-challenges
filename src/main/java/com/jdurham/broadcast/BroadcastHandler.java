package com.jdurham.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.NodeHandler;
import com.jdurham.Request;
import com.jdurham.Response;

public class BroadcastHandler implements NodeHandler<
        BroadcastHandler.BroadcastRequest,
        BroadcastHandler.BroadcastResponse> {

    private final MessageStore messageStore;
    private final TopologyStore topologyStore;

    public BroadcastHandler(MessageStore messageStore, TopologyStore topologyStore) {
        this.messageStore = messageStore;
        this.topologyStore = topologyStore;
    }

    public static class BroadcastRequest extends Request {
        @JsonProperty
        int message;
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
    public BroadcastResponse handle(BroadcastRequest request) {
        messageStore.messages.add(request.message);

        return new BroadcastResponse(request.msgId, request.msgId);
    }
}
