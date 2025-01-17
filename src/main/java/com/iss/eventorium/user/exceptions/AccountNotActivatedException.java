package com.iss.eventorium.user.exceptions;

public class AccountNotActivatedException extends RuntimeException {
    public AccountNotActivatedException(String message) {
        super(message);
    }
}
