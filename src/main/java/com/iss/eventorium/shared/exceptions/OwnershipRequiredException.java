package com.iss.eventorium.shared.exceptions;

public class OwnershipRequiredException extends RuntimeException {

    public OwnershipRequiredException(String message) {
        super(message);
    }
}