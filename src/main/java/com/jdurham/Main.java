package com.jdurham;

import com.jdurham.broadcast.*;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        final NodeMetadataStore nodeMetadataStore = new NodeMetadataStore();
        final Node node = new Node(nodeMetadataStore);

        final MessageStore messageStore = new MessageStore();

        node.registerHandler("broadcast", new BroadcastHandler(messageStore, nodeMetadataStore));
        node.registerHandler("read", new BroadcastReadHandler(messageStore));
        node.registerHandler("topology", new TopologyHandler(nodeMetadataStore));

        node.main();
    }
}