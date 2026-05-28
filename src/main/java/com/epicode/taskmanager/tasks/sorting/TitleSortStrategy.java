package com.epicode.taskmanager.tasks.sorting;

import com.epicode.taskmanager.tasks.Task;

import java.util.Comparator;

public final class TitleSortStrategy implements TaskSortStrategy {
    @Override
    public Comparator<Task> comparator() {
        return Comparator.comparing(Task::getTitle, String.CASE_INSENSITIVE_ORDER);
    }
}
