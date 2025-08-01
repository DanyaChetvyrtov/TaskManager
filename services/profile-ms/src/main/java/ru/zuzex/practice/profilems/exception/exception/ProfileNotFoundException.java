package ru.zuzex.practice.profilems.exception.exception;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException() {
        super("Profile not found");
    }

    public ProfileNotFoundException(String message) {
        super(message);
    }
}
