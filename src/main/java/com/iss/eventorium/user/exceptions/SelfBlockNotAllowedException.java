package com.iss.eventorium.user.exceptions;

public class SelfBlockNotAllowedException extends RuntimeException {
    public SelfBlockNotAllowedException(String message) {
        super(message);
    }
}