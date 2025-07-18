package com.iss.eventorium.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.user.dtos.auth.LoginRequestDto;
import com.iss.eventorium.user.dtos.auth.UserTokenState;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestRestTemplateAuthHelper {

    private final TestRestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, String> tokenCache = new ConcurrentHashMap<>();

    public TestRestTemplateAuthHelper(TestRestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public<T> ResponseEntity<T> authorizedGet(String email, String url, Class<T> responseType, Object... uriVars) {
        HttpHeaders headers = getAuthHeaders(email);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, responseType, uriVars);
    }

    public<T> ResponseEntity<T> authorizedPost(String email, String url, Object body, Class<T> responseType, Object... uriVars) {
        HttpHeaders headers = getAuthHeaders(email);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType, uriVars);
    }

    public<T> ResponseEntity<T> authorizedPut(String email, String url, Object body, Class<T> responseType, Object... uriVars) {
        HttpHeaders headers = getAuthHeaders(email);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, responseType, uriVars);
    }

    public<T> ResponseEntity<T> authorizedPatch(String email, String url, Object body, Class<T> responseType, Object... uriVars) {
        HttpHeaders headers = getAuthHeaders(email);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.PATCH, entity, responseType, uriVars);
    }

    public<T> ResponseEntity<T> authorizedDelete(String email, String url, Class<T> responseType, Object... uriVars) {
        HttpHeaders headers = getAuthHeaders(email);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, responseType, uriVars);
    }

    private HttpHeaders getAuthHeaders(String email) {
        String token = tokenCache.computeIfAbsent(email, key -> {
            try {
                return login(email);
            } catch (Exception e) {
                throw new RuntimeException("Login failed for user: " + key, e);
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private String login(String email) throws JsonProcessingException {
        LoginRequestDto request = new LoginRequestDto(email, TestUtil.PASSWORD);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/auth/login", entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful())
            throw new RuntimeException("Login failed with status " + response.getStatusCode());

        UserTokenState tokenState = objectMapper.readValue(response.getBody(), UserTokenState.class);
        return tokenState.getJwt();
    }
}