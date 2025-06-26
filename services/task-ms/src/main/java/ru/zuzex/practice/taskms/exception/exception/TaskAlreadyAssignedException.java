package ru.zuzex.practice.taskms.exception.exception;

public class TaskAlreadyAssignedException extends RuntimeException {
    public TaskAlreadyAssignedException() {
        super("Task already assigned to this account.");
    }

    public TaskAlreadyAssignedException(String message) {
        super(message);
    }
}
