package com.api.parkingcontrol.services.exceptions;

public class ObjectConflictException extends RuntimeException {
    public ObjectConflictException(String message) {
        super(message);
    }
}
