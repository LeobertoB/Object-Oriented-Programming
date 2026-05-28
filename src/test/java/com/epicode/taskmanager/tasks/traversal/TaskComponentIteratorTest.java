package com.epicode.taskmanager.tasks.traversal;

import com.epicode.taskmanager.tasks.Priority;
import com.epicode.taskmanager.tasks.Task;
import com.epicode.taskmanager.tasks.TaskComponent;
import com.epicode.taskmanager.tasks.TaskGroup;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskComponentIteratorTest {
    @Test
    void traversesNestedComponentsDepthFirst() {
        final TaskGroup project = new TaskGroup("project", "Project", "Root");
        final TaskGroup documentation = new TaskGroup("docs", "Documentation", "Docs group");
        final TaskGroup testing = new TaskGroup("tests", "Testing", "Tests group");

        project.add(task("task-1"));
        documentation.add(task("task-2"));
        testing.add(task("task-3"));
        documentation.add(testing);
        project.add(documentation);
        project.add(task("task-4"));

        final List<String> visitedIds = new ArrayList<>();
        for (TaskComponent component : project) {
            visitedIds.add(component.getId());
        }

        assertEquals(List.of("task-1", "docs", "task-2", "tests", "task-3", "task-4"), visitedIds);
    }

    @Test
    void emptyGroupIteratorHasNoElements() {
        final TaskGroup emptyGroup = new TaskGroup("empty", "Empty", "No children");
        final Iterator<TaskComponent> iterator = emptyGroup.iterator();

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    private static Task task(String id) {
        return new Task(id, "Task " + id, "Iterator test", Priority.MEDIUM, LocalDate.of(2026, 5, 22));
    }
}
