package com.epicode.taskmanager.tasks.persistence;

import com.epicode.taskmanager.tasks.TaskGroup;

import java.nio.file.Path;

public interface TaskRepository {
    void save(TaskGroup root, Path filePath);

    TaskGroup load(Path filePath);
}
