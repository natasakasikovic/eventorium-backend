package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.solution.dtos.services.ReservationRequestDto;
import com.iss.eventorium.solution.dtos.services.ReservationResponseDto;
import com.iss.eventorium.solution.services.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class ReservationController {

    private final ReservationService service;

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> getReservation(@PathVariable Long id) {
        // TODO: call -> reservationService.get(id);
        ReservationResponseDto reservation = new ReservationResponseDto();
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @PostMapping("/events/{event-id}/services/{service-id}/reservation")
    public ResponseEntity<Void> createReservation (@Valid @RequestBody ReservationRequestDto reservation,
                                                   @PathVariable("event-id") Long eventId,
                                                   @PathVariable("service-id") Long serviceId) {
        service.createReservation(reservation, eventId, serviceId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
