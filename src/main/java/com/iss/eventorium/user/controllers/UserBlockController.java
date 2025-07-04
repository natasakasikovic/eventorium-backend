package com.iss.eventorium.user.controllers;

import com.iss.eventorium.user.api.UserBlockApi;
import com.iss.eventorium.user.services.UserBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-blocking")
@RequiredArgsConstructor
public class UserBlockController implements UserBlockApi {

    private final UserBlockService service;

    @PostMapping("/{user-id}")
    public ResponseEntity<Void> blockUser(@PathVariable("user-id") Long id) {
        service.blockUser(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}