package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.solution.api.ReservationApi;
import com.iss.eventorium.solution.dtos.services.CalendarReservationDto;
import com.iss.eventorium.solution.dtos.services.ReservationRequestDto;
import com.iss.eventorium.solution.dtos.services.ReservationResponseDto;
import com.iss.eventorium.solution.dtos.services.UpdateReservationStatusRequestDto;
import com.iss.eventorium.solution.services.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class ReservationController implements ReservationApi {

    private final ReservationService service;

    @PostMapping("/events/{event-id}/services/{service-id}/reservation")
    public ResponseEntity<Void> createReservation (@Valid @RequestBody ReservationRequestDto reservation,
                                                   @PathVariable("event-id") Long eventId,
                                                   @PathVariable("service-id") Long serviceId) {
        service.createReservation(reservation, eventId, serviceId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/provider-reservations")
    public ResponseEntity<List<CalendarReservationDto>> getProviderReservations() {
        return ResponseEntity.ok(service.getProviderReservations());
    }

    @GetMapping("/reservations/pending")
    public ResponseEntity<List<ReservationResponseDto>> getPendingReservations() {
        return ResponseEntity.ok(service.getPendingReservations());
    }

    @PatchMapping("/reservations/{id}")
    public ResponseEntity<ReservationResponseDto> updateReservation(@PathVariable Long id, @Valid @RequestBody UpdateReservationStatusRequestDto request) {
        return ResponseEntity.ok(service.updateReservation(id, request.getStatus()));
    }
}