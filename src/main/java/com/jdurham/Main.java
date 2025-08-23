package com.jdurham;

import com.jdurham.broadcast.BroadcastHandler;
import com.jdurham.broadcast.BroadcastReadHandler;
import com.jdurham.broadcast.MessageStore;
import com.jdurham.broadcast.TopologyHandler;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final NodeMetadataStore nodeMetadataStore = new NodeMetadataStore();
        final Node node = new Node(nodeMetadataStore);

        final MessageStore messageStore = new MessageStore();

        node.registerHandler("echo", new OkHandler());
        node.registerHandler("generate", new GenerateIdHandler());
        node.registerHandler("broadcast", new BroadcastHandler(messageStore, nodeMetadataStore));
        node.registerHandler("read", new BroadcastReadHandler(messageStore));
        node.registerHandler("topology", new TopologyHandler(nodeMetadataStore));

        node.main();
    }

}