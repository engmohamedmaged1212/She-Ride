package com.project.shedrive.Exceptions;

public class NotAuthException extends RuntimeException {
    public NotAuthException(String message) {
        super(message);
    }
}
