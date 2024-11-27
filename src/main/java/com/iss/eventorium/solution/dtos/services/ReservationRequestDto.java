package com.iss.eventorium.solution.dtos.services;

import lombok.*;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDto {
    private Long eventId;
    private Long serviceId;
    private Time from;
    private Time to;
}
