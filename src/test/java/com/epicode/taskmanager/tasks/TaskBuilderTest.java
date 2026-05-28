package com.epicode.taskmanager.tasks;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskBuilderTest {
    @Test
    void buildsCompletedTask() {
        final Task task = new TaskBuilder()
                .id("task-1")
                .title("Builder task")
                .description("Created through builder")
                .priority(Priority.HIGH)
                .dueDate(LocalDate.of(2026, 5, 30))
                .completed(true)
                .build();

        assertEquals("task-1", task.getId());
        assertEquals(Priority.HIGH, task.getPriority());
        assertTrue(task.isCompleted());
    }

    @Test
    void rejectsMissingRequiredValues() {
        final TaskBuilder builder = new TaskBuilder()
                .id("task-1")
                .title("Builder task");

        assertThrows(NullPointerException.class, builder::build);
    }
}
