package com.iss.eventorium.solution.validators.reservation;

import com.iss.eventorium.solution.exceptions.ReservationDeadlineExceededException;
import com.iss.eventorium.solution.models.Reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReservationDeadlineValidator implements ReservationValidator {

    @Override
    public void validate(Reservation reservation) {
        long daysUntilEvent = ChronoUnit.DAYS.between(LocalDate.now(), reservation.getEvent().getDate());
        if (daysUntilEvent < reservation.getService().getReservationDeadline())
            throw new ReservationDeadlineExceededException("Reservation deadline has passed for this service!");
    }
}