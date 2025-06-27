package ru.zuzex.practice.authms.exception.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Account not found");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
