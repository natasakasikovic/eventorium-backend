package com.iss.eventorium.event.dtos.event;

import com.iss.eventorium.user.dtos.user.ChatUserDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailsDto {
    private String name;
    private String description;
    private String eventType;
    private String maxParticipants;
    private String privacy;
    private String address;
    private String city;
    private String date;
    private ChatUserDetailsDto organizer;
}
