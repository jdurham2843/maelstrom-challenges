package com.jdurham.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.NodeHandler;
import com.jdurham.Request;
import com.jdurham.Response;

import java.util.Set;

public class BroadcastReadHandler implements NodeHandler<
        BroadcastReadHandler.ReadRequest,
        BroadcastReadHandler.ReadResponse> {

    private final MessageStore messageStore;

    public BroadcastReadHandler(MessageStore messageStore) {
        this.messageStore = messageStore;
    }

    public static class ReadRequest extends Request {
    }

    public static class ReadResponse extends Response {
        @JsonProperty
        public Set<Integer> messages;

        public ReadResponse(int msgId, int inReplyTo, Set<Integer> messages) {
            super("read_ok", msgId, inReplyTo);
            this.messages = messages;
        }
    }
    @Override
    public Class<ReadRequest> getRequestType() {
        return ReadRequest.class;
    }

    @Override
    public Class<ReadResponse> getResponseType() {
        return ReadResponse.class;
    }

    @Override
    public ReadResponse handle(ReadRequest request) {
        return new ReadResponse(request.msgId, request.msgId, messageStore.messages);
    }
}
