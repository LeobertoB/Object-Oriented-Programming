package com.epicode.taskmanager.tasks.persistence;

import com.epicode.taskmanager.tasks.Priority;
import com.epicode.taskmanager.tasks.Task;
import com.epicode.taskmanager.tasks.TaskComponent;
import com.epicode.taskmanager.tasks.TaskGroup;
import com.epicode.taskmanager.security.exception.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlTaskRepositoryTest {
    private final XmlTaskRepository repository = new XmlTaskRepository();

    @TempDir
    Path tempDir;

    @Test
    void savesAndLoadsNestedTaskStructure() {
        final TaskGroup root = new TaskGroup("group-1", "Final Project", "Course assignment");
        final TaskGroup documentation = new TaskGroup("group-2", "Documentation", "README and diagrams");
        final Task modelTask = new Task(
                "task-1",
                "Build model",
                "Create composite model",
                Priority.HIGH,
                LocalDate.of(2026, 5, 20)
        );
        final Task readmeTask = new Task(
                "task-2",
                "Write README",
                "Explain decisions",
                Priority.MEDIUM,
                LocalDate.of(2026, 5, 22)
        );
        readmeTask.markCompleted();

        documentation.add(readmeTask);
        root.add(modelTask);
        root.add(documentation);

        final Path filePath = tempDir.resolve("tasks.xml");
        repository.save(root, filePath);

        final TaskGroup loaded = repository.load(filePath);

        assertEquals("group-1", loaded.getId());
        assertEquals("Final Project", loaded.getTitle());
        assertEquals(2, loaded.getChildren().size());
        assertEquals(2, loaded.countTotalTasks());
        assertEquals(1, loaded.countCompletedTasks());

        final Task loadedTask = assertInstanceOf(Task.class, loaded.getChildren().get(0));
        assertEquals("task-1", loadedTask.getId());
        assertEquals(Priority.HIGH, loadedTask.getPriority());
        assertEquals(LocalDate.of(2026, 5, 20), loadedTask.getDueDate());
        assertFalse(loadedTask.isCompleted());

        final TaskGroup loadedDocumentation = assertInstanceOf(TaskGroup.class, loaded.getChildren().get(1));
        final Task loadedReadme = assertInstanceOf(Task.class, loadedDocumentation.getChildren().get(0));
        assertTrue(loadedReadme.isCompleted());
    }

    @Test
    void createsParentDirectoriesWhenSaving() {
        final TaskGroup root = new TaskGroup("group-1", "Project", "Description");
        final Path nestedFile = tempDir.resolve("nested").resolve("tasks.xml");

        repository.save(root, nestedFile);

        assertTrue(Files.isRegularFile(nestedFile));
    }

    @Test
    void rejectsNonXmlFiles() {
        final TaskGroup root = new TaskGroup("group-1", "Project", "Description");

        assertThrows(PersistenceException.class, () -> repository.save(root, tempDir.resolve("tasks.txt")));
    }

    @Test
    void rejectsMissingFilesWithControlledException() {
        final Path missingFile = tempDir.resolve("missing.xml");

        assertThrows(PersistenceException.class, () -> repository.load(missingFile));
    }

    @Test
    void rejectsUnsupportedXmlElements() throws IOException {
        final Path filePath = tempDir.resolve("invalid.xml");
        Files.writeString(filePath, """
                <?xml version="1.0" encoding="UTF-8"?>
                <taskGroup id="group-1" title="Project" description="Description">
                    <unknown />
                </taskGroup>
                """);

        assertThrows(PersistenceException.class, () -> repository.load(filePath));
    }

    @Test
    void loadedGroupCanStillBeTraversedWithIterator() {
        final TaskGroup root = new TaskGroup("group-1", "Project", "Description");
        root.add(new Task("task-1", "First", "Description", Priority.LOW, LocalDate.of(2026, 5, 20)));
        root.add(new Task("task-2", "Second", "Description", Priority.HIGH, LocalDate.of(2026, 5, 21)));

        final Path filePath = tempDir.resolve("tasks.xml");
        repository.save(root, filePath);

        final List<String> ids = new java.util.ArrayList<>();
        for (TaskComponent component : repository.load(filePath)) {
            ids.add(component.getId());
        }

        assertEquals(List.of("task-1", "task-2"), ids);
    }
}
