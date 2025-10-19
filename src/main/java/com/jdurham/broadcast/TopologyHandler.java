package com.jdurham.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.*;

import java.util.List;
import java.util.Map;

public class TopologyHandler implements NodeHandler<
        TopologyHandler.TopologyRequest,
        TopologyHandler.TopologyResponse> {

    private final NodeMetadataStore nodeMetadataStore;

    public TopologyHandler(NodeMetadataStore nodeMetadataStore) {
        this.nodeMetadataStore = nodeMetadataStore;
    }

    public static class TopologyRequest extends Request {
        @JsonProperty
        Map<String, List<String>> topology;
    }

    public static class TopologyResponse extends Response {
        public TopologyResponse(int msgId, int inReplyTo) {
            super("topology_ok", msgId, inReplyTo);
        }
    }

    @Override
    public Class<TopologyRequest> getRequestType() {
        return TopologyRequest.class;
    }

    @Override
    public Class<TopologyResponse> getResponseType() {
        return TopologyResponse.class;
    }

    @Override
    public TopologyResponse handle(MessageContext messageContext, TopologyRequest request) {
        nodeMetadataStore.topology = request.topology;

        return new TopologyResponse(request.msgId, request.msgId);
    }
}
