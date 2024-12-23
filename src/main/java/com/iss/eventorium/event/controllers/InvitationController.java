package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.dtos.InvitationResponseDto;
import com.iss.eventorium.event.services.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("api/v1/invitations")
public class InvitationController {

    private final InvitationService service;

    @GetMapping("/verification/{hash}")
    public ResponseEntity<Void> verifyInvitation(@PathVariable String hash){
        service.verifyInvitation(hash);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{hash}")
    public ResponseEntity<InvitationResponseDto> getInvitation(@PathVariable String hash){
        return ResponseEntity.ok(service.getInvitation(hash));
    }

}