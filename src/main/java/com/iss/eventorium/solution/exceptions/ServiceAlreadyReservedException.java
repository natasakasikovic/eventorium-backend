package com.iss.eventorium.solution.exceptions;

public class ServiceAlreadyReservedException extends RuntimeException  {

    public ServiceAlreadyReservedException(String message) {
        super(message);
    }
}
