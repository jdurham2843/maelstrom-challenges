package com.jdurham;

import com.jdurham.broadcast.*;
import com.jdurham.broadcast.bulk.BulkBroadcastHandler;
import com.jdurham.broadcast.bulk.BulkBroadcastOkHandler;

public class Main {
    public static void main(String[] args) {
        final NodeMetadataStore nodeMetadataStore = new NodeMetadataStore();
        final Node node = new Node(nodeMetadataStore);

        final MessageStore messageStore = new MessageStore();
        final BroadcastMessageTracker messageTracker = new BroadcastMessageTracker();
        final BroadcastHandler broadcastHandler = new BroadcastHandler(messageStore, nodeMetadataStore, messageTracker);
        final BroadcastOkHandler broadcastOkHandler = new BroadcastOkHandler(messageTracker);

        node.registerHandler("echo", new OkHandler());
        node.registerHandler("generate", new GenerateIdHandler());
        node.registerHandler("broadcast", new BroadcastHandler(messageStore, nodeMetadataStore, messageTracker));
        node.registerHandler("broadcast_ok", broadcastOkHandler);
        node.registerHandler("bulk_broadcast", new BulkBroadcastHandler(broadcastHandler));
        node.registerHandler("bulk_broadcast_ok", new BulkBroadcastOkHandler(broadcastOkHandler));
        node.registerHandler("read", new BroadcastReadHandler(messageStore));
        node.registerHandler("topology", new TopologyHandler(nodeMetadataStore));

        node.main();
    }

}