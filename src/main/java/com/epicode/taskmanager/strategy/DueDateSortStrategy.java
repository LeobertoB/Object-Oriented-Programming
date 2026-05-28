package com.epicode.taskmanager.strategy;

import com.epicode.taskmanager.domain.Task;

import java.util.Comparator;

public final class DueDateSortStrategy implements TaskSortStrategy {
    @Override
    public Comparator<Task> comparator() {
        return Comparator.comparing(Task::getDueDate).thenComparing(Task::getTitle);
    }
}
