package com.libraryManagementArangoDB.exceptions;

public class NoSuchApiException extends RuntimeException {
    public NoSuchApiException(String message) {
        super(message);
    }
}
