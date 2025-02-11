package com.iss.eventorium.solution.dtos.services;

import lombok.*;
import org.springframework.cglib.core.Local;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDto {
    private Long id;
    private Long eventId;
    private String eventName;
    private Long serviceId;
    private String serviceName;
    private LocalDate date;
    private LocalTime startingTime;
    private LocalTime endingTime;
}
