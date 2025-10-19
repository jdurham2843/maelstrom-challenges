package com.jdurham;

import com.jdurham.broadcast.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final NodeMetadataStore nodeMetadataStore = new NodeMetadataStore();
        final Node node = new Node(nodeMetadataStore);

        final MessageStore messageStore = new MessageStore();
        final BroadcastMessageTracker messageTracker = new BroadcastMessageTracker();

        node.registerHandler("echo", new OkHandler());
        node.registerHandler("generate", new GenerateIdHandler());
        node.registerHandler("broadcast", new BroadcastHandler(messageStore, nodeMetadataStore, messageTracker));
        node.registerHandler("broadcast_ok", new BroadcastOkHandler(messageTracker));
        node.registerHandler("read", new BroadcastReadHandler(messageStore));
        node.registerHandler("topology", new TopologyHandler(nodeMetadataStore));

        node.main();
    }

}