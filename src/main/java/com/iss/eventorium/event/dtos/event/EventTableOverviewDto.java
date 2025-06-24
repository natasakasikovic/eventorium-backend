package com.iss.eventorium.event.dtos.event;

import com.iss.eventorium.shared.dtos.CityDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EventTableOverviewDto {
    private Long id;
    private String name;
    private LocalDate date;
    private String privacy;
    private Integer maxParticipants;
    private CityDto city;
}
