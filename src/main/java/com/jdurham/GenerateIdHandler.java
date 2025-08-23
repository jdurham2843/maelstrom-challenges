package com.jdurham;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class GenerateIdHandler implements NodeHandler<
        GenerateIdHandler.GenerateIdRequest,
        GenerateIdHandler.GenerateIdResponse> {

    public static class GenerateIdRequest extends Request {

    }

    public static class GenerateIdResponse extends Response {
        @JsonProperty
        String id;

        public GenerateIdResponse(int msgId, int inReplyTo, String id) {
            super("generate_ok", msgId, inReplyTo);
            this.id = id;
        }
    }

    @Override
    public Class<GenerateIdRequest> getRequestType() {
        return GenerateIdRequest.class;
    }

    @Override
    public Class<GenerateIdResponse> getResponseType() {
        return GenerateIdResponse.class;
    }

    @Override
    public GenerateIdResponse handle(GenerateIdRequest request) {
        final String id = UUID.randomUUID().toString();

        return new GenerateIdResponse(request.msgId, request.msgId, id);
    }
}
