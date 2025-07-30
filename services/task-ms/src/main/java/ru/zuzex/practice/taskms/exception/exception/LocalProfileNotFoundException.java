package ru.zuzex.practice.taskms.exception.exception;

public class LocalProfileNotFoundException extends RuntimeException {
    public LocalProfileNotFoundException() {
        super("Profile not found in local storage");
    }

    public LocalProfileNotFoundException(String message) {
        super(message);
    }
}
