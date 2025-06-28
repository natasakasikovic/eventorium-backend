package com.iss.eventorium.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.user.dtos.auth.LoginRequestDto;
import com.iss.eventorium.util.MockMvcAuthHelper;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeAll;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integration-test")
class AccountEventControllerIntegrationTest {

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

    @ParameterizedTest
    @MethodSource("com.iss.eventorium.event.provider.AccountEventProvider#provideOrganizerEvents")
    void testGetAllEvents(String email, int expected) throws Exception {
        mockMvc.perform(authHelper.authorizedGet(email,  "/api/v1/account/events/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expected));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/account/events/all",
            "/api/v1/account/events"
    })
    void testUnauthorizedAccess(String url) throws Exception {
        mockMvc.perform(get(url)).andExpect(status().isUnauthorized());
    }
}
