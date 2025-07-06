package com.iss.eventorium.event.exceptions;

public class EventTypeAlreadyExistsException extends RuntimeException {

    public EventTypeAlreadyExistsException(String name) {
        super("Event type with name '" + name + "' already exists.");
    }
}
