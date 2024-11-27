package com.iss.eventorium.service.dtos;

import lombok.*;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDto {
    private Long id;
    private Long eventId;
    private Long serviceId;
    private Time from;
    private Time to;
}
