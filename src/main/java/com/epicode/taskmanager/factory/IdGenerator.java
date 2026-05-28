package com.epicode.taskmanager.factory;

@FunctionalInterface
public interface IdGenerator {
    String nextId(String prefix);
}
