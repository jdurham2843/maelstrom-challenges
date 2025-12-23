package com.jdurham.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.*;

import java.util.Collection;

public class BroadcastReadHandler implements NodeHandler<
        BroadcastReadHandler.ReadRequest,
        BroadcastReadHandler.ReadResponse> {

    private final MessageStore messageStore;

    public BroadcastReadHandler(MessageStore messageStore) {
        this.messageStore = messageStore;
    }

    public static class ReadRequest extends Message {
    }

    public static class ReadResponse extends Message {
        @JsonProperty
        public Collection<Integer> messages;

        public ReadResponse(int msgId, int inReplyTo, Collection<Integer> messages) {
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
    public ReadResponse handle(MessageContext messageContext, ReadRequest request) {
        return new ReadResponse(request.msgId, request.msgId, messageStore.getAllMessages());
    }
}
