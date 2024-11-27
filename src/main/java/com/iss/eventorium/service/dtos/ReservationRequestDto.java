package com.iss.eventorium.service.dtos;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.service.models.Service;
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
