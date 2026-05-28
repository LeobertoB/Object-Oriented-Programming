package com.epicode.taskmanager.strategy;

import com.epicode.taskmanager.domain.Task;

import java.util.Comparator;

public final class PrioritySortStrategy implements TaskSortStrategy {
    @Override
    public Comparator<Task> comparator() {
        return Comparator.comparing(Task::getPriority).reversed()
                .thenComparing(Task::getDueDate)
                .thenComparing(Task::getTitle);
    }
}
