package com.jdurham.broadcast;

import java.util.*;

public class MessageStore {
    private final List<Integer> messages = new ArrayList<>();

    boolean contains(int message) {
        return messages.contains(message);
    }

    void add(int message) {
        messages.add(message);
    }

    Collection<Integer> getAllMessages() {
        return messages.stream().sorted().toList();
    }

}
