package com.iss.eventorium.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.dtos.budget.UpdateBudgetItemRequestDto;
import com.iss.eventorium.event.models.BudgetItemStatus;
import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.models.SolutionType;
import com.iss.eventorium.util.TestRestTemplateAuthHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
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
        restTemplate.getRestTemplate().setRequestFactory(new JdkClientHttpRequestFactory());
    }

    @Test
    @Tag("get-budget")
    @DisplayName("Should return budget details for event with budget")
    void givenExistingEventWithBudget_whenGetBudget_thenReturnBudgetDetails() {
        ResponseEntity<BudgetResponseDto> response = authHelper.authorizedGet(
                ORGANIZER_EMAIL_2,
                "/api/v1/events/{event-id}/budget",
                BudgetResponseDto.class,
                ORGANIZER_2_BUDGET_ITEM
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(20.0, Objects.requireNonNull(response.getBody()).getPlannedAmount());
        assertEquals(20.0, response.getBody().getSpentAmount());
    }

    @Test
    @Tag("get-budget")
    @DisplayName("Should return FOBIDDEN when organizer does not own the event")
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
    @DisplayName("Should return NOT_FOUND when event does not exist")
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
    @DisplayName("Should create budget item when purchase is valid")
    void givenValidProductPurchaseRequest_whenPostPurchase_thenReturnCreatedItem() {
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
    @DisplayName("Should return UNPROCESSABLE_ENTITY when user has insufficient funds")
    void givenInsufficientFunds_whenPostPurchase_thenReturnUnprocessableEntity() {
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
    @MethodSource("com.iss.eventorium.event.provider.BudgetProvider#provideArchivedProducts")
    @Tag("purchase-product")
    @DisplayName("Should return NOT_FOUND when trying to purchase invalid product")
    void givenInvalidProduct_whenPostPurchase_thenReturnNotFound(Long id) {
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
    @DisplayName("Should return CONFLICT when purchasing unavailable product")
    void givenUnavailableProduct_whenPostPurchase_thenReturnConflict() {
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
    @DisplayName("Should return CONFLICT when product is already purchased")
    void givenAlreadyPurchasedProduct_whenPostPurchase_thenReturnConflict() {
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
    @DisplayName("Should return NOT_FOUND when product does not exist")
    void givenNonExistentProduct_whenPostPurchase_thenReturnNotFoundResponse() {
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
    @DisplayName("Should return BAD_REQUEST with proper message for invalid purchase data")
    void givenInvalidBudgetItemRequest_whenPostPurchase_thenReturnValidationError(
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
    @DisplayName("Should create budget item and return it when purchase is valid")
    void givenValidRequest_whenPostPurchase_thenCreateBudgetItem() {
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
    @DisplayName("Should return correct number of budget items for given user email")
    void givenUserEmail_whenGetAllBudgetItems_thenReturnExpectedItemCount(String email, int expectedSize) {
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
    @DisplayName("Should return budget items for given event with accurate data")
    void givenEventWithItems_whenGetBudgetItems_thenReturnItemsWithRestoredPrices() {
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

    @Test
    @Tag("delete-item")
    @DisplayName("Should delete budget item successfully when it exists and is not processed")
    void givenExistingUnprocessedBudgetItem_whenDelete_thenReturnNoContent() {
        ResponseEntity<Void> deleteResponse = authHelper.authorizedDelete(
                ORGANIZER_EMAIL_2,
                "/api/v1/events/{event-id}/budget/budget-items/{item-id}",
                Void.class,
                ORGANIZER_2_EVENT,
                PLANNED_BUDGET_ITEM
        );

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }

    @Test
    @Tag("delete-item")
    @DisplayName("Should return CONFLICT when deleting already processed budget item")
    void givenProcessedItem_whenDelete_thenReturnConflict() {
        ResponseEntity<ExceptionResponse> deleteResponse = authHelper.authorizedDelete(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/budget-items/{item-id}",
                ExceptionResponse.class,
                EXISTING_EVENT,
                PROCESSED_BUDGET_ITEM
        );

        assertEquals(HttpStatus.CONFLICT, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
        assertEquals("Solution is already processed", deleteResponse.getBody().getMessage());
    }

    @Test
    @Tag("update-item")
    @DisplayName("Should return UNPROCESSABLE_ENTITY when updated amount exceeds remaining funds")
    void givenNotEnoughFunds_whenPatchUpdateItem_thenReturnUnprocessableEntity() {
        UpdateBudgetItemRequestDto request = new UpdateBudgetItemRequestDto(0.0);
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPatch(
                ORGANIZER_EMAIL_2,
                "/api/v1/events/{event-id}/budget/budget-items/{item-id}",
                request,
                ExceptionResponse.class,
                ORGANIZER_2_EVENT,
                PLANNED_BUDGET_ITEM_2
        );

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You do not have enough funds for this purchase/reservation!", response.getBody().getMessage());
    }

    @Test
    @Tag("update-item")
    @DisplayName("Should return CONFLICT when updating already processed item")
    void givenProcessedItem_whenPatchUpdateItem_thenReturnConflict() {
        UpdateBudgetItemRequestDto request = new UpdateBudgetItemRequestDto(20.0);
        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPatch(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/budget-items/{item-id}",
                request,
                ExceptionResponse.class,
                EXISTING_EVENT,
                PROCESSED_BUDGET_ITEM
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Solution is already processed", response.getBody().getMessage());
    }

    @Test
    @Tag("update-item")
    @DisplayName("Should update planned amount of budget item when valid request is provided")
    void givenValidUpdateRequest_whenPatchUpdateItem_thenReturnUpdatedItem() {
        UpdateBudgetItemRequestDto request = new UpdateBudgetItemRequestDto(25.0);
        ResponseEntity<BudgetItemResponseDto> response = authHelper.authorizedPatch(
                ORGANIZER_EMAIL_2,
                "/api/v1/events/{event-id}/budget/budget-items/{item-id}",
                request,
                BudgetItemResponseDto.class,
                ORGANIZER_2_EVENT,
                PLANNED_BUDGET_ITEM_2
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom Invitations", response.getBody().getSolutionName());
        assertEquals(25.0, response.getBody().getPlannedAmount());
    }

    @Test
    @Tag("create-budget-item")
    @DisplayName("Should create budget item with PLANNED status")
    void givenValidRequest_whenPostBudgetItem_thenReturnCreateItem() {
        BudgetItemRequestDto request = createBudgetItemRequest(100.0, PURCHASABLE_PRODUCT);
        ResponseEntity<BudgetItemResponseDto> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/budget-items",
                request,
                BudgetItemResponseDto.class,
                EMPTY_BUDGET_EVENT
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(PURCHASABLE_PRODUCT, response.getBody().getSolutionId());
        assertEquals(BudgetItemStatus.PLANNED, response.getBody().getStatus());
        assertEquals("Party Favors", response.getBody().getSolutionName());
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.BudgetProvider#provideArchivedSolutions")
    @Tag("create-budget-item")
    @DisplayName("Should return NOT_FOUND when trying to add invalid solution to planner")
    void givenInvalidSolution_whenPostBudgetItem_thenReturnErrorMessage(Long id) {
        BudgetItemRequestDto request = createBudgetItemRequest(1000.0, id);

        ResponseEntity<ExceptionResponse> response = authHelper.authorizedPost(
                ORGANIZER_EMAIL,
                "/api/v1/events/{event-id}/budget/budget-items",
                request,
                ExceptionResponse.class,
                EXISTING_EVENT
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Solution not found", response.getBody().getMessage());
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
