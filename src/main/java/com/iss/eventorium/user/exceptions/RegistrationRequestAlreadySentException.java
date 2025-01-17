package com.iss.eventorium.user.exceptions;

public class RegistrationRequestAlreadySentException extends RuntimeException {
    public RegistrationRequestAlreadySentException(String message) {
        super(message);
    }
}
