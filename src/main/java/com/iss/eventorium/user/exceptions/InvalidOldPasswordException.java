package com.iss.eventorium.user.exceptions;

public class InvalidOldPasswordException extends RuntimeException {

    public InvalidOldPasswordException(String message) {
        super(message);
    }
}