package com.jdurham.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.Message;
import com.jdurham.MessageContext;
import com.jdurham.NodeHandler;
import com.jdurham.NodeMetadataStore;
import com.jdurham.broadcast.bulk.BroadcastManager;

import java.util.List;
import java.util.Map;

public class TopologyHandler implements NodeHandler<
        TopologyHandler.TopologyRequest,
        TopologyHandler.TopologyResponse> {

    private final NodeMetadataStore nodeMetadataStore;
    private final BroadcastManager broadcastManager;

    public TopologyHandler(NodeMetadataStore nodeMetadataStore, BroadcastManager broadcastManager) {
        this.nodeMetadataStore = nodeMetadataStore;
        this.broadcastManager = broadcastManager;
    }

    public static class TopologyRequest extends Message {
        @JsonProperty
        Map<String, List<String>> topology;
    }

    public static class TopologyResponse extends Message {
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

        nodeMetadataStore.topology.get(nodeMetadataStore.nodeId).forEach(broadcastManager::startBroadcaster);

        System.err.println("Observed the following topology: " + nodeMetadataStore.topology);

        return new TopologyResponse(request.msgId, request.msgId);
    }
}
