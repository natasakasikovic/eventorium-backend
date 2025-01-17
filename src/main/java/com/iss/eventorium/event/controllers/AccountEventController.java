package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.event.EventSummaryResponseDto;
import com.iss.eventorium.event.services.AccountEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/account/events")
@RequiredArgsConstructor
public class AccountEventController {

    private final AccountEventService service;

    @GetMapping("/all")
    public ResponseEntity<List<EventSummaryResponseDto>> getAllEvents() {
        return ResponseEntity.ok(service.getEvents());
    }
}
