package com.iss.eventorium.event.exceptions;

public class AlreadyProcessedException extends RuntimeException {

    public AlreadyProcessedException(String message) {
        super(message);
    }
}
