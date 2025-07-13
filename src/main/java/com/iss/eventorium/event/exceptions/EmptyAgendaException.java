package com.iss.eventorium.event.exceptions;

public class EmptyAgendaException extends RuntimeException {
    public EmptyAgendaException(String message) {
        super(message);
    }
}
