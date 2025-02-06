package com.iss.eventorium.event.dtos.invitation;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDetailsDto {
    private Long eventId;
    private String eventName;
    private String eventDate;
}
