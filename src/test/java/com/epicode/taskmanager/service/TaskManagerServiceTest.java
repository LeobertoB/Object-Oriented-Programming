package com.epicode.taskmanager.service;

import com.epicode.taskmanager.domain.Priority;
import com.epicode.taskmanager.domain.Task;
import com.epicode.taskmanager.domain.TaskComponent;
import com.epicode.taskmanager.domain.TaskGroup;
import com.epicode.taskmanager.event.TaskEvent;
import com.epicode.taskmanager.event.TaskEventType;
import com.epicode.taskmanager.factory.IdGenerator;
import com.epicode.taskmanager.factory.TaskComponentFactory;
import com.epicode.taskmanager.persistence.XmlTaskRepository;
import com.epicode.taskmanager.security.exception.ValidationException;
import com.epicode.taskmanager.strategy.DueDateSortStrategy;
import com.epicode.taskmanager.strategy.PrioritySortStrategy;
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
        TaskManagerService service = newService();
        String rootId = service.getRoot().getId();

        TaskGroup group = service.createGroup(rootId, "Implementation", "Code tasks");
        Task task = service.createTask(group.getId(), "Create CLI", "Menu flow", Priority.HIGH, LocalDate.of(2026, 5, 25));

        assertEquals(3, service.listComponents().size());
        assertEquals("Create CLI", task.getTitle());
        assertEquals(1, group.countTotalTasks());
    }

    @Test
    void marksTasksAndGroupsCompletedById() {
        TaskManagerService service = newService();
        Task task = service.createTask(
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
        TaskManagerService service = newService();
        TaskGroup group = service.createGroup(service.getRoot().getId(), "Documentation", "README");
        service.createTask(group.getId(), "Write README", "Explain usage", Priority.MEDIUM, LocalDate.of(2026, 5, 26));

        List<String> lines = service.listLines();

        assertEquals("- [OPEN] group-1 | Main Project", lines.get(0));
        assertEquals("  - [OPEN] group-2 | Documentation", lines.get(1));
        assertEquals("    - [OPEN] task-3 | Write README", lines.get(2));
    }

    @Test
    void saveAndLoadCurrentProject() {
        TaskManagerService service = newService();
        Task task = service.createTask(
                service.getRoot().getId(),
                "Persist me",
                "Round trip",
                Priority.LOW,
                LocalDate.of(2026, 5, 27)
        );
        service.markCompleted(task.getId());
        Path filePath = tempDir.resolve("project.xml");

        service.save(filePath);

        TaskManagerService loadedService = newService();
        loadedService.load(filePath);

        TaskComponent loadedTask = loadedService.findComponent(task.getId()).orElseThrow();
        assertInstanceOf(Task.class, loadedTask);
        assertTrue(loadedTask.isCompleted());
    }

    @Test
    void rejectsUnknownParentGroup() {
        TaskManagerService service = newService();

        assertThrows(ValidationException.class, () -> service.createGroup(
                "missing",
                "Invalid",
                "No parent"
        ));
    }

    @Test
    void listsTasksUsingSelectedSortStrategy() {
        TaskManagerService service = newService();
        String rootId = service.getRoot().getId();
        service.createTask(rootId, "Later high", "Task", Priority.HIGH, LocalDate.of(2026, 5, 30));
        service.createTask(rootId, "Earlier low", "Task", Priority.LOW, LocalDate.of(2026, 5, 20));
        service.createTask(rootId, "Middle medium", "Task", Priority.MEDIUM, LocalDate.of(2026, 5, 25));

        List<String> byDate = service.listTasks(new DueDateSortStrategy()).stream()
                .map(Task::getTitle)
                .toList();
        List<String> byPriority = service.listTasks(new PrioritySortStrategy()).stream()
                .map(Task::getTitle)
                .toList();

        assertEquals(List.of("Earlier low", "Middle medium", "Later high"), byDate);
        assertEquals(List.of("Later high", "Middle medium", "Earlier low"), byPriority);
    }

    @Test
    void reportsOpenTasksDueBeforeDateUsingStreams() {
        TaskManagerService service = newService();
        String rootId = service.getRoot().getId();
        Task urgent = service.createTask(rootId, "Urgent", "Soon", Priority.HIGH, LocalDate.of(2026, 5, 20));
        Task completed = service.createTask(rootId, "Completed", "Soon but done", Priority.MEDIUM, LocalDate.of(2026, 5, 21));
        service.createTask(rootId, "Later", "Not due yet", Priority.LOW, LocalDate.of(2026, 6, 1));
        service.markCompleted(completed.getId());

        List<Task> openTasks = service.listOpenTasksDueBefore(LocalDate.of(2026, 5, 22));

        assertEquals(List.of(urgent.getId()), openTasks.stream().map(Task::getId).toList());
    }

    @Test
    void summarizesTasksUsingStreamApi() {
        TaskManagerService service = newService();
        String rootId = service.getRoot().getId();
        Task completed = service.createTask(rootId, "Done", "Completed", Priority.HIGH, LocalDate.of(2026, 5, 20));
        service.createTask(rootId, "Open", "Pending", Priority.LOW, LocalDate.of(2026, 5, 21));
        service.markCompleted(completed.getId());

        TaskSummary summary = service.summarizeTasks();

        assertEquals(2, summary.totalTasks());
        assertEquals(1, summary.completedTasks());
        assertEquals(1, summary.openTasks());
        assertEquals(0.5, summary.completionRate());
    }

    @Test
    void notifiesObserversAboutCriticalOperations() {
        TaskManagerService service = newService();
        List<TaskEvent> events = new ArrayList<>();
        service.addEventListener(events::add);

        Task task = service.createTask(
                service.getRoot().getId(),
                "Observed",
                "Observer test",
                Priority.HIGH,
                LocalDate.of(2026, 5, 20)
        );
        service.markCompleted(task.getId());
        Path filePath = tempDir.resolve("observed.xml");
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
        AtomicInteger sequence = new AtomicInteger();
        return prefix -> prefix + "-" + sequence.incrementAndGet();
    }
}
