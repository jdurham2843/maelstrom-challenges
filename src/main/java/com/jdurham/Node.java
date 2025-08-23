package com.jdurham;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    private final ObjectMapper mapper = new ObjectMapper();

    private final NodeMetadataStore nodeMetadataStore;

    private final Map<String, NodeHandler<? extends Request, ? extends Response>> handlers = new HashMap<>();

    static class InitHandler implements NodeHandler<InitHandler.InitRequest, InitHandler.InitResponse> {

        private final NodeMetadataStore nodeMetadataStore;

        public InitHandler(NodeMetadataStore nodeMetadataStore) {
            this.nodeMetadataStore = nodeMetadataStore;
        }

        @Override
        public Class<InitRequest> getRequestType() {
            return InitRequest.class;
        }

        @Override
        public Class<InitResponse> getResponseType() {
            return InitResponse.class;
        }

        @Override
        public InitResponse handle(InitRequest request) {
            this.nodeMetadataStore.nodeId = request.nodeId;
            this.nodeMetadataStore.nodeIds = request.nodeIds;
            return new InitResponse("init_ok", request.msgId, request.msgId);
        }

        public static class InitRequest extends Request {
            @JsonProperty
            @JsonAlias(value = "node_id")
            String nodeId;
            @JsonProperty
            @JsonAlias(value = "node_ids")
            List<String> nodeIds;
        }

        static class InitResponse extends Response {
            public InitResponse(String type, int msgId, int inReplyTo) {
                super(type, msgId, inReplyTo);
            }
        }

    }

    public Node(NodeMetadataStore nodeMetadataStore) {
        this.nodeMetadataStore = nodeMetadataStore;
        registerHandler("init", new InitHandler(nodeMetadataStore));
    }

    <T extends Request, R extends Response> void registerHandler(String type, NodeHandler<T, R> handler) {
        handlers.put(type, handler);
    }

    JsonNode dispatchToHandler(JsonNode requestJsonNode) throws JsonProcessingException {
        final String type = requestJsonNode.get("type").asText();

        final NodeHandler<? extends Request, ? extends Response> handler = handlers.get(type);

        return doDispatch(handler, requestJsonNode);
    }

    private <T extends Request, R extends Response> JsonNode doDispatch(NodeHandler<T, R> handler, JsonNode requestJsonNode) throws JsonProcessingException {
        final T request = mapper.treeToValue(requestJsonNode, handler.getRequestType());
        final R response = handler.handle(request);

        return mapper.valueToTree(response);
    }

    // main
    void main() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // handle
        while (true) {
            try {
                final String input = br.readLine();
                System.err.println("read message! " + input);
                final JsonNode inputJsonNode = mapper.readTree(input);
                final String src = inputJsonNode.get("src").asText();
                final String dest = inputJsonNode.get("dest").asText();
                final JsonNode requestBody = inputJsonNode.get("body");

                final JsonNode responseBody = dispatchToHandler(requestBody);

                final ObjectNode outputResponse = mapper.createObjectNode();
                outputResponse.put("src", dest);
                outputResponse.put("dest", src);
                outputResponse.put("body", responseBody);

                final String responseJson = mapper.writeValueAsString(outputResponse);
                System.err.println("responding with " + outputResponse);
                System.out.println(responseJson);
            } catch (Exception e) {
                System.err.println("Failed to read input: " + e.getMessage());
            }
        }
    }
}
