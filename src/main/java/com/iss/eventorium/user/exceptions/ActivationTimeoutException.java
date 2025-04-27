package com.iss.eventorium.user.exceptions;

public class ActivationTimeoutException extends RuntimeException {

    public ActivationTimeoutException(String message) {
        super(message);
    }
}
