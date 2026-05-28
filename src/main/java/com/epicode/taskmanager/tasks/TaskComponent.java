package com.epicode.taskmanager.tasks;

import java.time.LocalDate;
import java.util.List;

public interface TaskComponent {
    String getId();

    String getTitle();

    String getDescription();

    LocalDate getDueDate();

    boolean isCompleted();

    void markCompleted();

    int countTotalTasks();

    int countCompletedTasks();

    List<TaskComponent> getChildren();
}
