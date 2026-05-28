package com.epicode.taskmanager.tasks.creation;

import com.epicode.taskmanager.tasks.Priority;
import com.epicode.taskmanager.tasks.Task;
import com.epicode.taskmanager.tasks.TaskGroup;
import com.epicode.taskmanager.security.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskComponentFactoryTest {
    @Test
    void createsTaskWithGeneratedIdAndTrimmedValues() {
        final TaskComponentFactory factory = new TaskComponentFactory(deterministicIds());

        final Task task = factory.createTask(
                "  Write tests  ",
                "  Cover factory behavior  ",
                Priority.HIGH,
                LocalDate.of(2026, 5, 21)
        );

        assertEquals("task-1", task.getId());
        assertEquals("Write tests", task.getTitle());
        assertEquals("Cover factory behavior", task.getDescription());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(LocalDate.of(2026, 5, 21), task.getDueDate());
    }

    @Test
    void createsGroupWithGeneratedIdAndTrimmedValues() {
        final TaskComponentFactory factory = new TaskComponentFactory(deterministicIds());

        final TaskGroup group = factory.createGroup("  Documentation  ", "  README and UML  ");

        assertEquals("group-1", group.getId());
        assertEquals("Documentation", group.getTitle());
        assertEquals("README and UML", group.getDescription());
    }

    @Test
    void rejectsBlankTitle() {
        final TaskComponentFactory factory = new TaskComponentFactory(deterministicIds());

        assertThrows(ValidationException.class, () -> factory.createTask(
                "   ",
                "Valid description",
                Priority.MEDIUM,
                LocalDate.of(2026, 5, 21)
        ));
    }

    @Test
    void rejectsMissingPriority() {
        final TaskComponentFactory factory = new TaskComponentFactory(deterministicIds());

        assertThrows(NullPointerException.class, () -> factory.createTask(
                "Valid title",
                "Valid description",
                null,
                LocalDate.of(2026, 5, 21)
        ));
    }

    @Test
    void rejectsLongDescription() {
        final TaskComponentFactory factory = new TaskComponentFactory(deterministicIds());
        final String longDescription = "a".repeat(301);

        assertThrows(ValidationException.class, () -> factory.createGroup("Valid title", longDescription));
    }

    @Test
    void removesControlCharactersFromCreatedTaskText() {
        final TaskComponentFactory factory = new TaskComponentFactory(deterministicIds());

        final Task task = factory.createTask(
                "Safe\u0000 title",
                "Safe\u0007 description",
                Priority.LOW,
                LocalDate.of(2026, 5, 21)
        );

        assertEquals("Safe title", task.getTitle());
        assertEquals("Safe description", task.getDescription());
    }

    private static IdGenerator deterministicIds() {
        final AtomicInteger sequence = new AtomicInteger();
        return prefix -> prefix + "-" + sequence.incrementAndGet();
    }
}
