package com.iss.eventorium.solution.specifications;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.models.Reservation;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.user.models.User;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ServiceReservationSpecification {

    private ServiceReservationSpecification() {}

    public static Specification<Reservation> checkForOverlappingReservations(Reservation reservation) {
        return Specification
                .where(hasEventId(reservation.getEvent().getId()))
                .and(hasServiceId(reservation.getService().getId()))
                .and(hasStartTime(reservation.getEndingTime()))
                .and(hasEndTime(reservation.getStartingTime()))
                .and(hasDate(reservation.getEvent().getDate()))
                .and(hasNotStatus(Status.DECLINED));
    }

    public static Specification<Reservation> checkForReservationsInOneHour() {
        return Specification
                .where(hasStatus(Status.ACCEPTED))
                .and(hasEventDateToday())
                .and(hasStartTimeInNextHour());
    }

    private static Specification<Reservation> hasStartTimeInNextHour() {
        return (root, query, cb) -> {
            LocalTime nextHour = LocalDateTime.now().plusHours(1).toLocalTime().withSecond(0).withNano(0);
            Expression<LocalTime> startingTime = root.get("startingTime").as(LocalTime.class);
            return cb.equal(startingTime, nextHour);
        };
    }

    public static Specification<Reservation> checkForAcceptedFutureReservations(User provider) {
        return Specification
                .where(hasProviderId(provider.getId()))
                .and(hasStatus(Status.ACCEPTED))
                .and(hasEventDateInFuture());
    }

    public static Specification<Reservation> getProviderReservations(User provider) {
        return Specification.where(hasProviderId(provider.getId())).and(hasStatus(Status.ACCEPTED));
    }

    public static Specification<Reservation> getPendingReservations(User provider) {
        return Specification.where(hasProviderId(provider.getId()))
                .and(hasStatus(Status.PENDING)
                .and(hasEventDateInFuture()));
    }

    public static Specification<Reservation> getEventReservations(Event event) {
        return Specification.where(hasEventId(event.getId()));
    }

    private static Specification<Reservation> hasEventDateInFuture() {
        return (root, query, cb) -> cb.greaterThan(root.get("event").get("date"), LocalDate.now());
    }

    private static Specification<Reservation> hasProviderId(Long providerId) {
        return (root, query, cb) -> {
            Join<Reservation, Service> reservations = root.join("service", JoinType.INNER);
            return cb.equal(reservations.get("provider").get("id"), providerId);
        };
    }

    private static Specification<Reservation> hasNotStatus(Status status) {
        return (root, query, cb) -> cb.notEqual(root.get("status"), status);
    }

    private static Specification<Reservation> hasEventDateToday() {
        return (root, query, cb) -> cb.equal(root.get("event").get("date"), LocalDate.now());
    }

    private static Specification<Reservation> hasStatus(Status status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
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