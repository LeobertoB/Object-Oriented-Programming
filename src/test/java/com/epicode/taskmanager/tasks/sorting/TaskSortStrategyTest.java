package com.epicode.taskmanager.tasks.sorting;

import com.epicode.taskmanager.tasks.Priority;
import com.epicode.taskmanager.tasks.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskSortStrategyTest {
    @Test
    void dueDateStrategySortsByEarliestDate() {
        final List<Task> sorted = tasks().stream()
                .sorted(new DueDateSortStrategy().comparator())
                .toList();

        assertEquals(List.of("task-2", "task-3", "task-1"), ids(sorted));
    }

    @Test
    void priorityStrategySortsHighPriorityFirst() {
        final List<Task> sorted = tasks().stream()
                .sorted(new PrioritySortStrategy().comparator())
                .toList();

        assertEquals(List.of("task-1", "task-3", "task-2"), ids(sorted));
    }

    @Test
    void titleStrategySortsAlphabeticallyIgnoringCase() {
        final List<Task> sorted = tasks().stream()
                .sorted(new TitleSortStrategy().comparator())
                .toList();

        assertEquals(List.of("task-2", "task-3", "task-1"), ids(sorted));
    }

    private static List<Task> tasks() {
        return List.of(
                new Task("task-1", "Write README", "Docs", Priority.HIGH, LocalDate.of(2026, 5, 30)),
                new Task("task-2", "Add tests", "Tests", Priority.LOW, LocalDate.of(2026, 5, 20)),
                new Task("task-3", "Create CLI", "CLI", Priority.MEDIUM, LocalDate.of(2026, 5, 25))
        );
    }

    private static List<String> ids(List<Task> tasks) {
        return tasks.stream()
                .map(Task::getId)
                .toList();
    }
}
