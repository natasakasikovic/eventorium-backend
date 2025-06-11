package com.iss.eventorium.event.mappers;

import com.iss.eventorium.event.dtos.invitation.InvitationDetailsDto;
import com.iss.eventorium.event.dtos.invitation.InvitationRequestDto;
import com.iss.eventorium.event.dtos.invitation.InvitationResponseDto;
import com.iss.eventorium.event.models.Invitation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvitationMapper {

    private final ModelMapper modelMapper;

    public Invitation fromRequest(InvitationRequestDto invitationRequestDto) {
        return modelMapper.map(invitationRequestDto, Invitation.class);
    }

    public InvitationResponseDto toResponse(Invitation invitation) {
        return modelMapper.map(invitation, InvitationResponseDto.class);
    }

    public InvitationDetailsDto toInvitationDetails(Invitation invitation) {
        return modelMapper.map(invitation, InvitationDetailsDto.class);
    }
}
