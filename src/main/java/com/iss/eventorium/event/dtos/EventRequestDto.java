package com.iss.eventorium.event.dtos;

import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.shared.models.City;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {
    private String name;
    private String description;
    private LocalDate date;
    private Privacy privacy;
    private Integer maxParticipants;
    private EventTypeResponseDto eventType = null;
    private City city;
    private String address;
//     private List<ActivityResponseDto> activities;
    private List<InvitationRequestDto> invitations;
}
