package com.iss.eventorium.event.controllers;

import com.iss.eventorium.event.api.InvitationApi;
import com.iss.eventorium.event.dtos.invitation.InvitationDetailsDto;
import com.iss.eventorium.event.dtos.invitation.InvitationRequestDto;
import com.iss.eventorium.event.dtos.invitation.InvitationResponseDto;
import com.iss.eventorium.event.services.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/invitations")
public class InvitationController implements InvitationApi {

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

    @PostMapping("/{event-id}")
    public ResponseEntity<Void> sendInvitations(@RequestBody List<InvitationRequestDto> invitations, @PathVariable("event-id") Long id){
        service.sendInvitations(invitations, id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/my-invitations")
    public ResponseEntity<List<InvitationDetailsDto>> getInvitations() {
        return ResponseEntity.ok(service.getInvitations());
    }
}