package com.iss.eventorium.user.controllers;

import com.iss.eventorium.user.services.UserBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/user-blocking")
@RequiredArgsConstructor
public class UserBlockController {

    private final UserBlockService service;

    @PostMapping("/{user-id}")
    public ResponseEntity<Void> blockUser(@PathVariable("user-id") Long id) {
        service.blockUser(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}