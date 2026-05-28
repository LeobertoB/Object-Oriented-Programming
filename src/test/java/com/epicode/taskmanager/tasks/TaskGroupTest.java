package com.epicode.taskmanager.tasks;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskGroupTest {
    @Test
    void groupCountsNestedTasksUsingCompositeStructure() {
        final TaskGroup project = new TaskGroup("project-1", "Final project", "University assignment");
        final TaskGroup documentation = new TaskGroup("group-1", "Documentation", "README and diagrams");
        final Task implementation = task("task-1", "Implement domain", LocalDate.of(2026, 5, 15));
        final Task readme = task("task-2", "Write README", LocalDate.of(2026, 5, 18));
        final Task uml = task("task-3", "Create UML", LocalDate.of(2026, 5, 17));

        documentation.add(readme);
        documentation.add(uml);
        project.add(implementation);
        project.add(documentation);

        readme.markCompleted();

        assertEquals(3, project.countTotalTasks());
        assertEquals(1, project.countCompletedTasks());
        assertFalse(project.isCompleted());
    }

    @Test
    void markingAGroupCompletedMarksAllNestedTasks() {
        final TaskGroup project = new TaskGroup("project-1", "Final project", "University assignment");
        final TaskGroup implementation = new TaskGroup("group-1", "Implementation", "Code tasks");
        final Task firstTask = task("task-1", "Create model", LocalDate.of(2026, 5, 15));
        final Task secondTask = task("task-2", "Add tests", LocalDate.of(2026, 5, 16));

        implementation.add(secondTask);
        project.add(firstTask);
        project.add(implementation);

        project.markCompleted();

        assertTrue(firstTask.isCompleted());
        assertTrue(secondTask.isCompleted());
        assertTrue(project.isCompleted());
        assertEquals(2, project.countCompletedTasks());
    }

    @Test
    void groupDueDateIsEarliestNestedTaskDueDate() {
        final TaskGroup project = new TaskGroup("project-1", "Final project", "University assignment");
        project.add(task("task-1", "Later task", LocalDate.of(2026, 5, 20)));
        project.add(task("task-2", "Earlier task", LocalDate.of(2026, 5, 14)));

        assertEquals(LocalDate.of(2026, 5, 14), project.getDueDate());
    }

    @Test
    void groupChildrenCannotBeModifiedDirectly() {
        final TaskGroup project = new TaskGroup("project-1", "Final project", "University assignment");
        project.add(task("task-1", "Create model", LocalDate.of(2026, 5, 15)));

        assertThrows(UnsupportedOperationException.class, () -> project.getChildren().clear());
    }

    private static Task task(String id, String title, LocalDate dueDate) {
        return new Task(id, title, "Test task", Priority.MEDIUM, dueDate);
    }
}
