package com.jdurham.broadcast;

import com.jdurham.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BroadcastMessageTracker {
    private final ConcurrentHashMap<String, Set<Integer>> trackedRequests = new ConcurrentHashMap<>();

    void track(String dest, int msgId) {
        trackedRequests.computeIfAbsent(dest, d -> ConcurrentHashMap.newKeySet()).add(msgId);
    }

    boolean contains(String neighbor, int msgId) {
        return trackedRequests.getOrDefault(neighbor, ConcurrentHashMap.newKeySet()).contains(msgId);
    }

    void remove(String dest, int msgId) {
        trackedRequests.getOrDefault(dest, ConcurrentHashMap.newKeySet()).remove(msgId);
    }
}
