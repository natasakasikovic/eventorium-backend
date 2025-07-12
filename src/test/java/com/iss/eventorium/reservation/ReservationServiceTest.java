package com.iss.eventorium.reservation;

import com.iss.eventorium.company.models.Company;
import com.iss.eventorium.company.services.CompanyService;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.exceptions.InsufficientFundsException;
import com.iss.eventorium.shared.exceptions.OwnershipRequiredException;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.dtos.services.ReservationRequestDto;
import com.iss.eventorium.solution.exceptions.ReservationDeadlineExceededException;
import com.iss.eventorium.solution.exceptions.ReservationForPastEventException;
import com.iss.eventorium.solution.mappers.ReservationMapper;
import com.iss.eventorium.solution.models.Reservation;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ReservationRepository;
import com.iss.eventorium.solution.services.ReservationService;
import com.iss.eventorium.solution.services.ServiceService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/*
 NOTE: Certain test cases—such as attempting to reserve a logically deleted service,
 an invisible service, or a service that has not been accepted—are not written here,
 because such services are filtered out at the serviceRepository level.
 Therefore, these cases are considered part of the serviceRepository's test coverage
*/

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    ReservationRepository repository;

    @Mock
    EventService eventService;
    
    @Mock
    ServiceService serviceService;

    @Mock
    AuthService authService;

    @Mock
    CompanyService companyService;

    @Mock
    ReservationMapper mapper;

    @InjectMocks
    ReservationService service;

    private User currentUser;
    private User provider;
    private ReservationRequestDto request;

    @BeforeEach
    public void setUp() {
        currentUser = User.builder().id(1L).build();
        provider = User.builder().id(2L).build();
        request = new ReservationRequestDto(LocalTime.of(11, 0), LocalTime.of(13, 0), 100.0);
        lenient().when(authService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test 
    @Tag("exception-handling")
    @DisplayName("Should throw EntityNotFoundException when trying to create reservation for non-existing event")
    void givenNonExistingEvent_whenCreateReservation_thenThrowEntityNotFoundException() {
        when(eventService.find(anyLong())).thenThrow(new EntityNotFoundException("Event not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.createReservation(request, 1L, 1L));

        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    @Tag("exception-handling")
    @DisplayName("Should throw OwnershipRequiredException if user is not organizer of the event during reservation creation")
    void givenEventWithoutOwnership_whenCreateReservation_thenThrowsOwnershipRequiredException() {
        Event event = Event.builder().organizer(provider).build();
        when(eventService.find(anyLong())).thenReturn(event);

        OwnershipRequiredException exception = assertThrows(OwnershipRequiredException.class,
                () -> service.createReservation(request, 1L, 1L));

        assertEquals("You cannot make a reservation for an event you are not the organizer of!", exception.getMessage());
    }

    @Test
    @Tag("exception-handling")
    @DisplayName("Should throw EntityNotFoundException if service does not exist during reservation creation")
    void givenNonExistingService_whenCreateReservation_thenThrowEntityNotFoundException() {
        Event event = Event.builder().organizer(currentUser).build();

        when(eventService.find(anyLong())).thenReturn(event);
        when(serviceService.find(anyLong())).thenThrow(new EntityNotFoundException("Service not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.createReservation(request, 1L, 1L));

        assertEquals("Service not found", exception.getMessage());
    }

    @Test
    @Tag("exception-handling")
    @DisplayName("Should throw ReservationForPastEventException when trying to create a reservation for a past event")
    void givenPastEvent_whenCreateReservation_thenThrowEventInPastException() {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().minusDays(3)).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).isAvailable(true).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(request, event, service);

        when(companyService.getByProviderId(anyLong())).thenReturn(new Company());

        ReservationForPastEventException exception = assertThrows(ReservationForPastEventException.class,
                () -> this.service.createReservation(request, 1L, 1L));

        assertEquals("You cannot make a reservation for an event that has already passed." , exception.getMessage());
    }

    @Test
    @Tag("exception-handling")
    @DisplayName("Should throw InsufficientFundsException if planned amount is insufficient for service reservation")
    void givenInsufficientFunds_whenCreateReservation_thenThrowInsufficientFundsException() {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(15)).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(10).price(150.0).minDuration(1).maxDuration(5).discount(0.0).isAvailable(true).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        Reservation reservation = new Reservation(1L, event, service, request.getStartingTime(), request.getEndingTime(), false, Status.PENDING);
        when(mapper.fromRequest(request, event, service)).thenReturn(reservation);

        Company company = Company.builder().id(1L).openingHours(LocalTime.of(11, 0)).closingHours(LocalTime.of(15, 0)).build();
        when(companyService.getByProviderId(anyLong())).thenReturn(company);

        when(repository.exists(any(Specification.class))).thenReturn(false);

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
                () -> this.service.createReservation(request, 1L, 1L));

        assertEquals("You do not have enough funds for this reservation!", exception.getMessage());
    }

    @Test
    @Tag("exception-handling")
    @DisplayName("Should throw ReservationDeadlineExceededException when reservation deadline has passed")
    void givenExceededReservationDeadline_whenCreateReservation_thenThrowReservationDeadlineExceededException() {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(3)).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(request, event, service);

        when(companyService.getByProviderId(anyLong())).thenReturn(new Company());

        ReservationDeadlineExceededException exception = assertThrows(ReservationDeadlineExceededException.class,
                () -> this.service.createReservation(request, 1L, 1L));

        assertEquals("Reservation deadline has passed for this service!", exception.getMessage());
    }

    private void mockMapper(ReservationRequestDto request, Event event, Service service) {
        Reservation reservation = new Reservation(1L, event, service, request.getStartingTime(), request.getEndingTime(), false, Status.PENDING);
        when(mapper.fromRequest(request, event, service)).thenReturn(reservation);
    }
}