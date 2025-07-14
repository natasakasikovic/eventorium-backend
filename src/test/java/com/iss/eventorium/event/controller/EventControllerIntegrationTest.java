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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.UncheckedIOException;
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

    @Test
    @DisplayName("Should return OK and byte[] which represents pdf file with guests")
    void givenValidEventId_whenGettingGuestList_thenGenerateGuestListPdf() {
        ResponseEntity<byte[]> response = authHelper.authorizedGet(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/guest-list-pdf",
                byte[].class,
                EVENT_WITH_GUESTS
        );
        byte[] pdfBytes = response.getBody();

        assertNotNull(pdfBytes, "PDF body should not be null");
        assertTrue(pdfBytes.length > 0, "PDF body should not be empty");

        assertEquals("attachment; filename=guest_list.pdf",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION),
                "Expected Content-Disposition header with correct filename");

        assertEquals("application/pdf",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE),
                "Expected Content-Type to be application/pdf");

        assertTrue(new String(pdfBytes, 0, 5).startsWith("%PDF-"), "PDF should start with '%PDF-'");

        String pdfText = getPdfText(pdfBytes);
        assertTrue(pdfText.contains("Birthday Celebration in Beograd"), "PDF should contain event name");
        assertTrue(pdfText.contains("Emily Johnson"), "PDF should contain guest name");
        assertTrue(pdfText.contains("provider@gmail.com"), "PDF should contain guest's email");
    }

    @Test
    @DisplayName("Should return OK and byte[] which represents pdf file without guests")
    void givenEventWithNoGuests_whenGettingGuestList_thenGeneratePdfWithTextAboutNoGuests() {
        ResponseEntity<byte[]> response = authHelper.authorizedGet(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/guest-list-pdf",
                byte[].class,
                EVENT_WITHOUT_GUESTS
        );
        byte[] pdfBytes = response.getBody();
        assertNotNull(pdfBytes, "PDF body should not be null");
        assertTrue(pdfBytes.length > 0, "PDF body should not be empty");
        String pdfText = getPdfText(pdfBytes).trim();
        assertEquals("No guests for event with name 'Wedding in Novi Sad'",
                    pdfText,
                    "The PDF should contain the message that there are no guests.");
    }

    private String getPdfText(byte[] pdfBytes) {
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read PDF content", e);
        }
    }


    @Test
    @DisplayName("Should return FORBIDDEN when organizer generates guest list pdf event they do not own")
    void givenInvalidEventId_whenGettingGuestList_thenReturnForbidden() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedGet(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/guest-list-pdf",
                ExceptionResponse.class,
                FORBIDDEN_EVENT_ID
        );
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Expected 403 Forbidden");
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to manage this event.", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should return NOT_FOUND when organizer tries to generate guest list pdf for event that does not exist")
    void givenNonExistingEvent_whenGeneratingGuestListPdf_thenReturnNotFound() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedGet(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/guest-list-pdf",
                ExceptionResponse.class,
                0
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Expected 404 Not Found");
        assertNotNull(response.getBody());
        assertEquals("Event not found", response.getBody().getMessage());
    }
}
