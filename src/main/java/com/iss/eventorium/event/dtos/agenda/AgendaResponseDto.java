package com.iss.eventorium.event.dtos.agenda;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaResponseDto {
    private Long eventId;
    private List<ActivityResponseDto> activities;
}
