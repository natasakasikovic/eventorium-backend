package com.iss.eventorium.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.models.SolutionType;
import com.iss.eventorium.util.TestRestTemplateAuthHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.iss.eventorium.event.provider.BudgetProvider.VALID_CATEGORY;
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
    @Tag("get-budget")
    void givenExistingEventWithBudget_whenGetBudget_thenReturnBudgetDetails() {
        ResponseEntity<BudgetResponseDto> response = authHelper.authorizedGet(
                "organizer2@gmail.com",
                "/api/v1/events/{event-id}/budget",
                BudgetResponseDto.class,
                2L
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(20.0, Objects.requireNonNull(response.getBody()).getPlannedAmount());
        assertEquals(20.0, response.getBody().getSpentAmount());
    }

    @Test
    @Tag("get-budget")
    void givenWrongOrganizer_whenGetBudget_thenReturnForbidden() {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedGet(
                "organizer2@gmail.com",
                "/api/v1/events/{event-id}/budget",
                ExceptionResponse.class,
                EXISTING_EVENT
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to change this event.", response.getBody().getMessage());
    }

    @Test
    @Tag("get-budget")
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
    @Tag("purchase-product")
    void givenValidBudgetItemRequest_whenPurchaseProduct_thenReturnCreatedBudgetItemDetails() {
        BudgetItemRequestDto request = createBudgetItemRequest(10.0, NEW_BUDGET_ITEM);

        ResponseEntity<ProductResponseDto> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ProductResponseDto.class,
                EXISTING_EVENT
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ProductResponseDto body = response.getBody();
        assertNotNull(body);
        assertEquals(NEW_BUDGET_ITEM, body.getId());
        assertEquals("Decorative Balloons", body.getName());
        assertEquals(10.0, body.getPrice());
        assertEquals(0.0, body.getDiscount());
    }

    @Test
    @Tag("purchase-product")
    void givenInsufficientFunds_whenPurchaseProduct_thenReturnErrorMessage() {
        BudgetItemRequestDto request = createBudgetItemRequest(9.0, NEW_BUDGET_ITEM);

        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ExceptionResponse.class,
                EXISTING_EVENT
        );

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You do not have enough funds for this purchase!", response.getBody().getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.BudgetProvider#provideInvalidProducts")
    @Tag("purchase-product")
    void givenInvalidSolution_whenPurchaseProduct_thenReturnErrorMessage(Long id) {
        BudgetItemRequestDto request = createBudgetItemRequest(1000.0, id);

        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ExceptionResponse.class,
                EXISTING_EVENT
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product not found", response.getBody().getMessage());
    }

    @Test
    @Tag("purchase-product")
    void givenUnavailableProduct_whenPurchaseProduct_thenReturnErrorMessage() {
        BudgetItemRequestDto request = createBudgetItemRequest(1000.0, UNAVAILABLE_PRODUCT);

        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ExceptionResponse.class,
                EXISTING_EVENT
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You cannot purchase a product marked as unavailable!", response.getBody().getMessage());
    }

    @Test
    @Tag("purchase-product")
    void givenAlreadyPurchasedProduct_whenPurchaseProduct_thenReturnConflictWithMessage() {
        BudgetItemRequestDto request = createBudgetItemRequest(1000.0, PURCHASED_PRODUCT);

        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ExceptionResponse.class,
                EXISTING_EVENT
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Solution is already processed", response.getBody().getMessage());
    }

    @Test
    @Tag("purchase-product")
    void givenNonExistentProduct_whenPurchaseProduct_thenReturnNotFoundWithMessage() {
        BudgetItemRequestDto request = createBudgetItemRequest(1000.0, INVALID_PRODUCT);

        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ExceptionResponse.class,
                EXISTING_EVENT
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product not found", response.getBody().getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.BudgetProvider#provideInvalidBudgetItems")
    @Tag("purchase-product")
    void givenInvalidBudgetItemRequest_whenPurchaseProduct_thenThrowValidationError(
            BudgetItemRequestDto request,
            String expectedMessage
    ) {
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ExceptionResponse.class,
                EXISTING_EVENT
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedMessage, response.getBody().getMessage());
    }

    @Test
    @Tag("purchase-product")
    void givenNonExistentEvent_whenPurchaseProduct_thenReturnNotFoundWithMessage() {
        BudgetItemRequestDto request = createBudgetItemRequest(1000.0, NEW_BUDGET_ITEM);

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

    @Test
    @Tag("purchase-product")
    void givenValidRequest_whenPurchaseProduct_thenBudgetItemIsCreated() {
        BudgetItemRequestDto request = createBudgetItemRequest(15.0, NOT_PROCESSED_PRODUCT);

        ResponseEntity<ProductResponseDto> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/purchase",
                request,
                ProductResponseDto.class,
                EXISTING_EVENT
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ProductResponseDto body = response.getBody();
        assertNotNull(body);
        assertEquals(request.getItemId(), body.getId());
        assertTrue(request.getPlannedAmount() > body.getPrice() * (1 - body.getDiscount() / 100));
    }

    @ParameterizedTest
    @CsvSource({
            "organizernoevents@gmail.com,0",
            "organizer2@gmail.com,2",
            "organizer3@gmail.com,1",
    })
    @Tag("get-budget-items")
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

    @Test
    @Tag("get-budget-items")
    void givenEventWithBudgetItems_whenGetBudgetItems_thenReturnListOfItemsWithRestoredPrices() {
        ResponseEntity<BudgetItemResponseDto[]> response = authHelper.authorizedGet(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/budget-items",
                BudgetItemResponseDto[].class,
                EXISTING_EVENT
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BudgetItemResponseDto[] items = response.getBody();
        assertNotNull(items);
        assertTrue(items.length > 0);

        Optional<BudgetItemResponseDto> match = Arrays.stream(items)
                .filter(i -> i.getSolutionId().equals(1L))
                .findFirst();

        assertTrue(match.isPresent());
        assertEquals("Custom Invitations", match.get().getSolutionName());
        assertEquals(15.0, match.get().getSpentAmount());
    }


    private BudgetItemRequestDto createBudgetItemRequest(double plannedAmount, Long itemId) {
        return BudgetItemRequestDto.builder()
                .plannedAmount(plannedAmount)
                .itemId(itemId)
                .itemType(SolutionType.PRODUCT)
                .category(VALID_CATEGORY)
                .build();
    }

}
