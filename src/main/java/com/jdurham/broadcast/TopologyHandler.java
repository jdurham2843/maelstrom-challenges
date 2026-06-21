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
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        for (int i = 0; i < nodeIds.size(); i++) {
            if (i == 0) {
                topology.put("n0", IntStream.range(1, nodeIds.size())
                        .mapToObj(index -> "n" + index)
                        .collect(Collectors.toList()));
            } else {
                topology.put("n" + i, List.of("n0"));
            }
        }

        return topology;
    }
}
