package com.jdurham.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdurham.NodeHandler;
import com.jdurham.NodeMetadataStore;
import com.jdurham.Request;
import com.jdurham.Response;
import com.jdurham.client.MaelstromClient;
import com.jdurham.client.MaelstromRequest;

import java.util.List;

public class BroadcastHandler implements NodeHandler<
        BroadcastHandler.BroadcastRequest,
        BroadcastHandler.BroadcastResponse> {

    private final MessageStore messageStore;
    private final NodeMetadataStore nodeMetadataStore;
    private final MaelstromClient maelstromClient =  new MaelstromClient();

    public BroadcastHandler(MessageStore messageStore, NodeMetadataStore nodeMetadataStore) {
        this.messageStore = messageStore;
        this.nodeMetadataStore = nodeMetadataStore;
    }

    public static class BroadcastRequest extends Request {
        @JsonProperty
        int message;
    }

    public static class BroadcastResponse extends Response {
        public BroadcastResponse(int msgId, int inReplyTo) {
            super("broadcast_ok", msgId, inReplyTo);
        }
    }
    @Override
    public Class<BroadcastRequest> getRequestType() {
        return BroadcastRequest.class;
    }

    @Override
    public Class<BroadcastResponse> getResponseType() {
        return BroadcastResponse.class;
    }

    @Override
    public BroadcastResponse handle(BroadcastRequest request) {
        if (!messageStore.messages.contains(request.message)) {
            messageStore.messages.add(request.message);

            // broadcast
            final List<String> neighbors = nodeMetadataStore.topology.get(nodeMetadataStore.nodeId);
            neighbors.stream()
                    .map(neighbor -> new MaelstromRequest(nodeMetadataStore.nodeId, neighbor, request))
                    .forEach(maelstromClient::send);
        }

        return new BroadcastResponse(request.msgId, request.msgId);
    }
}
