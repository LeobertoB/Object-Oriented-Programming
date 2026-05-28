package com.epicode.taskmanager.tasks.sorting;

import com.epicode.taskmanager.tasks.Task;

import java.util.Comparator;

public interface TaskSortStrategy {
    Comparator<Task> comparator();
}
