package com.epicode.taskmanager;

import com.epicode.taskmanager.tasks.TaskManagerCli;
import com.epicode.taskmanager.tasks.creation.SequentialIdGenerator;
import com.epicode.taskmanager.tasks.creation.TaskComponentFactory;
import com.epicode.taskmanager.logging.ApplicationLoggerFactory;
import com.epicode.taskmanager.tasks.persistence.XmlTaskRepository;
import com.epicode.taskmanager.security.ExceptionShield;
import com.epicode.taskmanager.tasks.TaskManagerService;

import java.util.Scanner;
import java.util.logging.Logger;

public final class Main {
    private static final Logger LOGGER = ApplicationLoggerFactory.getLogger(Main.class);

    private Main() {
    }

    public static void main(String[] args) {
        LOGGER.info("Application started.");

        final TaskComponentFactory factory = new TaskComponentFactory(new SequentialIdGenerator());
        final TaskManagerService service = new TaskManagerService(factory, new XmlTaskRepository());
        final TaskManagerCli cli = new TaskManagerCli(service, new ExceptionShield(), new Scanner(System.in), System.out);
        cli.run();
    }
}
