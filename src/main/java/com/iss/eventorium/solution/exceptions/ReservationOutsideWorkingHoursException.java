package com.iss.eventorium.solution.exceptions;

public class ReservationOutsideWorkingHoursException extends RuntimeException {
    public ReservationOutsideWorkingHoursException(String message) {
        super(message);
    }
}