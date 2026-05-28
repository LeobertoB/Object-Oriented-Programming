package com.epicode.taskmanager.event;

@FunctionalInterface
public interface TaskEventListener {
    void onEvent(TaskEvent event);
}
