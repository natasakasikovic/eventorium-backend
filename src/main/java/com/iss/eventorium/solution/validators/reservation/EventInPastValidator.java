package com.iss.eventorium.solution.validators.reservation;

import com.iss.eventorium.solution.exceptions.ReservationForPastEventException;
import com.iss.eventorium.solution.models.Reservation;

import java.time.LocalDate;

public class EventInPastValidator implements ReservationValidator{
    @Override
    public void validate(Reservation reservation) {
        if (reservation.getEvent().getDate().isBefore(LocalDate.now()))
            throw new ReservationForPastEventException("You cannot make a reservation for an event that has already passed.");
    }
}