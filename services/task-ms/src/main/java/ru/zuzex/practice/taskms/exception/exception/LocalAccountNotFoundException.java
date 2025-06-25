package ru.zuzex.practice.taskms.exception.exception;

public class LocalAccountNotFoundException extends RuntimeException {
    public LocalAccountNotFoundException() {
        super("Account not found in local storage");
    }

    public LocalAccountNotFoundException(String message) {
        super(message);
    }
}
