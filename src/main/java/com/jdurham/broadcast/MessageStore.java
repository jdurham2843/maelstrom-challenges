package com.jdurham.broadcast;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MessageStore {
    private final Set<Integer> messages = ConcurrentHashMap.newKeySet();

    boolean contains(int message) {
        return messages.contains(message);
    }

    void add(int message) {
        messages.add(message);
    }

    Collection<Integer> getAllMessages() {
        return messages;
    }

}
