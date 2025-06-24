package com.iss.eventorium.solution.validators.reservation;

import com.iss.eventorium.solution.models.Reservation;
import com.iss.eventorium.solution.exceptions.InvalidServiceDurationException;

import java.time.Duration;

public class ServiceDurationValidator implements ReservationValidator {

    @Override
    public void validate(Reservation reservation) {
        long hours = Duration.between(reservation.getStartingTime(), reservation.getEndingTime()).toHours();
        int minDuration = reservation.getService().getMinDuration();
        int maxDuration = reservation.getService().getMaxDuration();

        if (minDuration == maxDuration && hours != minDuration)
            throw new InvalidServiceDurationException(String.format("The service duration must be exactly %d hours.", minDuration));

        if (minDuration != maxDuration && (hours < minDuration || hours > maxDuration))
            throw new InvalidServiceDurationException(String.format("The service duration must be between %d and %d hours.", minDuration, maxDuration));
    }
}