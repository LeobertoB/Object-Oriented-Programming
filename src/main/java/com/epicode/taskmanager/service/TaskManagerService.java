package com.epicode.taskmanager.service;

import com.epicode.taskmanager.domain.Priority;
import com.epicode.taskmanager.domain.Task;
import com.epicode.taskmanager.domain.TaskComponent;
import com.epicode.taskmanager.domain.TaskGroup;
import com.epicode.taskmanager.event.TaskEvent;
import com.epicode.taskmanager.event.TaskEventListener;
import com.epicode.taskmanager.event.TaskEventPublisher;
import com.epicode.taskmanager.event.TaskEventType;
import com.epicode.taskmanager.factory.TaskComponentFactory;
import com.epicode.taskmanager.persistence.TaskRepository;
import com.epicode.taskmanager.security.exception.ValidationException;
import com.epicode.taskmanager.strategy.TaskSortStrategy;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

public final class TaskManagerService {
    private final TaskComponentFactory factory;
    private final TaskRepository repository;
    private final TaskEventPublisher eventPublisher = new TaskEventPublisher();
    private TaskGroup root;

    public TaskManagerService(TaskComponentFactory factory, TaskRepository repository) {
        this.factory = Objects.requireNonNull(factory, "factory cannot be null");
        this.repository = Objects.requireNonNull(repository, "repository cannot be null");
        this.root = factory.createGroup("Main Project", "Default task project");
    }

    public void addEventListener(TaskEventListener listener) {
        eventPublisher.addListener(listener);
    }

    public boolean removeEventListener(TaskEventListener listener) {
        return eventPublisher.removeListener(listener);
    }

    public TaskGroup getRoot() {
        return root;
    }

    public TaskGroup createGroup(String parentId, String title, String description) {
        TaskGroup parent = findGroup(parentId)
                .orElseThrow(() -> new ValidationException("Parent group was not found."));
        TaskGroup group = factory.createGroup(title, description);
        parent.add(group);
        publish(TaskEventType.GROUP_CREATED, group.getId(), "Group created.");
        return group;
    }

    public Task createTask(String parentId, String title, String description, Priority priority, LocalDate dueDate) {
        TaskGroup parent = findGroup(parentId)
                .orElseThrow(() -> new ValidationException("Parent group was not found."));
        Task task = factory.createTask(title, description, priority, dueDate);
        parent.add(task);
        publish(TaskEventType.TASK_CREATED, task.getId(), "Task created.");
        return task;
    }

    public void markCompleted(String componentId) {
        TaskComponent component = findComponent(componentId)
                .orElseThrow(() -> new ValidationException("Task or group was not found."));
        component.markCompleted();
        publish(TaskEventType.COMPONENT_COMPLETED, componentId, "Task component completed.");
    }

    public List<TaskComponent> listComponents() {
        List<TaskComponent> components = new ArrayList<>();
        components.add(root);
        for (TaskComponent component : root) {
            components.add(component);
        }
        return List.copyOf(components);
    }

    public List<String> listLines() {
        List<String> lines = new ArrayList<>();
        appendLine(lines, root, 0);
        return List.copyOf(lines);
    }

    public List<Task> listTasks(TaskSortStrategy sortStrategy) {
        Objects.requireNonNull(sortStrategy, "sortStrategy cannot be null");
        return StreamSupport.stream(root.spliterator(), false)
                .filter(Task.class::isInstance)
                .map(Task.class::cast)
                .sorted(sortStrategy.comparator())
                .toList();
    }

    public List<Task> listOpenTasksDueBefore(LocalDate limitDate) {
        Objects.requireNonNull(limitDate, "limitDate cannot be null");
        return StreamSupport.stream(root.spliterator(), false)
                .filter(Task.class::isInstance)
                .map(Task.class::cast)
                .filter(task -> !task.isCompleted())
                .filter(task -> !task.getDueDate().isAfter(limitDate))
                .toList();
    }

    public TaskSummary summarizeTasks() {
        List<Task> tasks = StreamSupport.stream(root.spliterator(), false)
                .filter(Task.class::isInstance)
                .map(Task.class::cast)
                .toList();
        int completed = (int) tasks.stream()
                .filter(Task::isCompleted)
                .count();
        return new TaskSummary(tasks.size(), completed, tasks.size() - completed);
    }

    public void save(Path filePath) {
        repository.save(root, filePath);
        publish(TaskEventType.PROJECT_SAVED, root.getId(), "Project saved.");
    }

    public void load(Path filePath) {
        root = repository.load(filePath);
        publish(TaskEventType.PROJECT_LOADED, root.getId(), "Project loaded.");
    }

    public Optional<TaskComponent> findComponent(String componentId) {
        if (root.getId().equals(componentId)) {
            return Optional.of(root);
        }
        for (TaskComponent component : root) {
            if (component.getId().equals(componentId)) {
                return Optional.of(component);
            }
        }
        return Optional.empty();
    }

    private Optional<TaskGroup> findGroup(String groupId) {
        return findComponent(groupId)
                .filter(TaskGroup.class::isInstance)
                .map(TaskGroup.class::cast);
    }

    private static void appendLine(List<String> lines, TaskComponent component, int depth) {
        String indent = "  ".repeat(depth);
        String status = component.isCompleted() ? "DONE" : "OPEN";
        lines.add(indent + "- [" + status + "] " + component.getId() + " | " + component.getTitle());
        for (TaskComponent child : component.getChildren()) {
            appendLine(lines, child, depth + 1);
        }
    }

    private void publish(TaskEventType type, String targetId, String message) {
        eventPublisher.publish(new TaskEvent(type, targetId, message));
    }
}
