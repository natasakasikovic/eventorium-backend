package com.iss.eventorium.solution.validators.reservation;

import com.iss.eventorium.company.models.Company;
import com.iss.eventorium.solution.exceptions.ReservationOutsideWorkingHoursException;
import com.iss.eventorium.solution.models.Reservation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorkingHoursValidator implements ReservationValidator {

    private final Company company;

    @Override
    public void validate(Reservation reservation) {
        boolean isBeforeOpeningTime = reservation.getStartingTime().isBefore(company.getOpeningHours());
        boolean isAfterOpeningTime = reservation.getEndingTime().isAfter(company.getClosingHours());

        if (isBeforeOpeningTime || isAfterOpeningTime)
            throw new ReservationOutsideWorkingHoursException(String.format("Reservations can only be made between %s and %s", company.getOpeningHours(), company.getClosingHours()));
    }
}