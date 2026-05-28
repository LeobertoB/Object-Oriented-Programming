package com.epicode.taskmanager.cli;

import com.epicode.taskmanager.domain.Priority;
import com.epicode.taskmanager.security.ExceptionShield;
import com.epicode.taskmanager.security.OperationResult;
import com.epicode.taskmanager.security.exception.ValidationException;
import com.epicode.taskmanager.service.TaskManagerService;

import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Scanner;

public final class TaskManagerCli {
    private final TaskManagerService service;
    private final ExceptionShield shield;
    private final Scanner scanner;
    private final PrintStream output;

    public TaskManagerCli(TaskManagerService service, ExceptionShield shield, Scanner scanner, PrintStream output) {
        this.service = Objects.requireNonNull(service, "service cannot be null");
        this.shield = Objects.requireNonNull(shield, "shield cannot be null");
        this.scanner = Objects.requireNonNull(scanner, "scanner cannot be null");
        this.output = Objects.requireNonNull(output, "output cannot be null");
    }

    public void run() {
        output.println("Secure Task Manager");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> listTasks();
                case "2" -> createGroup();
                case "3" -> createTask();
                case "4" -> markCompleted();
                case "5" -> save();
                case "6" -> load();
                case "0" -> running = false;
                default -> output.println("Invalid option.");
            }
        }
        output.println("Goodbye.");
    }

    private void printMenu() {
        output.println();
        output.println("1. List tasks");
        output.println("2. Create group");
        output.println("3. Create task");
        output.println("4. Mark completed");
        output.println("5. Save");
        output.println("6. Load");
        output.println("0. Exit");
        output.print("Choose an option: ");
    }

    private void listTasks() {
        service.listLines().forEach(output::println);
    }

    private void createGroup() {
        OperationResult<String> result = shield.execute(() -> {
            String parentId = ask("Parent group id");
            String title = ask("Group title");
            String description = ask("Group description");
            return service.createGroup(parentId, title, description).getId();
        });
        printResult(result, "Group created with id: ");
    }

    private void createTask() {
        OperationResult<String> result = shield.execute(() -> {
            String parentId = ask("Parent group id");
            String title = ask("Task title");
            String description = ask("Task description");
            Priority priority = parsePriority(ask("Priority (LOW, MEDIUM, HIGH)"));
            LocalDate dueDate = parseDate(ask("Due date (YYYY-MM-DD)"));
            return service.createTask(parentId, title, description, priority, dueDate).getId();
        });
        printResult(result, "Task created with id: ");
    }

    private void markCompleted() {
        OperationResult<String> result = shield.execute(() -> {
            String componentId = ask("Task or group id");
            service.markCompleted(componentId);
            return componentId;
        });
        printResult(result, "Marked completed: ");
    }

    private void save() {
        OperationResult<String> result = shield.execute(() -> {
            String filePath = ask("XML file path");
            service.save(Path.of(filePath));
            return filePath;
        });
        printResult(result, "Saved to: ");
    }

    private void load() {
        OperationResult<String> result = shield.execute(() -> {
            String filePath = ask("XML file path");
            service.load(Path.of(filePath));
            return filePath;
        });
        printResult(result, "Loaded from: ");
    }

    private String ask(String prompt) {
        output.print(prompt + ": ");
        return scanner.nextLine();
    }

    private void printResult(OperationResult<String> result, String successPrefix) {
        if (result.isSuccess()) {
            output.println(successPrefix + result.getValue().orElse(""));
        } else {
            output.println(result.getMessage());
        }
    }

    private static Priority parsePriority(String value) {
        try {
            return Priority.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Priority must be LOW, MEDIUM, or HIGH.");
        }
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new ValidationException("Date must use the YYYY-MM-DD format.");
        }
    }
}
