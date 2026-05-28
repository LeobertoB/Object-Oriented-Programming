package com.epicode.taskmanager.iterator;

import com.epicode.taskmanager.domain.TaskComponent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class TaskComponentIterator implements Iterator<TaskComponent> {
    private final Deque<Iterator<TaskComponent>> iterators = new ArrayDeque<>();

    public TaskComponentIterator(List<TaskComponent> roots) {
        iterators.push(Objects.requireNonNull(roots, "roots cannot be null").iterator());
    }

    @Override
    public boolean hasNext() {
        while (!iterators.isEmpty()) {
            if (iterators.peek().hasNext()) {
                return true;
            }
            iterators.pop();
        }
        return false;
    }

    @Override
    public TaskComponent next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more task components");
        }

        TaskComponent component = iterators.peek().next();
        if (!component.getChildren().isEmpty()) {
            iterators.push(component.getChildren().iterator());
        }
        return component;
    }
}
