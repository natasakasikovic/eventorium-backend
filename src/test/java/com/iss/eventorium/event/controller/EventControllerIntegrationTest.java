package com.iss.eventorium.event.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.event.dtos.event.AgendaRequestDto;
import com.iss.eventorium.event.dtos.event.EventRequestDto;
import com.iss.eventorium.event.dtos.event.EventResponseDto;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.util.TestRestTemplateAuthHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static com.iss.eventorium.util.TestUtil.EVENT_WITHOUT_AGENDA;
import static com.iss.eventorium.util.TestUtil.ORGANIZER_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.EventProvider#provideValidEventRequest")
    @DisplayName("Should create event when making POST request to endpoint - /api/v1/events")
    void shouldCreateEvent(EventRequestDto request) {
        ResponseEntity<EventResponseDto> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events",
                request,
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
    @DisplayName("Should return expected validation message for invalid EventRequestDto")
    void shouldReturnExpectedValidationMessage(EventRequestDto invalidRequest, String expectedMessage) throws Exception{
        ResponseEntity<String> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events",
                invalidRequest,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 Bad Request");
        assertNotNull(response.getBody());
        assertValidationMessage(response, expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.EventProvider#provideValidAgenda")
    @DisplayName("Should set agenda to event when making PUT request to endpoint - /api/v1/events/{id}/agenda")
    void shouldSetAgenda(AgendaRequestDto request) {
        ResponseEntity<Void> response = authHelper.authorizedPut(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/agenda",
                request,
                Void.class,
                EVENT_WITHOUT_AGENDA
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.EventProvider#provideInvalidAgenda")
    @DisplayName("Should return expected validation message for invalid ActivityRequestDto")
    void shouldReturnExpectedValidationMessage(AgendaRequestDto request, String expectedMessage) throws Exception {
        ResponseEntity<String> response = authHelper.authorizedPut(
                ORGANIZER_EMAIL,
                "/api/v1/events/{id}/agenda",
                request,
                String.class,
                EVENT_WITHOUT_AGENDA
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected 400 Bad Request");
        assertNotNull(response.getBody());
        assertValidationMessage(response, expectedMessage);
    }


    private void assertValidationMessage(ResponseEntity<String> response, String expectedMessage) throws Exception {
        JsonNode json = objectMapper.readTree(response.getBody());
        String actualMessage = json.get("message").asText();
        assertEquals(expectedMessage, actualMessage, "Validation message mismatch");
    }

}
