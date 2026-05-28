package com.epicode.taskmanager.tasks.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TaskEventPublisher {
    private final List<TaskEventListener> listeners = new ArrayList<>();

    public void addListener(TaskEventListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener cannot be null"));
    }

    public boolean removeListener(TaskEventListener listener) {
        return listeners.remove(listener);
    }

    public void publish(TaskEvent event) {
        Objects.requireNonNull(event, "event cannot be null");
        List.copyOf(listeners).forEach(listener -> listener.onEvent(event));
    }
}
