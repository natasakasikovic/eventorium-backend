package com.iss.eventorium.users.controllers;

import com.iss.eventorium.users.dtos.LoginRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request) throws  Exception {

        // NOTE: for testing
        boolean isAuthenticated = request.getEmail().equals("example@example.com") && request.getPassword().equals("password");

        // TODO: call service
        // boolean isAuthenticated = authService.validateCredentials(request.getEmail(), request.getPassword());

        if (isAuthenticated) {
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
}
