package com.epicode.taskmanager.tasks.creation;

@FunctionalInterface
public interface IdGenerator {
    String nextId(String prefix);
}
