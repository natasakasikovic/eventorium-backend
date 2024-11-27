package com.iss.eventorium.service.controllers;

import com.iss.eventorium.service.dtos.ReservationRequestDto;
import com.iss.eventorium.service.dtos.ReservationResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/reservations")
public class ReservationController {

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> getReservation(@PathVariable Long id) {
        // TODO: call -> reservationService.get(id);
        ReservationResponseDto reservation = new ReservationResponseDto();
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation (@RequestBody ReservationRequestDto reservation){
        // TODO: call -> reservationService.createReservation(reservation); and delete line below
        ReservationResponseDto reservationCreated = new ReservationResponseDto();
        return new ResponseEntity<>(reservationCreated, HttpStatus.CREATED);
    }

}
