package com.iss.eventorium.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.solution.models.SolutionType;
import com.iss.eventorium.util.MockMvcAuthHelper;
import jakarta.servlet.Filter;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.iss.eventorium.util.TestUtil.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integration-test")
class BudgetControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvcAuthHelper authHelper;

    @BeforeAll
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
        authHelper = new MockMvcAuthHelper(mockMvc, objectMapper);
    }

    @Test
    void givenExistingEventWithBudget_whenGetBudget_thenReturnBudgetDetails() throws Exception {
        mockMvc.perform(authHelper.authorizedGet(ORGANIZER_EMAIL, "/api/v1/events/{event-id}/budget", EVENT_WITH_BUDGET))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plannedAmount").value(85.0))
                .andExpect(jsonPath("$.spentAmount").value(80.0));
    }

    @Test
    void givenNonExistentEvent_whenGetBudget_thenReturnNotFoundWithMessage() throws Exception {
        mockMvc.perform(authHelper.authorizedGet(ORGANIZER_EMAIL, "/api/v1/events/{event-id}/budget", INVALID_EVENT))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Event not found"));
    }

    @Test
    @Transactional
    void givenValidBudgetItemRequest_whenPurchaseProduct_thenReturnCreatedBudgetItemDetails() throws Exception {
        BudgetItemRequestDto request = createBudgetItemRequest(10.0);
        mockMvc.perform(
                authHelper.authorizedPost(
                        ORGANIZER_EMAIL,
                        "/api/v1/events/{event-id}/budget/purchase",
                        request,
                        EVENT_WITH_BUDGET
                    ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.name").value("Decorative Balloons"))
                .andExpect(jsonPath("$.price").value(10.0))
                .andExpect(jsonPath("$.discount").value(0.0));
    }

    @Test
    @Transactional
    void givenInsufficientFunds_whenPurchaseProduct_thenReturnErrorMessage() throws Exception {
        BudgetItemRequestDto request = createBudgetItemRequest(9.0);
        mockMvc.perform(
                authHelper.authorizedPost(
                        ORGANIZER_EMAIL,
                        "/api/v1/events/{event-id}/budget/purchase",
                        request,
                        EVENT_WITH_BUDGET
                ))
            .andExpect(jsonPath("$.message").value("You do not have enough funds for this purchase!"));
    }

    @Test
    @Transactional
    void givenAlreadyPurchasedProduct_whenPurchaseProduct_thenReturnConflictWithMessage() throws Exception {
        BudgetItemRequestDto request = BudgetItemRequestDto.builder()
                .plannedAmount(1000.0)
                .itemId(1L)
                .itemType(SolutionType.PRODUCT)
                .category(CategoryResponseDto.builder().id(9L).build())
                .build();

        mockMvc.perform(
                authHelper.authorizedPost(
                        ORGANIZER_EMAIL,
                        "/api/v1/events/{event-id}/budget/purchase",
                        request,
                        EVENT_WITH_BUDGET
                ))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Solution is already processed"));
    }

    @Test
    @Transactional
    void givenNonExistentProduct_whenPurchaseProduct_thenReturnNotFoundWithMessage() throws Exception {
        BudgetItemRequestDto request = BudgetItemRequestDto.builder()
                .plannedAmount(1000.0)
                .itemId(INVALID_PRODUCT)
                .itemType(SolutionType.PRODUCT)
                .category(CategoryResponseDto.builder().id(9L).build())
                .build();

        mockMvc.perform(
                authHelper.authorizedPost(
                        ORGANIZER_EMAIL,
                        "/api/v1/events/{event-id}/budget/purchase",
                        request,
                        EVENT_WITH_BUDGET
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.BudgetProvider#provideInvalidBudgetItems")
    @Transactional
    void givenInvalidBudgetItemRequest_whenPurchaseProduct_thenThrowValidationError(BudgetItemRequestDto request) throws Exception {
        mockMvc.perform(
                authHelper.authorizedPost(
                    ORGANIZER_EMAIL,
                    "/api/v1/events/{event-id}/budget/purchase",
                    request,
                    EVENT_WITH_BUDGET
                    ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", anyOf(
                        matchesPattern(".* is mandatory"),
                        is("Planned amount must be positive")
                )));
    }

    @Test
    void givenNonExistentEvent_whenPurchaseProduct_thenReturnNotFoundWithMessage() throws Exception {
        BudgetItemRequestDto request = createBudgetItemRequest(1000.0);
        mockMvc.perform(authHelper.authorizedPost(
                    ORGANIZER_EMAIL,
                    "/api/v1/events/{event-id}/budget/purchase",
                    request,
                    INVALID_EVENT
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Event not found"));
    }

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.BudgetProvider#provideBudgetItems")
    void givenUserEmail_whenGetAllBudgetItems_thenReturnExpectedSize(String email, int expectedSize) throws Exception {
        mockMvc.perform(authHelper.authorizedGet(email, "/api/v1/budget-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/events/{event-id}/budget",
            "/api/v1/events/{event-id}/budget/purchase",
            "/api/v1/events/{event-id}/budget/budget-items",
            "/api/v1/budget-items"
    })
    void givenNoAuthentication_whenAccessingProtectedUrls_thenReturnUnauthorized(String urlTemplate) throws Exception {
        mockMvc.perform(get(urlTemplate, EVENT_WITH_BUDGET))
                .andExpect(status().isUnauthorized());
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
