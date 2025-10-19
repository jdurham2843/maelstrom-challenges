package com.jdurham.broadcast;

import com.jdurham.client.MaelstromRequest;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BroadcastMessageTracker {
    private final Map<String, MaelstromRequest> trackedRequests = new ConcurrentHashMap<>();

    void track(String dest, MaelstromRequest request) {
        trackedRequests.put(request.msgId() + ":" + dest, request);
    }

    Collection<MaelstromRequest> getAll() {
        return trackedRequests.values();
    }

    void remove(int msgId, String dest) {
        trackedRequests.remove(msgId + ":" + dest);
    }
}
