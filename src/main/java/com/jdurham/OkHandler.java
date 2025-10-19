package com.jdurham;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OkHandler implements NodeHandler<OkHandler.OkRequest, OkHandler.OkResponse> {
    public static class OkRequest extends Request {
        @JsonProperty
        String echo;
    }

    public static class OkResponse extends Response {
        @JsonProperty
        String echo;
        public OkResponse(int msgId, int inReplyTo, String echo) {
            super("echo_ok", msgId, inReplyTo);
            this.echo = echo;
        }
    }

    @Override
    public Class<OkRequest> getRequestType() {
        return OkRequest.class;
    }

    @Override
    public Class<OkResponse> getResponseType() {
        return OkResponse.class;
    }

    @Override
    public OkResponse handle(MessageContext messageContext, OkRequest request) {
        return new OkResponse(request.msgId, request.msgId, request.echo);
    }
}
