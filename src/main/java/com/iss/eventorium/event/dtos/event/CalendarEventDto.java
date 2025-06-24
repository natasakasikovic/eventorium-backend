package com.iss.eventorium.event.dtos.event;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEventDto {
    private Long id;
    private String name;
    private LocalDate date;
}
