package com.iss.eventorium.user.validators.deactivation;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.repositories.EventRepository;
import com.iss.eventorium.event.specifications.EventSpecification;
import com.iss.eventorium.solution.models.Reservation;
import com.iss.eventorium.solution.repositories.ReservationRepository;
import com.iss.eventorium.solution.specifications.ServiceReservationSpecification;
import com.iss.eventorium.user.exceptions.AccountDeactivationNotAllowedException;
import com.iss.eventorium.user.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountDeactivationValidator {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;

    public void validate(User user) {
        if (user.getRoles().stream().anyMatch(role -> "PROVIDER".equals(role.getName())))
            validateProviderDeactivation(user);

        else if (user.getRoles().stream().anyMatch(role -> "EVENT_ORGANIZER".equals(role.getName())))
            validateEventOrganizerDeactivation(user);
    }

    private void validateProviderDeactivation(User user) {
        Specification<Reservation> specification = ServiceReservationSpecification.checkForAcceptedFutureReservations(user);
        if (reservationRepository.count(specification) != 0)
            throw new AccountDeactivationNotAllowedException("Account deactivation is not possible at this time due to upcoming service reservations.");
    }

    private void validateEventOrganizerDeactivation(User user) {
        Specification<Event> specification = EventSpecification.filterUpcomingEventsByOrganizer(user);
        if (eventRepository.count(specification) != 0)
            throw new AccountDeactivationNotAllowedException("Account deactivation is not possible due to upcoming events.");
    }
}

