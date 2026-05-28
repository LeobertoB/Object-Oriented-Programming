package com.epicode.taskmanager.strategy;

import com.epicode.taskmanager.domain.Task;

import java.util.Comparator;

public final class TitleSortStrategy implements TaskSortStrategy {
    @Override
    public Comparator<Task> comparator() {
        return Comparator.comparing(Task::getTitle, String.CASE_INSENSITIVE_ORDER);
    }
}
