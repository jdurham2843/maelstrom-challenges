package com.jdurham;

import java.util.concurrent.atomic.AtomicInteger;

public class MsgIdGenerator {
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static int getNextId() {
        return counter.incrementAndGet();
    }
}
