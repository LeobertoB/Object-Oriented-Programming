package com.epicode.taskmanager.factory;

import com.epicode.taskmanager.domain.Priority;
import com.epicode.taskmanager.domain.Task;
import com.epicode.taskmanager.domain.TaskGroup;
import com.epicode.taskmanager.security.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskComponentFactoryTest {
    @Test
    void createsTaskWithGeneratedIdAndTrimmedValues() {
        TaskComponentFactory factory = new TaskComponentFactory(deterministicIds());

        Task task = factory.createTask(
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
        TaskComponentFactory factory = new TaskComponentFactory(deterministicIds());

        TaskGroup group = factory.createGroup("  Documentation  ", "  README and UML  ");

        assertEquals("group-1", group.getId());
        assertEquals("Documentation", group.getTitle());
        assertEquals("README and UML", group.getDescription());
    }

    @Test
    void rejectsBlankTitle() {
        TaskComponentFactory factory = new TaskComponentFactory(deterministicIds());

        assertThrows(ValidationException.class, () -> factory.createTask(
                "   ",
                "Valid description",
                Priority.MEDIUM,
                LocalDate.of(2026, 5, 21)
        ));
    }

    @Test
    void rejectsMissingPriority() {
        TaskComponentFactory factory = new TaskComponentFactory(deterministicIds());

        assertThrows(NullPointerException.class, () -> factory.createTask(
                "Valid title",
                "Valid description",
                null,
                LocalDate.of(2026, 5, 21)
        ));
    }

    @Test
    void rejectsLongDescription() {
        TaskComponentFactory factory = new TaskComponentFactory(deterministicIds());
        String longDescription = "a".repeat(301);

        assertThrows(ValidationException.class, () -> factory.createGroup("Valid title", longDescription));
    }

    @Test
    void removesControlCharactersFromCreatedTaskText() {
        TaskComponentFactory factory = new TaskComponentFactory(deterministicIds());

        Task task = factory.createTask(
                "Safe\u0000 title",
                "Safe\u0007 description",
                Priority.LOW,
                LocalDate.of(2026, 5, 21)
        );

        assertEquals("Safe title", task.getTitle());
        assertEquals("Safe description", task.getDescription());
    }

    private static IdGenerator deterministicIds() {
        AtomicInteger sequence = new AtomicInteger();
        return prefix -> prefix + "-" + sequence.incrementAndGet();
    }
}
