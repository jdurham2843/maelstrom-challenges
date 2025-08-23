package com.jdurham.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MaelstromClient {

    private final ObjectMapper mapper = new ObjectMapper();

    public void send(MaelstromRequest maelstromRequest) {
        final ObjectNode outputResponse = mapper.createObjectNode();
        outputResponse.put("src", maelstromRequest.src());
        outputResponse.put("dest", maelstromRequest.dest());
        outputResponse.putPOJO("body", maelstromRequest.request());

        try {
            final String requestPayload = mapper.writeValueAsString(outputResponse);
            System.err.println("Attempting to send " + requestPayload);
            System.out.println(requestPayload);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to send request of type: " + maelstromRequest.request().type);
        }
    }
}
