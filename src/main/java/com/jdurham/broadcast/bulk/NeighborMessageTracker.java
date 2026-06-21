package com.jdurham.broadcast.bulk;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NeighborMessageTracker {
    private final Map<String, Set<Integer>> neighborStores = new ConcurrentHashMap<>();
    private final Map<String, Set<Integer>> pendingMessages = new ConcurrentHashMap<>();

    public void track(String neighborId, int message) {
        if (!neighborStores.containsKey(neighborId) || !neighborStores.computeIfAbsent(neighborId, k -> ConcurrentHashMap.newKeySet()).contains(message)) {
            pendingMessages.computeIfAbsent(neighborId, k -> ConcurrentHashMap.newKeySet()).add(message);
        }
    }

    public Set<Integer> takePendingMessages(String neighborId) {
        final Set<Integer> messages = pendingMessages.put(neighborId, ConcurrentHashMap.newKeySet());

        return messages != null ? messages : Set.of();
    }

    public void resolve(String neighborId, Collection<Integer> messages) {
        pendingMessages.computeIfAbsent(neighborId, k -> ConcurrentHashMap.newKeySet()).removeAll(messages);
        neighborStores.computeIfAbsent(neighborId, k -> ConcurrentHashMap.newKeySet()).addAll(messages);
    }
}
