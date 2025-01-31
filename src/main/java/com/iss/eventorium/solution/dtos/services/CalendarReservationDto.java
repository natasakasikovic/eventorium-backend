package com.iss.eventorium.solution.dtos.services;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarReservationDto {
    private String eventName;
    private String serviceName;
    private LocalDate date;
}
