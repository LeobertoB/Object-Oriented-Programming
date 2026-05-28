package com.epicode.taskmanager.tasks.events;

@FunctionalInterface
public interface TaskEventListener {
    void onEvent(TaskEvent event);
}
