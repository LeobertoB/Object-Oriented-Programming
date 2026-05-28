package com.epicode.taskmanager.tasks.events;

public record TaskEvent(TaskEventType type, String targetId, String message) {
}
