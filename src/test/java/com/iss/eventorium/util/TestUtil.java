package com.iss.eventorium.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.user.dtos.auth.LoginRequestDto;
import com.iss.eventorium.user.dtos.auth.UserTokenState;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtil {

    public static final LoginRequestDto ORGANIZER_LOGIN = new LoginRequestDto("organizer@gmail.com", "pera");
    public static final Long EVENT_WITH_BUDGET = 1L;
    public static final Long EVENT_WITHOUT_BUDGET = 6L;
    public static final Long INVALID_EVENT = 500L;
    public static final Long INVALID_PRODUCT = 500L;

    public static String login(MockMvc mockMvc, ObjectMapper objectMapper, LoginRequestDto request) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        UserTokenState tokenState = objectMapper.readValue(responseContent, UserTokenState.class);
        return tokenState.getJwt();
    }

    private static void resetTable(JdbcTemplate jdbcTemplate, String tableName) {
        jdbcTemplate.execute(String.format("DELETE FROM %s;", tableName));
        jdbcTemplate.execute(String.format("ALTER TABLE %s ALTER COLUMN id RESTART WITH 1;", tableName));
    }

    public static void resetTables(JdbcTemplate jdbcTemplate) {
        resetTable(jdbcTemplate, "roles");
        resetTable(jdbcTemplate, "cities");
        resetTable(jdbcTemplate, "users");
        resetTable(jdbcTemplate, "event_types");
        resetTable(jdbcTemplate, "events");
    }

}
