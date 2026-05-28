package com.epicode.taskmanager.tasks;

import java.time.LocalDate;
import java.util.Objects;

public final class TaskBuilder {
    private String id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private boolean completed;

    public TaskBuilder id(String id) {
        this.id = id;
        return this;
    }

    public TaskBuilder title(String title) {
        this.title = title;
        return this;
    }

    public TaskBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder priority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public TaskBuilder dueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TaskBuilder completed(boolean completed) {
        this.completed = completed;
        return this;
    }

    public Task build() {
        final Task task = new Task(
                Objects.requireNonNull(id, "id cannot be null"),
                Objects.requireNonNull(title, "title cannot be null"),
                Objects.requireNonNull(description, "description cannot be null"),
                Objects.requireNonNull(priority, "priority cannot be null"),
                Objects.requireNonNull(dueDate, "dueDate cannot be null")
        );
        if (completed) {
            task.markCompleted();
        }
        return task;
    }
}
