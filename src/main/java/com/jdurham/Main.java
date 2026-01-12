package com.jdurham;

import com.jdurham.broadcast.BroadcastHandler;
import com.jdurham.broadcast.BroadcastReadHandler;
import com.jdurham.broadcast.MessageStore;
import com.jdurham.broadcast.TopologyHandler;
import com.jdurham.broadcast.bulk.BroadcastManager;
import com.jdurham.broadcast.bulk.BulkBroadcastHandler;
import com.jdurham.broadcast.bulk.BulkBroadcastOkHandler;
import com.jdurham.broadcast.bulk.NeighborMessageTracker;
import com.jdurham.client.MaelstromClient;

public class Main {
    public static void main(String[] args) {
        final NodeMetadataStore nodeMetadataStore = new NodeMetadataStore();
        final Node node = new Node(nodeMetadataStore);

        final MessageStore messageStore = new MessageStore();
        final NeighborMessageTracker neighborMessageTracker = new NeighborMessageTracker();
        final BroadcastManager broadcastManager = new BroadcastManager(neighborMessageTracker, nodeMetadataStore, new MaelstromClient());

        final BroadcastHandler broadcastHandler = new BroadcastHandler(messageStore, nodeMetadataStore, neighborMessageTracker);

        node.registerHandler("echo", new OkHandler());
        node.registerHandler("generate", new GenerateIdHandler());
        node.registerHandler("broadcast", broadcastHandler);
        node.registerHandler("bulk_broadcast", new BulkBroadcastHandler(broadcastHandler, neighborMessageTracker));
        node.registerHandler("bulk_broadcast_ok", new BulkBroadcastOkHandler(broadcastManager));
        node.registerHandler("read", new BroadcastReadHandler(messageStore));
        node.registerHandler("topology", new TopologyHandler(nodeMetadataStore, broadcastManager));

        node.main();
    }

}