package com.iss.eventorium.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.models.SolutionType;
import com.iss.eventorium.util.TestRestTemplateAuthHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static com.iss.eventorium.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integration-test")
class BudgetControllerIntegrationTest {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    private TestRestTemplateAuthHelper authHelper;

    @BeforeAll
    void setup() {
        authHelper = new TestRestTemplateAuthHelper(restTemplate, objectMapper);
    }

    @Test
    void givenExistingEventWithBudget_whenGetBudget_thenReturnBudgetDetails() {
        ResponseEntity<BudgetResponseDto> response = authHelper.authorizedGet(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget",
                BudgetResponseDto.class,
                2L
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(20.0, Objects.requireNonNull(response.getBody()).getPlannedAmount());
        assertEquals(20.0, response.getBody().getSpentAmount());
    }

    @Test
    void givenNonExistentEvent_whenGetBudget_thenReturnNotFoundWithMessage() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedGet(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget",
                ExceptionResponse.class,
                INVALID_EVENT
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Event not found", response.getBody().getMessage());
    }

    @Test
    void givenValidBudgetItemRequest_whenPurchaseProduct_thenReturnCreatedBudgetItemDetails() {
        BudgetItemRequestDto request = createBudgetItemRequest(10.0);

        ResponseEntity<ProductResponseDto> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ProductResponseDto.class,
                EVENT_WITH_BUDGET
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ProductResponseDto body = response.getBody();
        assertNotNull(body);
        assertEquals(4L, body.getId());
        assertEquals("Decorative Balloons", body.getName());
        assertEquals(10.0, body.getPrice());
        assertEquals(0.0, body.getDiscount());
    }

    @Test
    void givenInsufficientFunds_whenPurchaseProduct_thenReturnErrorMessage() {
        BudgetItemRequestDto request = createBudgetItemRequest(9.0);

        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ExceptionResponse.class,
                EVENT_WITH_BUDGET
        );

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You do not have enough funds for this purchase!", response.getBody().getMessage());
    }

    @Test
    void givenAlreadyPurchasedProduct_whenPurchaseProduct_thenReturnConflictWithMessage() {
        BudgetItemRequestDto request = BudgetItemRequestDto.builder()
                .plannedAmount(1000.0)
                .itemId(1L)
                .itemType(SolutionType.PRODUCT)
                .category(CategoryResponseDto.builder().id(9L).build())
                .build();

        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ExceptionResponse.class,
                EVENT_WITH_BUDGET
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Solution is already processed", response.getBody().getMessage());
    }

    @Test
    void givenNonExistentProduct_whenPurchaseProduct_thenReturnNotFoundWithMessage() {
        BudgetItemRequestDto request = BudgetItemRequestDto.builder()
                .plannedAmount(1000.0)
                .itemId(INVALID_PRODUCT)
                .itemType(SolutionType.PRODUCT)
                .category(CategoryResponseDto.builder().id(9L).build())
                .build();

        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ExceptionResponse.class,
                EVENT_WITH_BUDGET
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product not found", response.getBody().getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.BudgetProvider#provideInvalidBudgetItems")
    void givenInvalidBudgetItemRequest_whenPurchaseProduct_thenThrowValidationError(BudgetItemRequestDto request) {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ExceptionResponse.class,
                EVENT_WITH_BUDGET
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        String message = response.getBody().getMessage();
        assertTrue(
                message.matches(".* is mandatory") || message.equals("Planned amount must be positive"),
                "Unexpected validation message: " + message
        );
    }

    @Test
    void givenNonExistentEvent_whenPurchaseProduct_thenReturnNotFoundWithMessage() {
        BudgetItemRequestDto request = createBudgetItemRequest(1000.0);

        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ExceptionResponse.class,
                INVALID_EVENT
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Event not found", response.getBody().getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.BudgetProvider#provideBudgetItems")
    void givenUserEmail_whenGetAllBudgetItems_thenReturnExpectedSize(String email, int expectedSize) {
        ResponseEntity<BudgetItemResponseDto[]> response = authHelper.authorizedGet(
                email,
                "/api/v1/budget-items",
                BudgetItemResponseDto[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BudgetItemResponseDto[] items = response.getBody();
        assertNotNull(items);
        assertEquals(expectedSize, items.length);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/events/{event-id}/budget",
            "/api/v1/events/{event-id}/budget/purchase",
            "/api/v1/events/{event-id}/budget/budget-items",
            "/api/v1/budget-items"
    })
    void givenNoAuthentication_whenAccessingProtectedUrls_thenReturnUnauthorized(String urlTemplate) {
        ResponseEntity<String> response = restTemplate.getForEntity(urlTemplate, String.class, EVENT_WITH_BUDGET);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private BudgetItemRequestDto createBudgetItemRequest(double plannedAmount) {
        return BudgetItemRequestDto.builder()
                .plannedAmount(plannedAmount)
                .itemId(4L)
                .itemType(SolutionType.PRODUCT)
                .category(CategoryResponseDto.builder().id(7L).build())
                .build();
    }

}
