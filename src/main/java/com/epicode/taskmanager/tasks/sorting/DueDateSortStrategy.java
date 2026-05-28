package com.epicode.taskmanager.tasks.sorting;

import com.epicode.taskmanager.tasks.Task;

import java.util.Comparator;

public final class DueDateSortStrategy implements TaskSortStrategy {
    @Override
    public Comparator<Task> comparator() {
        return Comparator.comparing(Task::getDueDate).thenComparing(Task::getTitle);
    }
}
