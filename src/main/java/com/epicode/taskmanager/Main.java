package com.epicode.taskmanager;

import com.epicode.taskmanager.cli.TaskManagerCli;
import com.epicode.taskmanager.factory.SequentialIdGenerator;
import com.epicode.taskmanager.factory.TaskComponentFactory;
import com.epicode.taskmanager.logging.ApplicationLoggerFactory;
import com.epicode.taskmanager.persistence.XmlTaskRepository;
import com.epicode.taskmanager.security.ExceptionShield;
import com.epicode.taskmanager.service.TaskManagerService;

import java.util.Scanner;
import java.util.logging.Logger;

public final class Main {
    private static final Logger LOGGER = ApplicationLoggerFactory.getLogger(Main.class);

    private Main() {
    }

    public static void main(String[] args) {
        LOGGER.info("Application started.");

        TaskComponentFactory factory = new TaskComponentFactory(new SequentialIdGenerator());
        TaskManagerService service = new TaskManagerService(factory, new XmlTaskRepository());
        TaskManagerCli cli = new TaskManagerCli(service, new ExceptionShield(), new Scanner(System.in), System.out);
        cli.run();
    }
}
