package com.iss.eventorium.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.user.dtos.auth.LoginRequestDto;
import com.iss.eventorium.user.dtos.auth.UserTokenState;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MockMvcAuthHelper {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final Map<String, String> tokenCache = new ConcurrentHashMap<>();

    public MockMvcAuthHelper(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public RequestBuilder authorizedGet(String email, String urlTemplate, Object... uriVars) {
        return get(urlTemplate, uriVars)
                .header("Authorization", "Bearer " + getToken(email));
    }

    public RequestBuilder authorizedPut(String email, String urlTemplate, Object body, Object... uriVars) throws Exception {
        return put(urlTemplate, uriVars)
                .header("Authorization", "Bearer " + getToken(email))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));
    }

    public RequestBuilder authorizedPost(String email, String urlTemplate, Object body, Object... uriVars) throws Exception {
        return post(urlTemplate, uriVars)
                .header("Authorization", "Bearer " + getToken(email))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));
    }

    public RequestBuilder authorizedDelete(String username, String urlTemplate, Object... uriVars) {
        return delete(urlTemplate, uriVars)
                .header("Authorization", "Bearer " + getToken(username));
    }

    private String getToken(String email) {
        return tokenCache.computeIfAbsent(email, key -> {
            try {
                return login(mockMvc, objectMapper, new LoginRequestDto(email, "pera"));
            } catch (Exception e) {
                throw new RuntimeException("Login failed for user: " + key, e);
            }
        });
    }

    private String login(MockMvc mockMvc, ObjectMapper objectMapper, LoginRequestDto request) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        UserTokenState tokenState = objectMapper.readValue(responseContent, UserTokenState.class);
        return tokenState.getJwt();
    }

}
