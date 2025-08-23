package com.jdurham;

import com.jdurham.broadcast.*;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        final Node node = new Node();

        final MessageStore messageStore = new MessageStore();
        final TopologyStore topologyStore = new TopologyStore();

        node.registerHandler("broadcast", new BroadcastHandler(messageStore, topologyStore));
        node.registerHandler("read", new BroadcastReadHandler(messageStore));
        node.registerHandler("topology", new TopologyHandler(topologyStore));

        node.main();
    }
}