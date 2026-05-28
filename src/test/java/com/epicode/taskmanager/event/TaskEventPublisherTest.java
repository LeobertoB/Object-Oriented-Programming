package com.epicode.taskmanager.event;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskEventPublisherTest {
    @Test
    void publishesEventsToRegisteredListeners() {
        TaskEventPublisher publisher = new TaskEventPublisher();
        List<TaskEvent> received = new ArrayList<>();
        publisher.addListener(received::add);

        publisher.publish(new TaskEvent(TaskEventType.TASK_CREATED, "task-1", "Task created."));

        assertEquals(1, received.size());
        assertEquals(TaskEventType.TASK_CREATED, received.get(0).type());
        assertEquals("task-1", received.get(0).targetId());
    }

    @Test
    void removedListenersStopReceivingEvents() {
        TaskEventPublisher publisher = new TaskEventPublisher();
        List<TaskEvent> received = new ArrayList<>();
        TaskEventListener listener = received::add;

        publisher.addListener(listener);
        assertTrue(publisher.removeListener(listener));
        publisher.publish(new TaskEvent(TaskEventType.TASK_CREATED, "task-1", "Task created."));

        assertTrue(received.isEmpty());
    }
}
