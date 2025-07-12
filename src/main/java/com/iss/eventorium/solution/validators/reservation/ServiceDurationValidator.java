package com.iss.eventorium.solution.validators.reservation;

import com.iss.eventorium.solution.exceptions.InvalidServiceDurationException;
import com.iss.eventorium.solution.models.Reservation;

import java.time.Duration;

public class ServiceDurationValidator implements ReservationValidator {

    @Override
    public void validate(Reservation reservation) {
        long minutes = Duration.between(reservation.getStartingTime(), reservation.getEndingTime()).toMinutes();
        int minDurationMinutes = reservation.getService().getMinDuration() * 60;
        int maxDurationMinutes = reservation.getService().getMaxDuration() * 60;

        if (minDurationMinutes == maxDurationMinutes && minutes != minDurationMinutes)
            throw new InvalidServiceDurationException(String.format("The service duration must be exactly %d hours.", reservation.getService().getMinDuration()));

        if (minDurationMinutes != maxDurationMinutes && (minutes < minDurationMinutes || minutes > maxDurationMinutes))
            throw new InvalidServiceDurationException(String.format("The service duration must be between %d and %d hours.", reservation.getService().getMinDuration(), reservation.getService().getMaxDuration()));
    }
}