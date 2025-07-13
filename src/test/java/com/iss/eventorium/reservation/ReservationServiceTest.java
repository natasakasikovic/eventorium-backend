package com.iss.eventorium.reservation;

import com.iss.eventorium.company.models.Company;
import com.iss.eventorium.company.services.CompanyService;
import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.event.services.BudgetService;
import com.iss.eventorium.event.services.EventService;
import com.iss.eventorium.shared.exceptions.InsufficientFundsException;
import com.iss.eventorium.shared.exceptions.OwnershipRequiredException;
import com.iss.eventorium.shared.models.City;
import com.iss.eventorium.shared.models.EmailDetails;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.shared.services.EmailService;
import com.iss.eventorium.solution.dtos.services.ReservationRequestDto;
import com.iss.eventorium.solution.exceptions.*;
import com.iss.eventorium.solution.mappers.ReservationMapper;
import com.iss.eventorium.solution.models.Reservation;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ReservationRepository;
import com.iss.eventorium.solution.services.ReservationService;
import com.iss.eventorium.solution.services.ServiceService;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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

    @InjectMocks
    ReservationService service;

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
    BudgetService budgetService;

    @Mock
    EmailService emailService;

    @Mock
    SpringTemplateEngine templateEngine;

    @Mock
    ReservationMapper mapper;

    @Captor
    private ArgumentCaptor<Reservation> reservationCaptor;

    private User currentUser;
    private User provider;
    private City city;
    private ReservationRequestDto request;

    @BeforeEach
    public void setUp() {
        currentUser = User.builder().id(1L).build();
        provider = User.builder().id(2L).build();
        city = City.builder().name("Trebinje").build();
        request = new ReservationRequestDto(LocalTime.of(11, 0), LocalTime.of(15, 0), 100.0);
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
    @DisplayName("Should throw ServiceNotAvailableException if trying to reserve for service marked as unavailable")
    void givenUnavailableService_whenCreateReservation_thenThrowServiceNotAvailableException() {
        Event event = Event.builder().organizer(currentUser).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().id(1L).provider(provider).isAvailable(false).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        ServiceNotAvailableException exception = assertThrows(ServiceNotAvailableException.class,
                () -> this.service.createReservation(request, 1L, 1L));

        assertEquals("You cannot make a reservation for service marked as unavailable!" , exception.getMessage());
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
    @Tag("reservation-deadline")
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

    @Test
    @Tag("reservation-deadline")
    @DisplayName("Should allow reservation exactly on the reservation deadline")
    public void givenReservationExactlyOnDeadline_whenValidateReservationDeadline_thenSuccess() {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).city(city).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(10).isAvailable(true).minDuration(4).maxDuration(4).price(100.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(request, event, service);
        mockDependenciesForSuccessfulReservation();

        Company company = Company.builder().openingHours(LocalTime.of(7, 0)).closingHours(LocalTime.of(17, 0)).build();
        when(companyService.getByProviderId(anyLong())).thenReturn(company);

        this.service.createReservation(request, 1L, 1L);

        verify(repository, times(1)).save(reservationCaptor.capture());
        verify(emailService, times(2)).sendSimpleMail(any(EmailDetails.class));

        Reservation savedReservation = reservationCaptor.getValue();
        assertThat(savedReservation.getStartingTime()).isEqualTo(LocalTime.of(11, 0));
        assertThat(savedReservation.getEndingTime()).isEqualTo(LocalTime.of(15, 0));
        assertThat(savedReservation.getEvent()).isEqualTo(event);
        assertThat(savedReservation.getService()).isEqualTo(service);
    }

    @ParameterizedTest
    @MethodSource("provideReservationsOutsideWorkingHours")
    @Tag("exception-handling")
    @Tag("working-hours")
    void givenReservationOutsideWorkingHours_whenCreateReservation_thenThrowReservationOutsideWorkingHours(LocalTime opening, LocalTime closing) {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().isAvailable(true).reservationDeadline(5).minDuration(1).maxDuration(6).provider(provider).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(request, event, service);

        Company company = Company.builder().openingHours(opening).closingHours(closing).build();
        when(companyService.getByProviderId(anyLong())).thenReturn(company);

        ReservationOutsideWorkingHoursException exception = assertThrows(ReservationOutsideWorkingHoursException.class,
                () -> this.service.createReservation(request, 1L, 1L)
        );

        assertEquals(String.format("Reservations can only be made between %s and %s", company.getOpeningHours(), company.getClosingHours()), exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideValidWorkingHours")
    @Tag("working-hours")
    void givenReservationWithinCompanyWorkingHours_whenCreateReservation_thenSuccess(LocalTime opening, LocalTime closing) {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).address("test-address").city(city).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().isAvailable(true).reservationDeadline(5).minDuration(1).maxDuration(6).provider(provider).price(10.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(request, event, service);
        mockDependenciesForSuccessfulReservation();

        Company company = Company.builder().openingHours(opening).closingHours(closing).build();
        when(companyService.getByProviderId(anyLong())).thenReturn(company);

        this.service.createReservation(request, 1L, 1L);

        verify(repository, times(1)).save(reservationCaptor.capture());
        verify(emailService, times(2)).sendSimpleMail(any(EmailDetails.class));

        Reservation savedReservation = reservationCaptor.getValue();
        assertThat(savedReservation.getStartingTime()).isEqualTo(LocalTime.of(11, 0));
        assertThat(savedReservation.getEndingTime()).isEqualTo(LocalTime.of(15, 0));
        assertThat(savedReservation.getEvent()).isEqualTo(event);
        assertThat(savedReservation.getService()).isEqualTo(service);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidReservations")
    @Tag("exception-handling")
    @Tag("service-duration")
    public void givenDurationOutsideAllowedRange_whenValidateServiceDuration_thenThrowInvalidServiceDurationException(LocalTime startingTime, LocalTime endingTime) {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).minDuration(2).maxDuration(6).price(10.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        ReservationRequestDto requestDto = ReservationRequestDto.builder().startingTime(startingTime).endingTime(endingTime).plannedAmount(100.0).build();
        mockMapper(requestDto, event, service);

        when(companyService.getByProviderId(anyLong())).thenReturn(new Company());

        InvalidServiceDurationException exception = assertThrows(InvalidServiceDurationException.class,
                () -> this.service.createReservation(requestDto, 1L, 1L));

        assertEquals(String.format("The service duration must be between %d and %d hours.", service.getMinDuration(), service.getMaxDuration()), exception.getMessage());
    }

    @Test
    @Tag("exception-handling")
    @Tag("service-duration")
    public void givenDurationNotEqualToFixedDuration_whenValidateServiceDuration_thenThrowInvalidServiceDurationException() {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).minDuration(6).maxDuration(6).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(request, event, service);

        when(companyService.getByProviderId(anyLong())).thenReturn(new Company());

        InvalidServiceDurationException exception = assertThrows(InvalidServiceDurationException.class,
                () -> this.service.createReservation(request, 1L, 1L));

        assertEquals(String.format("The service duration must be exactly %d hours.", service.getMinDuration()) , exception.getMessage());
    }

    @Test
    @Tag("service-duration")
    public void givenDurationEqualToFixedDuration_whenValidateServiceDuration_thenSuccess() {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).city(city).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).minDuration(4).maxDuration(4).price(10.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(request, event, service);
        mockDependenciesForSuccessfulReservation();

        Company company = Company.builder().openingHours(LocalTime.of(7, 0)).closingHours(LocalTime.of(17, 0)).build();
        when(companyService.getByProviderId(anyLong())).thenReturn(company);

        this.service.createReservation(request, 1L, 1L);

        verify(repository, times(1)).save(reservationCaptor.capture());
        verify(emailService, times(2)).sendSimpleMail(any(EmailDetails.class));

        Reservation saved = reservationCaptor.getValue();
        assertEquals(request.getStartingTime(), saved.getStartingTime());
        assertEquals(request.getEndingTime(), saved.getEndingTime());
    }

    @ParameterizedTest
    @MethodSource("provideValidReservations")
    @Tag("service-duration")
    public void givenDurationBetweenMinimumAndMaximum_whenValidateServiceDuration_thenSuccess(LocalTime startingTime, LocalTime endingTime) {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).city(city).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).minDuration(2).maxDuration(6).price(10.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        ReservationRequestDto requestDto = ReservationRequestDto.builder().startingTime(startingTime).endingTime(endingTime).plannedAmount(100.0).build();
        mockMapper(requestDto, event, service);
        mockDependenciesForSuccessfulReservation();

        Company company = Company.builder().openingHours(LocalTime.of(7, 0)).closingHours(LocalTime.of(17, 0)).build();
        when(companyService.getByProviderId(anyLong())).thenReturn(company);

        this.service.createReservation(requestDto, 1L, 1L);

        verify(repository, times(1)).save(reservationCaptor.capture());
        verify(emailService, times(2)).sendSimpleMail(any(EmailDetails.class));

        Reservation saved = reservationCaptor.getValue();
        assertEquals(startingTime, saved.getStartingTime());
        assertEquals(endingTime, saved.getEndingTime());
    }

    // NOTE: These arguments are used to test reservation creation when the reservation spans from 11:00 to 15:00. Each case defines different company working hours to verify correct behavior around working hours boundaries.
    private static Stream<Arguments> provideValidWorkingHours() {
        return Stream.of(
                Arguments.of(LocalTime.of(11, 0), LocalTime.of(15, 0)), // Company working hours exactly match the reservation (11:00 - 15:00)
                Arguments.of(LocalTime.of(8, 0), LocalTime.of(17, 0)), // Company working hours fully include the reservation (8:00 - 17:00)
                Arguments.of(LocalTime.of(11, 0), LocalTime.of(16, 0)), // Reservation starts exactly at opening time, ends before closing (11:00 - 16:00)
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(15, 0)) // Reservation starts after opening, ends exactly at closing time (10:00 - 15:00)
        );
    }

    // NOTE: These arguments are used to test reservation creation when the reservation spans from 11:00 to 15:00.
    private static Stream<Arguments> provideReservationsOutsideWorkingHours() {
        return Stream.of(
                Arguments.of(LocalTime.of(7, 0), LocalTime.of(14, 59)), // Company closes one minute before reservation ends (closing at 14:59, reservation ends at 15:00)
                Arguments.of(LocalTime.of(11, 1), LocalTime.of(16, 0)), // Company opens one minute after reservation starts (opening at 11:01, reservation starts at 11:00)
                Arguments.of(LocalTime.of(6, 0), LocalTime.of(10, 30))  // Company working hours completely outside the reservation request (company 6:00 - 10:30, reservation 11:00 - 15:00)
        );
    }

    // NOTE:  This method is used to test reservation durations against a service that allows durations between 2 and 6 hours (inclusive).
    private static Stream<Arguments> provideValidReservations() {
        return Stream.of(
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(12, 0)), // Exactly 2 hours (minimum valid duration)
                Arguments.of(LocalTime.of(13, 0), LocalTime.of(17, 0)), // Exactly 4 hours (within range)
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(16, 0)) // Exactly 6 hours (maximum valid duration)
        );
    }

    // NOTE: Provides invalid reservation time ranges for testing the validation of service duration.
    // This method is used to test reservation durations against a service  that allows durations between 2 and 6 hours.
    private static Stream<Arguments> provideInvalidReservations() {
        return Stream.of(
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(11, 59)), // One minute shorter than the minimum allowed duration (1h 59min)
                Arguments.of(LocalTime.of(9, 0), LocalTime.of(15, 1)),   // one minute longer than the maximum allowed duration (6h 1min)
                Arguments.of(LocalTime.of(12, 0), LocalTime.of(12, 0)),  // Duration of exactly zero hours (start and end time are the same)
                Arguments.of(LocalTime.of(14, 0), LocalTime.of(13, 0))   // Negative duration
        );
    }

    private void mockMapper(ReservationRequestDto request, Event event, Service service) {
        Reservation reservation = new Reservation(1L, event, service, request.getStartingTime(), request.getEndingTime(), false, Status.PENDING);
        when(mapper.fromRequest(request, event, service)).thenReturn(reservation);
    }

    private void mockDependenciesForSuccessfulReservation() {
        when(repository.exists(any(Specification.class))).thenReturn(false);
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("dummy-content");

        doNothing().when(emailService).sendSimpleMail(any(EmailDetails.class));
        doNothing().when(budgetService).addReservationAsBudgetItem(any(Reservation.class), anyDouble());
    }
}