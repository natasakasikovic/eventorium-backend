package com.iss.eventorium.solution.exceptions;

public class ReservationDeadlineExceededException extends RuntimeException {

    public ReservationDeadlineExceededException(String message) {
        super(message);
    }
}