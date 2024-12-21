package com.iss.eventorium.shared.exceptions;

public class AlreadyInFavoritesException extends  RuntimeException {
    public AlreadyInFavoritesException(String message) {
        super(message);
    }
}
