package com.iss.eventorium.event.exceptions;

public class AlreadyPurchasedException extends RuntimeException {

    public AlreadyPurchasedException(String message) {
        super(message);
    }
}
