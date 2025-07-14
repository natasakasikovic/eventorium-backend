package com.iss.eventorium.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.agenda.AgendaRequestDto;
import com.iss.eventorium.event.dtos.event.EventRequestDto;
import com.iss.eventorium.event.dtos.event.EventResponseDto;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.util.TestRestTemplateAuthHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static com.iss.eventorium.event.provider.EventProvider.provideValidAgenda;
import static com.iss.eventorium.event.provider.EventProvider.provideValidEventRequest;
import static com.iss.eventorium.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integration-test")
class EventControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private TestRestTemplateAuthHelper authHelper;

    @BeforeAll
    void setup() {
        authHelper = new TestRestTemplateAuthHelper(restTemplate, objectMapper);
    }

    @Test
    @DisplayName("Should return CREATED when event is successfully created")
    void givenValidRequest_whenCreateEvent_thenCreateEvent() {
        ResponseEntity<EventResponseDto> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events",
                provideValidEventRequest(),
                EventResponseDto.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        EventResponseDto body = response.getBody();
        assertNotNull(body);

        assertEquals("Test event", body.getName());
        assertEquals("Test city", body.getCity().getName());
        assertEquals("Test description", body.getDescription());
        assertEquals(Privacy.OPEN, body.getPrivacy());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.EventProvider#provideInvalidEventRequestsWithExpectedError")
    @DisplayName("Should return BAD_REQUEST and expected validation message for invalid EventRequestDto")
    void givenInvalidRequest_whenCreateEvent_thenReturnExpectedValidationMessage(EventRequestDto invalidRequest, String expectedMessage) {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events",
                invalidRequest,
                ExceptionResponse.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 Bad Request");
        assertNotNull(response.getBody());
        assertEquals(response.getBody().getMessage(), expectedMessage);
    }

    @Test
    @DisplayName("Should return OK when event is successfully updated with agenda")
    void givenValidRequest_whenCreateAgenda_thenReturnOk() {
        ResponseEntity<Void> response = authHelper.authorizedPut(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/agenda",
                provideValidAgenda(),
                Void.class,
                EVENT_WITHOUT_AGENDA_1
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.EventProvider#provideInvalidAgenda")
    @DisplayName("Should return BAD_REQUEST and expected validation message for invalid ActivityRequestDto")
    void givenInvalidRequest_whenCreateAgenda_thenReturnExpectedValidationMessage(AgendaRequestDto request, String expectedMessage) {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPut(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/agenda",
                request,
                ExceptionResponse.class,
                EVENT_WITHOUT_AGENDA_1
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 Bad Request");
        assertNotNull(response.getBody());
        assertEquals(expectedMessage, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return NOT_FOUND when creating agenda for non-existent event")
    void givenNonExistingEvent_whenCreateAgenda_thenReturnNotFoundException() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPut(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/agenda",
                provideValidAgenda(),
                ExceptionResponse.class,
                0
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Expected 404 Not Found");
        assertNotNull(response.getBody());
        assertEquals("Event not found", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return CONFLICT when creating agenda for event which already have one")
    void givenEventWithAgenda_whenCreateAgenda_thenReturnConflict() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPut(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/agenda",
                provideValidAgenda(),
                ExceptionResponse.class,
                EVENT_WITH_AGENDA
        );
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Expected 409 Conflict");
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().startsWith("Agenda already defined for event with name"));
    }

    @Test
    @DisplayName("Should return FORBIDDEN when organizer creates agenda for event they do not own")
    void givenUnauthorizedEvent_whenCreateAgenda_thenReturnForbidden() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPut(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/agenda",
                provideValidAgenda(),
                ExceptionResponse.class,
                FORBIDDEN_EVENT_ID
        );
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Expected 403 Forbidden");
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to manage this event.", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when creating agenda for event that is not in draft state")
    void givenEventNotInDraftState_whenCreateAgenda_thenReturnBadRequest() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPut(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/agenda",
                provideValidAgenda(),
                ExceptionResponse.class,
                EVENT_NOT_IN_DRAFT_STATE
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 Bad Request");
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().startsWith("Cannot add agenda to event with name"));
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when sending empty agenda")
    void givenEmptyRequest_whenCreateAgenda_thenReturnBadRequest() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPut(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/agenda",
                new AgendaRequestDto(new ArrayList<>()),
                ExceptionResponse.class,
                EVENT_WITHOUT_AGENDA_1
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 Bad Request");
        assertNotNull(response.getBody());
        assertEquals("Agenda must contain at least one activity.", response.getBody().getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.EventProvider#provideActivityRequestsWithInvalidTimeRange")
    @DisplayName("Should return BAD_REQUEST when sending invalid time range for activities")
    void givenInvalidTimeRanges_whenCreateAgenda_thenReturnBadRequest(ActivityRequestDto request, String expectedMessage) {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPut(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/agenda",
                new AgendaRequestDto(List.of(request)),
                ExceptionResponse.class,
                EVENT_WITHOUT_AGENDA_2
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 Bad Request");
        assertNotNull(response.getBody());
        assertEquals(expectedMessage, response.getBody().getMessage());
    }
}
