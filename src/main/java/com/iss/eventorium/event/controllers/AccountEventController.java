package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.event.EventSummaryResponseDto;
import com.iss.eventorium.event.services.AccountEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account/events")
@RequiredArgsConstructor
public class AccountEventController {
    private final AccountEventService service;

    @GetMapping("/favourites")
    public ResponseEntity<List<EventSummaryResponseDto>> getFavouriteEvents() {
        return ResponseEntity.ok(service.getFavouriteEvents());
    }

    @PostMapping("/favourites/{id}")
    public ResponseEntity<Void> addFavouriteEvent(@PathVariable Long id) {
        service.addFavouriteEvent(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/favourites/{id}")
    public ResponseEntity<Void> removeFavouriteEvent(@PathVariable Long id) {
        service.removeFavouriteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favourites/{id}")
    public ResponseEntity<Boolean> isFavouriteEvent(@PathVariable Long id) {
        return ResponseEntity.ok(service.isFavouriteEvent(id));
    }
}
