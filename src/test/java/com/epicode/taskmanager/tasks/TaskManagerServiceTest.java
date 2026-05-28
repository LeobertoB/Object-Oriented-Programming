package com.epicode.taskmanager.tasks;

import com.epicode.taskmanager.tasks.Priority;
import com.epicode.taskmanager.tasks.Task;
import com.epicode.taskmanager.tasks.TaskComponent;
import com.epicode.taskmanager.tasks.TaskGroup;
import com.epicode.taskmanager.tasks.events.TaskEvent;
import com.epicode.taskmanager.tasks.events.TaskEventType;
import com.epicode.taskmanager.tasks.creation.IdGenerator;
import com.epicode.taskmanager.tasks.creation.TaskComponentFactory;
import com.epicode.taskmanager.tasks.persistence.XmlTaskRepository;
import com.epicode.taskmanager.security.exception.ValidationException;
import com.epicode.taskmanager.tasks.sorting.DueDateSortStrategy;
import com.epicode.taskmanager.tasks.sorting.PrioritySortStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskManagerServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void createsGroupsAndTasksUnderSelectedParent() {
        final TaskManagerService service = newService();
        final String rootId = service.getRoot().getId();

        final TaskGroup group = service.createGroup(rootId, "Implementation", "Code tasks");
        final Task task = service.createTask(group.getId(), "Create CLI", "Menu flow", Priority.HIGH, LocalDate.of(2026, 5, 25));

        assertEquals(3, service.listComponents().size());
        assertEquals("Create CLI", task.getTitle());
        assertEquals(1, group.countTotalTasks());
    }

    @Test
    void marksTasksAndGroupsCompletedById() {
        final TaskManagerService service = newService();
        final Task task = service.createTask(
                service.getRoot().getId(),
                "Create CLI",
                "Menu flow",
                Priority.HIGH,
                LocalDate.of(2026, 5, 25)
        );

        service.markCompleted(task.getId());

        assertTrue(task.isCompleted());
        assertEquals(1, service.getRoot().countCompletedTasks());
    }

    @Test
    void listLinesIncludesIndentedNestedTasks() {
        final TaskManagerService service = newService();
        final TaskGroup group = service.createGroup(service.getRoot().getId(), "Documentation", "README");
        service.createTask(group.getId(), "Write README", "Explain usage", Priority.MEDIUM, LocalDate.of(2026, 5, 26));

        final List<String> lines = service.listLines();

        assertEquals("- [OPEN] group-1 | Main Project", lines.get(0));
        assertEquals("  - [OPEN] group-2 | Documentation", lines.get(1));
        assertEquals("    - [OPEN] task-3 | Write README", lines.get(2));
    }

    @Test
    void saveAndLoadCurrentProject() {
        final TaskManagerService service = newService();
        final Task task = service.createTask(
                service.getRoot().getId(),
                "Persist me",
                "Round trip",
                Priority.LOW,
                LocalDate.of(2026, 5, 27)
        );
        service.markCompleted(task.getId());
        final Path filePath = tempDir.resolve("project.xml");

        service.save(filePath);

        final TaskManagerService loadedService = newService();
        loadedService.load(filePath);

        final TaskComponent loadedTask = loadedService.findComponent(task.getId()).orElseThrow();
        assertInstanceOf(Task.class, loadedTask);
        assertTrue(loadedTask.isCompleted());
    }

    @Test
    void rejectsUnknownParentGroup() {
        final TaskManagerService service = newService();

        assertThrows(ValidationException.class, () -> service.createGroup(
                "missing",
                "Invalid",
                "No parent"
        ));
    }

    @Test
    void listsTasksUsingSelectedSortStrategy() {
        final TaskManagerService service = newService();
        final String rootId = service.getRoot().getId();
        service.createTask(rootId, "Later high", "Task", Priority.HIGH, LocalDate.of(2026, 5, 30));
        service.createTask(rootId, "Earlier low", "Task", Priority.LOW, LocalDate.of(2026, 5, 20));
        service.createTask(rootId, "Middle medium", "Task", Priority.MEDIUM, LocalDate.of(2026, 5, 25));

        final List<String> byDate = service.listTasks(new DueDateSortStrategy()).stream()
                .map(Task::getTitle)
                .toList();
        final List<String> byPriority = service.listTasks(new PrioritySortStrategy()).stream()
                .map(Task::getTitle)
                .toList();

        assertEquals(List.of("Earlier low", "Middle medium", "Later high"), byDate);
        assertEquals(List.of("Later high", "Middle medium", "Earlier low"), byPriority);
    }

    @Test
    void reportsOpenTasksDueBeforeDateUsingStreams() {
        final TaskManagerService service = newService();
        final String rootId = service.getRoot().getId();
        final Task urgent = service.createTask(rootId, "Urgent", "Soon", Priority.HIGH, LocalDate.of(2026, 5, 20));
        final Task completed = service.createTask(rootId, "Completed", "Soon but done", Priority.MEDIUM, LocalDate.of(2026, 5, 21));
        service.createTask(rootId, "Later", "Not due yet", Priority.LOW, LocalDate.of(2026, 6, 1));
        service.markCompleted(completed.getId());

        final List<Task> openTasks = service.listOpenTasksDueBefore(LocalDate.of(2026, 5, 22));

        assertEquals(List.of(urgent.getId()), openTasks.stream().map(Task::getId).toList());
    }

    @Test
    void summarizesTasksUsingStreamApi() {
        final TaskManagerService service = newService();
        final String rootId = service.getRoot().getId();
        final Task completed = service.createTask(rootId, "Done", "Completed", Priority.HIGH, LocalDate.of(2026, 5, 20));
        service.createTask(rootId, "Open", "Pending", Priority.LOW, LocalDate.of(2026, 5, 21));
        service.markCompleted(completed.getId());

        final TaskSummary summary = service.summarizeTasks();

        assertEquals(2, summary.totalTasks());
        assertEquals(1, summary.completedTasks());
        assertEquals(1, summary.openTasks());
        assertEquals(0.5, summary.completionRate());
    }

    @Test
    void notifiesObserversAboutCriticalOperations() {
        final TaskManagerService service = newService();
        final List<TaskEvent> events = new ArrayList<>();
        service.addEventListener(events::add);

        final Task task = service.createTask(
                service.getRoot().getId(),
                "Observed",
                "Observer test",
                Priority.HIGH,
                LocalDate.of(2026, 5, 20)
        );
        service.markCompleted(task.getId());
        final Path filePath = tempDir.resolve("observed.xml");
        service.save(filePath);
        service.load(filePath);

        assertEquals(
                List.of(
                        TaskEventType.TASK_CREATED,
                        TaskEventType.COMPONENT_COMPLETED,
                        TaskEventType.PROJECT_SAVED,
                        TaskEventType.PROJECT_LOADED
                ),
                events.stream().map(TaskEvent::type).toList()
        );
    }

    private static TaskManagerService newService() {
        return new TaskManagerService(new TaskComponentFactory(deterministicIds()), new XmlTaskRepository());
    }

    private static IdGenerator deterministicIds() {
        final AtomicInteger sequence = new AtomicInteger();
        return prefix -> prefix + "-" + sequence.incrementAndGet();
    }
}
