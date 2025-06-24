package com.iss.eventorium.event.exceptions;

public class EventAlreadyPassedException extends RuntimeException {

    public EventAlreadyPassedException(String message) {
        super(message);
    }
}
