package com.jdurham.broadcast;

import com.jdurham.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BroadcastMessageTracker {
    private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Message>> trackedRequests = new ConcurrentHashMap<>();

    void track(String dest, Message request) {
        trackedRequests.computeIfAbsent(dest, d -> new ConcurrentHashMap<>()).put(request.msgId, request);
    }

    Set<Map.Entry<String, ConcurrentHashMap<Integer, Message>>> getAll() {
        return trackedRequests.entrySet();
    }

    Set<Map.Entry<String, ConcurrentHashMap<Integer, Message>>> takeAll() {
        final Map<String, ConcurrentHashMap<Integer, Message>> copy = new HashMap<>(this.trackedRequests);
        trackedRequests.clear();

        return copy.entrySet();
    }

    void remove(int msgId, String dest) {
        trackedRequests.get(dest).remove(msgId);
    }
}
