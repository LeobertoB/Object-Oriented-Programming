package com.epicode.taskmanager.persistence;

import com.epicode.taskmanager.domain.TaskGroup;

import java.nio.file.Path;

public interface TaskRepository {
    void save(TaskGroup root, Path filePath);

    TaskGroup load(Path filePath);
}
