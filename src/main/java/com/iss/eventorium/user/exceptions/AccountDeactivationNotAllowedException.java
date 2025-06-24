package com.iss.eventorium.user.exceptions;

public class AccountDeactivationNotAllowedException extends RuntimeException {

    public AccountDeactivationNotAllowedException(String message) {
        super(message);
    }
}
