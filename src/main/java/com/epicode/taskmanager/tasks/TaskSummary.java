package com.epicode.taskmanager.tasks;

public record TaskSummary(int totalTasks, int completedTasks, int openTasks) {
    public double completionRate() {
        if (totalTasks == 0) {
            return 0.0;
        }
        return (double) completedTasks / totalTasks;
    }
}
