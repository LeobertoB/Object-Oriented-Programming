package com.epicode.taskmanager.event;

public record TaskEvent(TaskEventType type, String targetId, String message) {
}
