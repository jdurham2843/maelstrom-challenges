package com.jdurham;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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
        final String id = generateId();

        return new GenerateIdResponse(request.msgId, request.msgId, id);
    }

    private String generateId() {
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);

        return bytesToHex(randomBytes);
    }

    private String bytesToHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
