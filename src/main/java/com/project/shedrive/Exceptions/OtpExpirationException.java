package com.project.shedrive.Exceptions;

public class OtpExpirationException extends RuntimeException {
    public OtpExpirationException(String message) {
        super(message);
    }
}
