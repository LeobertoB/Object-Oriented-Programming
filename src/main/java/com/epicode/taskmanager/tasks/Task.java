package com.epicode.taskmanager.tasks;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public final class Task implements TaskComponent {
    private final String id;
    private final String title;
    private final String description;
    private final Priority priority;
    private final LocalDate dueDate;
    private boolean completed;

    public Task(String id, String title, String description, Priority priority, LocalDate dueDate) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.title = Objects.requireNonNull(title, "title cannot be null");
        this.description = Objects.requireNonNull(description, "description cannot be null");
        this.priority = Objects.requireNonNull(priority, "priority cannot be null");
        this.dueDate = Objects.requireNonNull(dueDate, "dueDate cannot be null");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    @Override
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void markCompleted() {
        completed = true;
    }

    @Override
    public int countTotalTasks() {
        return 1;
    }

    @Override
    public int countCompletedTasks() {
        return completed ? 1 : 0;
    }

    @Override
    public List<TaskComponent> getChildren() {
        return List.of();
    }
}
