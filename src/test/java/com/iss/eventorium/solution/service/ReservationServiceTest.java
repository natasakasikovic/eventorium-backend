package com.iss.eventorium.solution.service;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 NOTE: Certain test cases—such as attempting to reserve a logically deleted service, an invisible service,
 or a service that has not been accepted—are not included here because such services are filtered out at
 the serviceRepository level. These cases are covered by the repository's test suite.

 Additionally, cases that trigger the same behavior (e.g., non-existing, deleted, invisible, or unaccepted services)
 have only one representative test to avoid duplication.
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
    void setUp() {
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
    // NOTE: Same behavior if you pass id of invisible, unaccepted or deleted service, so that cases won't be tested
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

        ReservationForPastEventException exception = assertThrows(ReservationForPastEventException.class,
                () -> this.service.createReservation(request, 1L, 1L));

        assertEquals("You cannot make a reservation for an event that has already passed." , exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#providePricesAndDiscountsThatDoNotFitPlannedAmount")
    @Tag("exception-handling")
    @Tag("service-funds")
    @DisplayName("Should throw InsufficientFundsException if planned amount is insufficient for service reservation")
    void givenInsufficientFunds_whenCreateReservation_thenThrowInsufficientFundsException(Double price, Double discount) {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(15)).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(10).price(price).minDuration(1).maxDuration(5).discount(discount).isAvailable(true).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(request, event, service);
        mockCompanyWorkingHours(LocalTime.of(11, 0), LocalTime.of(15, 0));

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
                () -> this.service.createReservation(request, 1L, 1L));

        assertEquals("You do not have enough funds for this reservation!", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#providePricesAndDiscountsThatFitPlannedAmount")
    @Tag("service-funds")
    @DisplayName("Should allow reservation when planned amount is exactly or higher equal to service price with discount applied")
    void givenPlannedAmountCoversServicePriceWithDiscount_whenCreateReservation_thenSuccess(Double price, Double discount) {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).city(city).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).minDuration(4).maxDuration(4).price(price).discount(discount).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(request, event, service);
        mockCompanyWorkingHours(LocalTime.of(7, 0), LocalTime.of(17, 0));
        mockDependenciesForSuccessfulReservation();

        this.service.createReservation(request, 1L, 1L);

        verify(repository, times(1)).save(reservationCaptor.capture());
        verify(emailService, times(2)).sendSimpleMail(any(EmailDetails.class));

        Reservation savedReservation = reservationCaptor.getValue();
        assertThat(savedReservation.getStartingTime()).isEqualTo(LocalTime.of(11, 0));
        assertThat(savedReservation.getEndingTime()).isEqualTo(LocalTime.of(15, 0));
        assertThat(savedReservation.getEvent()).isEqualTo(event);
        assertThat(savedReservation.getService()).isEqualTo(service);
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

        ReservationDeadlineExceededException exception = assertThrows(ReservationDeadlineExceededException.class,
                () -> this.service.createReservation(request, 1L, 1L));

        assertEquals("Reservation deadline has passed for this service!", exception.getMessage());
    }

    @Test
    @Tag("reservation-deadline")
    @DisplayName("Should allow reservation exactly on the reservation deadline")
    void givenReservationExactlyOnDeadline_whenValidateReservationDeadline_thenSuccess() {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).city(city).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(10).isAvailable(true).minDuration(4).maxDuration(4).price(100.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(request, event, service);
        mockDependenciesForSuccessfulReservation();
        mockCompanyWorkingHours(LocalTime.of(7, 0), LocalTime.of(17, 0));

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
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideReservationsOutsideCompanyWorkingHours")
    @Tag("exception-handling")
    @Tag("working-hours")
    @DisplayName("Should throw ReservationOutsideWorkingHoursException when trying to create reservation outside company's working hours")
    void givenReservationOutsideWorkingHours_whenCreateReservation_thenThrowReservationOutsideWorkingHours(ReservationRequestDto reservationRequest) {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().isAvailable(true).reservationDeadline(5).minDuration(1).maxDuration(6).provider(provider).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        LocalTime companyOpeningHours = LocalTime.of(8, 0);
        LocalTime companyClosingHours = LocalTime.of(14, 0);

        mockMapper(reservationRequest, event, service);
        mockCompanyWorkingHours(companyOpeningHours, companyClosingHours);

        ReservationOutsideWorkingHoursException exception = assertThrows(ReservationOutsideWorkingHoursException.class,
                () -> this.service.createReservation(reservationRequest, 1L, 1L)
        );

        assertEquals(String.format("Reservations can only be made between %s and %s", companyOpeningHours, companyClosingHours), exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideReservationsWithinCompanyWorkingHours")
    @Tag("working-hours")
    @DisplayName("Should create reservation successfully when within company's working hours")
    void givenReservationWithinCompanyWorkingHours_whenCreateReservation_thenSuccess(ReservationRequestDto reservationRequest) {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).city(city).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().isAvailable(true).reservationDeadline(5).minDuration(1).maxDuration(6).provider(provider).price(10.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(reservationRequest, event, service);
        mockDependenciesForSuccessfulReservation();
        mockCompanyWorkingHours(LocalTime.of(8, 0), LocalTime.of(14, 0));

        this.service.createReservation(reservationRequest, 1L, 1L);

        verify(repository, times(1)).save(reservationCaptor.capture());
        verify(emailService, times(2)).sendSimpleMail(any(EmailDetails.class));

        Reservation savedReservation = reservationCaptor.getValue();
        assertThat(savedReservation.getStartingTime()).isEqualTo(reservationRequest.getStartingTime());
        assertThat(savedReservation.getEndingTime()).isEqualTo(reservationRequest.getEndingTime());
        assertThat(savedReservation.getEvent()).isEqualTo(event);
        assertThat(savedReservation.getService()).isEqualTo(service);
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideReservationsWithInvalidDurations")
    @Tag("exception-handling")
    @Tag("service-duration")
    @DisplayName("Should throw InvalidServiceDurationException when reservation duration is outside the allowed service duration range [minDuration, maxDuration]")
    void givenDurationOutsideAllowedRange_whenValidateServiceDuration_thenThrowInvalidServiceDurationException(ReservationRequestDto reservationRequest) {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).minDuration(2).maxDuration(6).price(10.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(reservationRequest, event, service);

        InvalidServiceDurationException exception = assertThrows(InvalidServiceDurationException.class,
                () -> this.service.createReservation(reservationRequest, 1L, 1L));

        assertEquals(String.format("The service duration must be between %d and %d hours.", service.getMinDuration(), service.getMaxDuration()), exception.getMessage());
    }

    @Test
    @Tag("exception-handling")
    @Tag("service-duration")
    @DisplayName("Should throw InvalidServiceDurationException when service has fixed duration and reservation duration does not match it")
    void givenDurationNotEqualToFixedDuration_whenValidateServiceDuration_thenThrowInvalidServiceDurationException() {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).minDuration(6).maxDuration(6).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(request, event, service);

        InvalidServiceDurationException exception = assertThrows(InvalidServiceDurationException.class,
                () -> this.service.createReservation(request, 1L, 1L));

        assertEquals(String.format("The service duration must be exactly %d hours.", service.getMinDuration()) , exception.getMessage());
    }

    @Test
    @Tag("service-duration")
    @DisplayName("Should successfully reserve when service has fixed duration and reservation duration matches it")
    void givenDurationEqualToFixedDuration_whenValidateServiceDuration_thenSuccess() {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).city(city).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).minDuration(4).maxDuration(4).price(10.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(request, event, service);
        mockCompanyWorkingHours(LocalTime.of(7, 0), LocalTime.of(17, 0));
        mockDependenciesForSuccessfulReservation();

        this.service.createReservation(request, 1L, 1L);

        verify(repository, times(1)).save(reservationCaptor.capture());
        verify(emailService, times(2)).sendSimpleMail(any(EmailDetails.class));

        Reservation saved = reservationCaptor.getValue();
        assertEquals(request.getStartingTime(), saved.getStartingTime());
        assertEquals(request.getEndingTime(), saved.getEndingTime());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideReservationsWithValidDurations")
    @Tag("service-duration")
    @DisplayName("Should successfully reserve when reservation duration is within the allowed service duration range [minDuration, maxDuration]")
    void givenReservationDurationForServiceWithRangeDuration_whenValidateServiceDuration_thenSuccess(ReservationRequestDto reservationRequest) {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).city(city).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).minDuration(2).maxDuration(6).price(10.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockMapper(reservationRequest, event, service);
        mockCompanyWorkingHours(LocalTime.of(7, 0), LocalTime.of(21, 0));
        mockDependenciesForSuccessfulReservation();

        this.service.createReservation(reservationRequest, 1L, 1L);

        verify(repository, times(1)).save(reservationCaptor.capture());
        verify(emailService, times(2)).sendSimpleMail(any(EmailDetails.class));

        Reservation saved = reservationCaptor.getValue();
        assertEquals(reservationRequest.getStartingTime(), saved.getStartingTime());
        assertEquals(reservationRequest.getEndingTime(), saved.getEndingTime());
    }

    @Test
    @Tag("exception-handling")
    @Tag("overlapping-reservations")
    @DisplayName("Should throw ReservationConflictException when reservation overlaps with an existing one")
    void givenOverlappingReservation_whenCreateReservation_thenThrowReservationConflictException() {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).city(city).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).minDuration(2).maxDuration(6).price(10.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockCompanyWorkingHours(LocalTime.of(7, 0), LocalTime.of(17, 0));

        when(repository.exists(any(Specification.class)))
                .thenReturn(false)  // No overlap for the first reservation
                .thenReturn(true);  // Overlap detected for the second reservation

        ReservationRequestDto request1 = new ReservationRequestDto(LocalTime.of(8, 0), LocalTime.of(12, 0), 100.0);
        mockMapper(request1, event, service);
        assertDoesNotThrow(() -> this.service.createReservation(request1, 1L, 1L)); // First reservation succeeds

        ReservationRequestDto request2 = new ReservationRequestDto(LocalTime.of(11, 55), LocalTime.of(14, 0), 150.0);
        mockMapper(request2, event, service);
        ReservationConflictException exception = assertThrows(ReservationConflictException.class, // Second reservation throws conflict exception
                () -> this.service.createReservation(request2, 1L, 1L));

        assertEquals("For your event, this service is already reserved during the selected time slot. Please choose a different time.", exception.getMessage());
    }

    @Test
    @Tag("overlapping-reservations")
    @DisplayName("Should create both reservations when time slots do not overlap (same service, same event)")
    void givenNonOverlappingReservations_whenCreateReservation_thenSuccess() {
        Event event = Event.builder().organizer(currentUser).date(LocalDate.now().plusDays(10)).city(city).build();
        when(eventService.find(anyLong())).thenReturn(event);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).minDuration(2).maxDuration(6).price(10.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockCompanyWorkingHours(LocalTime.of(7, 0), LocalTime.of(17, 0));

        when(repository.exists(any(Specification.class)))
                .thenReturn(false)  // No overlap for first reservation
                .thenReturn(false); // No overlap for second reservation

        ReservationRequestDto request1 = new ReservationRequestDto(LocalTime.of(8, 0), LocalTime.of(10, 0), 100.0);
        mockMapper(request1, event, service);
        assertDoesNotThrow(() -> this.service.createReservation(request1, 1L, 1L));

        ReservationRequestDto request2 = new ReservationRequestDto(LocalTime.of(10, 0), LocalTime.of(12, 0), 120.0);
        mockMapper(request2, event, service);
        assertDoesNotThrow(() -> this.service.createReservation(request2, 1L, 1L));
    }

    @Test
    @Tag("overlapping-reservations")
    @DisplayName("Should allow reservation of the same service at the same time for different events")
    void givenSameServiceDifferentEvents_whenCreateReservation_thenSuccess() {
        Event event1 = Event.builder().id(1L).organizer(currentUser).date(LocalDate.now().plusDays(10)).city(city).build();
        Event event2 = Event.builder().id(2L).organizer(currentUser).date(LocalDate.now().plusDays(10)).city(city).id(2L).build();

        when(eventService.find(1L)).thenReturn(event1);
        when(eventService.find(2L)).thenReturn(event2);

        Service service = Service.builder().provider(provider).reservationDeadline(5).isAvailable(true).minDuration(2).maxDuration(6).price(10.0).discount(0.0).build();
        when(serviceService.find(anyLong())).thenReturn(service);

        mockCompanyWorkingHours(LocalTime.of(7, 0), LocalTime.of(17, 0));

        when(repository.exists(any(Specification.class))).thenReturn(false);

        ReservationRequestDto request1 = new ReservationRequestDto(LocalTime.of(9, 0), LocalTime.of(11, 0), 100.0);
        mockMapper(request1, event1, service);
        assertDoesNotThrow(() -> this.service.createReservation(request1, 1L, 1L));

        ReservationRequestDto request2 = new ReservationRequestDto(LocalTime.of(9, 0), LocalTime.of(11, 0), 120.0);
        mockMapper(request2, event2, service);
        assertDoesNotThrow(() -> this.service.createReservation(request2, 2L, 1L));
    }

    private void mockMapper(ReservationRequestDto request, Event event, Service service) {
        Reservation reservation = new Reservation(1L, event, service, request.getStartingTime(), request.getEndingTime(), false, Status.PENDING);
        when(mapper.fromRequest(request, event, service)).thenReturn(reservation);
    }

    private void mockCompanyWorkingHours(LocalTime opening, LocalTime closing) {
        Company company = Company.builder().openingHours(opening).closingHours(closing).build();
        when(companyService.getByProviderId(anyLong())).thenReturn(company);
    }

    private void mockDependenciesForSuccessfulReservation() {
        when(repository.exists(any(Specification.class))).thenReturn(false);
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("dummy-content");

        doNothing().when(emailService).sendSimpleMail(any(EmailDetails.class));
        doNothing().when(budgetService).addReservationAsBudgetItem(any(Reservation.class), anyDouble());
    }
}