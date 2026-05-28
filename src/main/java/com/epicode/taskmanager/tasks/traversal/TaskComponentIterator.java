package com.epicode.taskmanager.tasks.traversal;

import com.epicode.taskmanager.tasks.TaskComponent;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class TaskComponentIterator implements Iterator<TaskComponent> {
    private final Deque<TaskComponent> pendingComponents = new ArrayDeque<>();

    public TaskComponentIterator(Collection<TaskComponent> roots) {
        final List<TaskComponent> rootList = List.copyOf(Objects.requireNonNull(roots, "roots cannot be null"));
        for (int index = rootList.size() - 1; index >= 0; index--) {
            pendingComponents.push(rootList.get(index));
        }
    }

    @Override
    public boolean hasNext() {
        return !pendingComponents.isEmpty();
    }

    @Override
    public TaskComponent next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more task components");
        }

        final TaskComponent component = pendingComponents.pop();
        final List<TaskComponent> children = component.getChildren();
        for (int index = children.size() - 1; index >= 0; index--) {
            pendingComponents.push(children.get(index));
        }
        return component;
    }
}
