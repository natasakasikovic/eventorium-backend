package com.iss.eventorium.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.TestUtil;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.budget.BudgetItemRequestDto;
import com.iss.eventorium.solution.models.SolutionType;
import com.iss.eventorium.user.dtos.auth.LoginRequestDto;
import com.iss.eventorium.user.dtos.auth.UserTokenState;
import jakarta.servlet.Filter;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.iss.eventorium.TestUtil.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BudgetControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    void testGetBudget() throws Exception {
        String token = login(mockMvc, objectMapper, ORGANIZER_LOGIN);
        mockMvc.perform(get("/api/v1/events/{event-id}/budget", EVENT_WITH_BUDGET)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plannedAmount").value(85.0))
                .andExpect(jsonPath("$.spentAmount").value(80.0));
    }

    @Test
    void testGetBudget_eventDoesNotExist() throws Exception {
        String token = login(mockMvc, objectMapper, ORGANIZER_LOGIN);
        mockMvc.perform(get("/api/v1/events/{event-id}/budget", INVALID_EVENT)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void testGetBudget_shouldCreateNewBudget() throws Exception {
        String token = login(mockMvc, objectMapper, ORGANIZER_LOGIN);
        mockMvc.perform(get("/api/v1/events/{event-id}/budget", EVENT_WITHOUT_BUDGET)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plannedAmount").value(0.0))
                .andExpect(jsonPath("$.spentAmount").value(0.0));
    }

    @Test
    @Transactional
    void testPurchaseProduct() throws Exception {
        String token = login(mockMvc, objectMapper, ORGANIZER_LOGIN);
        BudgetItemRequestDto request = BudgetItemRequestDto.builder()
                .plannedAmount(10.0)
                .itemId(4L)
                .itemType(SolutionType.PRODUCT)
                .category(CategoryResponseDto.builder().id(7L).build())
                .build();

        mockMvc.perform(post("/api/v1/events/{event-id}/budget/purchase", EVENT_WITH_BUDGET)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.name").value("Decorative Balloons"))
                .andExpect(jsonPath("$.price").value(10.0))
                .andExpect(jsonPath("$.discount").value(0.0));
    }

    @Test
    @Transactional
    void testPurchaseProduct_insufficientFunds() throws Exception {
        String token = login(mockMvc, objectMapper, ORGANIZER_LOGIN);
        BudgetItemRequestDto request = BudgetItemRequestDto.builder()
                .plannedAmount(9.0)
                .itemId(4L)
                .itemType(SolutionType.PRODUCT)
                .category(CategoryResponseDto.builder().id(7L).build())
                .build();

        mockMvc.perform(post("/api/v1/events/{event-id}/budget/purchase", EVENT_WITH_BUDGET)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You do not have enough funds for this purchase!"));
    }

    @Test
    @Transactional
    void testPurchaseProduct_alreadyPurchased() throws Exception {
        String token = login(mockMvc, objectMapper, ORGANIZER_LOGIN);
        BudgetItemRequestDto request = BudgetItemRequestDto.builder()
                .plannedAmount(1000.0)
                .itemId(1L)
                .itemType(SolutionType.PRODUCT)
                .category(CategoryResponseDto.builder().id(9L).build())
                .build();

        mockMvc.perform(post("/api/v1/events/{event-id}/budget/purchase", EVENT_WITH_BUDGET)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Solution with the same category is already purchased!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/events/{event-id}/budget",
            "/api/v1/events/{event-id}/budget/purchase",
            "/api/v1/events/{event-id}/budget/budget-items",
            "/api/v1/budget-items"
    })
    void testUnauthorizedAccess(String urlTemplate) throws Exception {
        mockMvc.perform(get(urlTemplate, EVENT_WITH_BUDGET))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}
