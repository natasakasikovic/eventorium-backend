package com.iss.eventorium.event.exceptions;

public class AgendaAlreadyDefinedException extends RuntimeException {
    public AgendaAlreadyDefinedException(String message) {
        super(message);
    }
}
