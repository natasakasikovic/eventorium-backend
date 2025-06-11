package com.iss.eventorium.user.exceptions;

public class UserSuspendedException extends RuntimeException {

    public UserSuspendedException(String message) {
        super(message);
    }
}