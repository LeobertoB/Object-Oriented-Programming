package com.epicode.taskmanager.tasks.creation;

import com.epicode.taskmanager.tasks.Priority;
import com.epicode.taskmanager.tasks.Task;
import com.epicode.taskmanager.tasks.TaskBuilder;
import com.epicode.taskmanager.tasks.TaskGroup;
import com.epicode.taskmanager.security.AnnotatedInputValidator;
import com.epicode.taskmanager.security.annotation.SanitizedText;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public final class TaskComponentFactory {
    private static final int MAX_TITLE_LENGTH = 80;
    private static final int MAX_DESCRIPTION_LENGTH = 300;

    private final IdGenerator idGenerator;

    public TaskComponentFactory(IdGenerator idGenerator) {
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator cannot be null");
    }

    public Task createTask(String title, String description, Priority priority, LocalDate dueDate) {
        final Map<String, String> safeText = AnnotatedInputValidator.sanitize(new TextInput(title, description));
        final Priority safePriority = Objects.requireNonNull(priority, "priority cannot be null");
        final LocalDate safeDueDate = Objects.requireNonNull(dueDate, "dueDate cannot be null");

        return new TaskBuilder()
                .id(idGenerator.nextId("task"))
                .title(safeText.get("title"))
                .description(safeText.get("description"))
                .priority(safePriority)
                .dueDate(safeDueDate)
                .build();
    }

    public TaskGroup createGroup(String title, String description) {
        final Map<String, String> safeText = AnnotatedInputValidator.sanitize(new TextInput(title, description));

        return new TaskGroup(idGenerator.nextId("group"), safeText.get("title"), safeText.get("description"));
    }

    private record TextInput(
            @SanitizedText(maxLength = MAX_TITLE_LENGTH) String title,
            @SanitizedText(maxLength = MAX_DESCRIPTION_LENGTH) String description
    ) {
    }
}
