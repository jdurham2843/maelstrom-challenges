package com.jdurham.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.Message;
import com.jdurham.MessageContext;
import com.jdurham.NodeHandler;
import com.jdurham.NodeMetadataStore;
import com.jdurham.broadcast.bulk.BroadcastManager;

import java.util.ArrayList;
import java.util.HashMap;
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
        nodeMetadataStore.topology = constructTopology(nodeMetadataStore.nodeIds);

        nodeMetadataStore.topology.get(nodeMetadataStore.nodeId).forEach(broadcastManager::startBroadcaster);

        System.err.println("Observed the following topology: " + request.topology);
        System.err.println("Constructed topology: " + nodeMetadataStore.topology);

        return new TopologyResponse(request.msgId, request.msgId);
    }

    static Map<String, List<String>> constructTopology(List<String> nodeIds) {
        Map<String, List<String>> topology = new HashMap<>(nodeIds.size());
        nodeIds.forEach(id -> {
            List<String> neighbors = new ArrayList<>();
            topology.put(id, neighbors);

            int current = Integer.parseInt(id.split("n")[1]);
            // up
            if (current - 5 >= 0) {
                neighbors.add("n" + (current - 5));
            }
            // down
            if (current + 5 <= nodeIds.size() - 1) {
                neighbors.add("n" + (current + 5));
            }
            // left
            if (current - 1 >= 0 && (current - 1) % 5 < current % 5) {
                neighbors.add("n" + (current - 1));
            }
            // right
            if (current + 1 <= nodeIds.size() - 1 && (current + 1) % 5 > current % 5) {
                neighbors.add("n" + (current + 1));
            }
        });

        return topology;
    }
}
