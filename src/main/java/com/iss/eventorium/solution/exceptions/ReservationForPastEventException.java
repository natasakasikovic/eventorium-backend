package com.iss.eventorium.solution.exceptions;

public class ReservationForPastEventException extends RuntimeException {
    public ReservationForPastEventException(String message) {
        super(message);
    }
}