package com.iss.eventorium.solution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.solution.dtos.services.ReservationRequestDto;
import com.iss.eventorium.util.TestRestTemplateAuthHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;
import java.util.stream.Stream;

import static com.iss.eventorium.solution.provider.ReservationProvider.provideReservationToCauseOverlapping;
import static com.iss.eventorium.solution.provider.ReservationProvider.provideValidReservationForFixedServiceDurationTest;
import static com.iss.eventorium.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integration-test")
class ReservationControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private TestRestTemplateAuthHelper authHelper;

    private ReservationRequestDto request;

    @BeforeAll
    void setup() {
        authHelper = new TestRestTemplateAuthHelper(restTemplate, objectMapper);
        request = new ReservationRequestDto(LocalTime.of(11,0), LocalTime.of(15, 0), 150.0);
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideInvalidReservationRequests")
    @DisplayName("Should return BAD_REQUEST and expected validation message for invalid ReservationRequestDto")
    void givenInvalidRequest_whenCreateReservation_thenReturnExpectedValidationMessage(ReservationRequestDto invalidRequest, String expectedMessage) {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                invalidRequest,
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION_1,
                RESERVABLE_SERVICE_ID_1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 BadRequest");
        assertNotNull(response.getBody());
        assertEquals(expectedMessage, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return NOT_FOUND when trying to reserve service for non-existing event")
    void givenNonExistingEvent_whenCreateReservation_thenReturnNotFoundException() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                request,
                ExceptionResponse.class,
                NON_EXISTENT_ENTITY_ID,
                RESERVABLE_SERVICE_ID_1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Expected 404 Not Found");
        assertNotNull(response.getBody());
        assertEquals("Event not found", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return FORBIDDEN when a user attempts to reserve a service for an event they do not own")
    void givenEventWithoutOwnership_whenCreateReservation_thenReturnForbiddenException() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                request,
                ExceptionResponse.class,
                EVENT_ID_NOT_OWNED_BY_LOGGED_IN_ORGANIZER,
                RESERVABLE_SERVICE_ID_1);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Expected 403 Forbidden");
        assertNotNull(response.getBody());
        assertEquals("You cannot make a reservation for an event you are not the organizer of!", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return NOT_FOUND when trying to reserve service for non-existing service")
    // NOTE: Same behavior if you pass id of invisible, unaccepted or deleted service, so that cases won't be tested
    void givenNonExistingService_whenReserveService_thenReturnNotFoundException() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                request,
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION_1,
                NON_EXISTENT_ENTITY_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Expected 404 Not Found");
        assertNotNull(response.getBody());
        assertEquals("Service not found", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return CONFLICT when trying to reserve unavailable service")
    void givenNotAvailableService_whenReserveService_thenReturnConflictException() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                request,
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION_1,
                UNAVAILABLE_SERVICE_ID);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Expected 409 Conflict");
        assertNotNull(response.getBody());
        assertEquals("You cannot make a reservation for service marked as unavailable!", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when trying to reserve a service which reservation deadline expired")
    void givenExceededReservationDeadline_whenReserveService_thenReturnBadRequestException() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                request,
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION_1,
                SERVICE_ID_RESERVATION_DEADLINE_EXPIRED);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 BadRequest");
        assertNotNull(response.getBody());
        assertEquals("Reservation deadline has passed for this service!", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when trying to reserve a service for event in past")
    void givenPastEvent_whenCreateReservation_thenThrowBadRequestException() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                request,
                ExceptionResponse.class,
                EVENT_IN_PAST_ID,
                RESERVABLE_SERVICE_ID_1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 BadRequest");
        assertNotNull(response.getBody());
        assertEquals("You cannot make a reservation for an event that has already passed.", response.getBody().getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideReservationsWithInvalidDurations")
    @DisplayName("Should return BAD_REQUEST when trying to reserve with duration which is not in range of service duration")
    void givenReservationDurationOutsideAllowedRange_whenValidateServiceDuration_thenThrowBadRequestException(ReservationRequestDto reservationRequest) {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                reservationRequest,
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION_1,
                SERVICE_ID_WITH_DURATION_RANGE_1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 BadRequest");
        assertNotNull(response.getBody());
        assertEquals("The service duration must be between 2 and 6 hours.", response.getBody().getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideReservationsWithValidDurations")
    @Tag("service-duration")
    @DisplayName("Should successfully reserve when reservation duration is within the allowed service duration range [minDuration, maxDuration]")
    void givenReservationDurationForServiceWithRangeDuration_whenValidateServiceDuration_thenSuccess(ReservationRequestDto reservationRequest) {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                reservationRequest,
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION_1,
                SERVICE_ID_WITH_DURATION_RANGE_2);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @Tag("service-duration")
    @DisplayName("Should return BAD_REQUEST when trying to reserve with duration which is not equal to fixed service duration")
    void givenReservationDurationNotEqualToFixedServiceDuration_whenValidateServiceDuration_thenThrowBadRequestException() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                request,
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION_1,
                SERVICE_ID_WITH_FIXED_DURATION);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 BadRequest");
        assertNotNull(response.getBody());
        assertEquals("The service duration must be exactly 5 hours.", response.getBody().getMessage());
    }

    @Test
    @Tag("service-duration")
    @DisplayName("Should successfully reserve when service has fixed duration and reservation duration matches it")
    void givenDurationEqualToFixedDuration_whenValidateServiceDuration_thenSuccess() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                provideValidReservationForFixedServiceDurationTest(),
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION_1,
                SERVICE_ID_WITH_FIXED_DURATION);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideReservationsOutsideCompanyWorkingHours")
    @DisplayName("Should return BAD_REQUEST when trying to create reservation outside company working hours")
    void givenReservationOutsideWorkingHours_whenCreateReservation_thenThrowBadRequestException() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                request,
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION_2,
                12);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 BadRequest");
        assertNotNull(response.getBody());
        assertEquals("Reservations can only be made between 08:00 and 14:00", response.getBody().getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideReservationsWithValidDurations")
    @DisplayName("Should return CREATED when successfully reserved")
    void givenReservationsForServiceWithRangeDuration_whenValidateServiceDuration_thenSuccess(ReservationRequestDto reservationRequest) {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                reservationRequest,
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION_1,
                SERVICE_ID_WITH_DURATION_RANGE_1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideReservationsWithinCompanyWorkingHours")
    @DisplayName("Should return CREATED when successfully reserved")
    void givenReservationWithinCompanyWorkingHours_whenCreateReservation_thenSuccess(ReservationRequestDto reservationRequest) {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                reservationRequest,
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION_1,
                RESERVABLE_SERVICE_ID_2);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Should return CONFLICT when reservations overlap, even for a minute (as in this case)")
    void givenOverlappingReservation_whenCreateReservation_thenReturnConflictException() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                provideReservationToCauseOverlapping(),
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION_2,
                OVERLAPPING_SERVICE_ID);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Expected 409 Conflict");
        assertNotNull(response.getBody());
        assertEquals("For your event, this service is already reserved during the selected time slot. Please choose a different time.", response.getBody().getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideReservationsThatFitPlannedAmount")
    @DisplayName("Should return CREATED when successfully reserved")
    void givenEnoughFunds_whenCreateReservation_thenSuccess(ReservationRequestDto reservationRequest) {
        Stream.of(SERVICE_WITH_DISCOUNT, SERVICE_WITHOUT_DISCOUNT)
                .forEach(serviceId -> {
                    ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                            RESERVATION_ENDPOINT,
                            reservationRequest,
                            ExceptionResponse.class,
                            VALID_EVENT_ID_FOR_RESERVATION_2,
                            serviceId);

                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    assertNull(response.getBody());
                });
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.solution.provider.ReservationProvider#provideReservationsThatDoNotFitPlannedAmount")
    @DisplayName("Should return UNPROCESSABLE_ENTITY when there is not enough funds for reservation")
    void givenInsufficientFundsForServiceWithDiscount_whenCreateReservation_thenReturnUnprocessableEntityException(ReservationRequestDto reservationRequest) {
        Stream.of(SERVICE_WITH_DISCOUNT, SERVICE_WITHOUT_DISCOUNT)
                .forEach(serviceId -> {
                    ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                            RESERVATION_ENDPOINT,
                            reservationRequest,
                            ExceptionResponse.class,
                            VALID_EVENT_ID_FOR_RESERVATION_1,
                            serviceId);

                    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode(), "Expected 409 Unprocessable entity");
                    assertNotNull(response.getBody());
                    assertEquals("You do not have enough funds for this reservation!", response.getBody().getMessage());
                });
    }
}