package com.epicode.taskmanager.strategy;

import com.epicode.taskmanager.domain.Task;

import java.util.Comparator;

public interface TaskSortStrategy {
    Comparator<Task> comparator();
}
