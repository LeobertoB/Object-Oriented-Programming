package com.epicode.taskmanager.tasks.creation;

import java.util.concurrent.atomic.AtomicLong;

public final class SequentialIdGenerator implements IdGenerator {
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public String nextId(String prefix) {
        return prefix + "-" + sequence.incrementAndGet();
    }
}
