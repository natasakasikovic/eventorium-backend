package com.iss.eventorium.event.mappers;

import com.iss.eventorium.event.dtos.invitation.InvitationDetailsDto;
import com.iss.eventorium.event.dtos.invitation.InvitationRequestDto;
import com.iss.eventorium.event.dtos.invitation.InvitationResponseDto;
import com.iss.eventorium.event.models.Invitation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvitationMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public InvitationMapper(ModelMapper modelMapper) {
        InvitationMapper.modelMapper = modelMapper;
    }

    public static Invitation fromRequest(InvitationRequestDto invitationRequestDto) {
        return modelMapper.map(invitationRequestDto, Invitation.class);
    }

    public static InvitationResponseDto toResponse(Invitation invitation) {
        return modelMapper.map(invitation, InvitationResponseDto.class);
    }

    public static InvitationDetailsDto toInvitationDetails(Invitation invitation) {
        return modelMapper.map(invitation, InvitationDetailsDto.class);
    }
}
