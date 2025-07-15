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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static com.iss.eventorium.util.TestUtil.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integration-test")
public class ReservationControllerIntegrationTest {

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
                VALID_EVENT_ID_FOR_RESERVATION,
                RESERVABLE_SERVICE_ID);

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
                RESERVABLE_SERVICE_ID);

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
                RESERVABLE_SERVICE_ID);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Expected 403 Forbidden");
        assertNotNull(response.getBody());
        assertEquals("You cannot make a reservation for an event you are not the organizer of!", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return NOT_FOUND when trying to reserve service for non-existing event")
    void givenNonExistingService_whenReserveService_thenReturnNotFoundException() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(ORGANIZER_EMAIL,
                RESERVATION_ENDPOINT,
                request,
                ExceptionResponse.class,
                VALID_EVENT_ID_FOR_RESERVATION,
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
                VALID_EVENT_ID_FOR_RESERVATION,
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
                VALID_EVENT_ID_FOR_RESERVATION,
                SERVICE_ID_RESERVATION_DEADLINE_EXPIRED);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 BadRequest");
        assertNotNull(response.getBody());
        assertEquals("Reservation deadline has passed for this service!", response.getBody().getMessage());
    }
}