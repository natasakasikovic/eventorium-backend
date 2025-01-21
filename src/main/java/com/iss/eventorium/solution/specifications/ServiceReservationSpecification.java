package com.iss.eventorium.solution.specifications;

import com.iss.eventorium.solution.models.Reservation;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public class ServiceReservationSpecification {

    public static Specification<Reservation> checkForOverlappingReservations(Reservation reservation) {
        return Specification
                .where(hasEventId(reservation.getEvent().getId()))
                .and(hasServiceId(reservation.getService().getId()))
                .and(hasStartTime(reservation.getEndingTime()))
                .and(hasEndTime(reservation.getStartingTime()))
                .and(hasDate(reservation.getEvent().getDate()));
    }

    private static Specification<Reservation> hasEventId(Long eventId) {
        return (root, query, cb) -> cb.equal(root.get("event").get("id"), eventId);
    }

    private static Specification<Reservation> hasServiceId(Long serviceId) {
        return (root, query, cb) -> cb.equal(root.get("service").get("id"), serviceId);
    }

    private static Specification<Reservation> hasStartTime(LocalTime endTime) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("startingTime"), endTime);
    }

    private static Specification<Reservation> hasEndTime(LocalTime startTime) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("endingTime"), startTime);
    }

    private static Specification<Reservation> hasDate(LocalDate date) {
        return (root, query, cb) -> cb.equal(root.get("event").get("date"), date);
    }
}