package com.iss.eventorium.solution.validators.reservation;

import com.iss.eventorium.solution.exceptions.ReservationConflictException;
import com.iss.eventorium.solution.models.Reservation;
import com.iss.eventorium.solution.repositories.ReservationRepository;
import com.iss.eventorium.solution.specifications.ServiceReservationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class ReservationConflictValidator implements ReservationValidator {

    private final ReservationRepository repository;

    @Override
    public void validate(Reservation reservation) {
        Specification<Reservation> specification = ServiceReservationSpecification.checkForOverlappingReservations(reservation);
        if (repository.exists(specification))
            throw new ReservationConflictException("For your event, this service is already reserved during the selected time slot. Please choose a different time.");
    }
}