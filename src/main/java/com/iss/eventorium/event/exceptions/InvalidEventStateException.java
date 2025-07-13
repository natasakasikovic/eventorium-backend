package com.iss.eventorium.event.exceptions;

public class InvalidEventStateException extends RuntimeException {

    public InvalidEventStateException(String message) {
        super(message);
    }
}