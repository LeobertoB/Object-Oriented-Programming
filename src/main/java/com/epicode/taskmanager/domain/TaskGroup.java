package com.epicode.taskmanager.domain;

import com.epicode.taskmanager.iterator.TaskComponentIterator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class TaskGroup implements TaskComponent, Iterable<TaskComponent> {
    private final String id;
    private final String title;
    private final String description;
    private final List<TaskComponent> children = new ArrayList<>();

    public TaskGroup(String id, String title, String description) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.title = Objects.requireNonNull(title, "title cannot be null");
        this.description = Objects.requireNonNull(description, "description cannot be null");
    }

    public void add(TaskComponent component) {
        children.add(Objects.requireNonNull(component, "component cannot be null"));
    }

    public boolean removeById(String componentId) {
        Objects.requireNonNull(componentId, "componentId cannot be null");
        return children.removeIf(component -> component.getId().equals(componentId));
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

    @Override
    public LocalDate getDueDate() {
        return children.stream()
                .map(TaskComponent::getDueDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);
    }

    @Override
    public boolean isCompleted() {
        return !children.isEmpty() && countTotalTasks() == countCompletedTasks();
    }

    @Override
    public void markCompleted() {
        children.forEach(TaskComponent::markCompleted);
    }

    @Override
    public int countTotalTasks() {
        return children.stream()
                .mapToInt(TaskComponent::countTotalTasks)
                .sum();
    }

    @Override
    public int countCompletedTasks() {
        return children.stream()
                .mapToInt(TaskComponent::countCompletedTasks)
                .sum();
    }

    @Override
    public List<TaskComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public Iterator<TaskComponent> iterator() {
        return new TaskComponentIterator(getChildren());
    }
}
