package com.iss.eventorium.solution.exceptions;

public class InvalidServiceDurationException extends RuntimeException {
    public InvalidServiceDurationException(String message) {
        super(message);
    }
}