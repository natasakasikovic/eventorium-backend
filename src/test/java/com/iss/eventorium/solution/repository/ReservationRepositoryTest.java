package com.iss.eventorium.solution.repository;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.solution.models.Reservation;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ReservationRepository;
import com.iss.eventorium.solution.specifications.ServiceReservationSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/sql/reservation-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("It should return false because the existing reservation for this event and service at the specified time is declined, which there's no overlapping.")
    void givenExistingDeclinedReservationInDatabase_whenCheckingForOverlap_thenShouldReturnFalse()  {
        Service service = entityManager.find(Service.class, 1);
        Event event = entityManager.find(Event.class, 1);

        Reservation reservation = Reservation.builder().startingTime(LocalTime.of(10, 0)).endingTime(LocalTime.of(12, 0)).service(service).event(event).build();
        Specification<Reservation> specification = ServiceReservationSpecification.checkForOverlappingReservations(reservation);
        boolean exists = reservationRepository.exists(specification);

        assertFalse(exists);
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideReservationsThatOverlapExisting")
    @DisplayName("Should return true because an existing reservation for this event and service at the specified time is active, meaning overlapping is detected.")
    void givenExistingActiveReservationInDatabase_whenCheckingForOverlap_thenShouldReturnTrue(Reservation reservation) {
        Service service = entityManager.find(Service.class, 3);
        Event event = entityManager.find(Event.class, 1);

        reservation.setEvent(event);
        reservation.setService(service);

        Specification<Reservation> specification = ServiceReservationSpecification.checkForOverlappingReservations(reservation);
        boolean exists = reservationRepository.exists(specification);

        assertTrue(exists);
    }


    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideBoundaryReservations")
    @DisplayName("Should return false when new reservation starts exactly when existing one ends or ends exactly when existing one starts (no overlapping).")
    void givenNewReservationAtBoundaryTimes_whenCheckingForOverlap_thenShouldReturnFalse(Reservation reservation) {
        Service service = entityManager.find(Service.class, 1);
        Event event = entityManager.find(Event.class, 2);

        reservation.setEvent(event);
        reservation.setService(service);

        // Existing in database: 13:00 – 15:00
        Specification<Reservation> specification = ServiceReservationSpecification.checkForOverlappingReservations(reservation);
        boolean exists = reservationRepository.exists(specification);

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return false when same service is reserved at the same time but for a different event (no overlapping).")
    void givenDifferentEventAndSameServiceAtSameTime_whenCheckingForOverlap_thenShouldReturnFalse() {
        Service service = entityManager.find(Service.class, 1);
        Event differentEvent = entityManager.find(Event.class, 2);

        // Existing reservation in DB: event_id = 1, service_id = 1, Time = 17:00 – 19:00, only differ is that event_id is 2 in reservation here
        Reservation reservation = Reservation.builder().startingTime(LocalTime.of(16, 0)).endingTime(LocalTime.of(19, 40)).service(service).event(differentEvent).build();
        Specification<Reservation> specification = ServiceReservationSpecification.checkForOverlappingReservations(reservation);
        boolean exists = reservationRepository.exists(specification);

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return false when different services are reserved at the same time for the same event (no overlapping).")
    void givenSameEventAndDifferentServiceAtSameTime_whenCheckingForOverlap_thenShouldReturnFalse() {
        Event event = entityManager.find(Event.class, 2);
        Service service = entityManager.find(Service.class, 2);

        // Existing reservation in DB: event_id = 2, service_id = 1, Time = 13:00 – 15:00
        Reservation reservation = Reservation.builder().startingTime(LocalTime.of(13, 0)).endingTime(LocalTime.of(15, 0)).service(service).event(event).build();
        Specification<Reservation> specification = ServiceReservationSpecification.checkForOverlappingReservations(reservation);
        boolean exists = reservationRepository.exists(specification);

        assertFalse(exists);
    }
}