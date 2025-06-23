package ru.zuzex.practice.accountms.exception.exception;

public class PageNotFound extends RuntimeException {
    public PageNotFound() {}

    public PageNotFound(String message) {
        super(message);
    }
}
