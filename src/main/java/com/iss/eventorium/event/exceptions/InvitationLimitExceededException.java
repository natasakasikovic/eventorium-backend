package com.iss.eventorium.event.exceptions;

public class InvitationLimitExceededException extends RuntimeException {
    public InvitationLimitExceededException(String message) {
        super(message);
    }
}