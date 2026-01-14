package com.oot.clinic.exceptions;

public abstract class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }
}

