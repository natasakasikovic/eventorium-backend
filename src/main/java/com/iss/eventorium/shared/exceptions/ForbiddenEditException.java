package com.iss.eventorium.shared.exceptions;

public class ForbiddenEditException extends RuntimeException {

    public ForbiddenEditException(String message) {
        super(message);
    }
}
