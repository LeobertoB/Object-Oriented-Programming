package com.epicode.taskmanager.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {
    @Test
    void taskStartsIncompleteAndCanBeMarkedCompleted() {
        Task task = new Task(
                "task-1",
                "Write tests",
                "Cover the domain model",
                Priority.HIGH,
                LocalDate.of(2026, 5, 20)
        );

        assertFalse(task.isCompleted());
        assertEquals(1, task.countTotalTasks());
        assertEquals(0, task.countCompletedTasks());

        task.markCompleted();

        assertTrue(task.isCompleted());
        assertEquals(1, task.countCompletedTasks());
    }

    @Test
    void taskRejectsNullRequiredValues() {
        assertThrows(NullPointerException.class, () -> new Task(
                null,
                "Invalid",
                "Missing id",
                Priority.LOW,
                LocalDate.of(2026, 5, 20)
        ));
    }
}
