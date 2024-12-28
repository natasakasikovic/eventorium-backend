package com.iss.eventorium.event.dtos;

import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.shared.models.City;
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
public class EventResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate date;
    private Privacy privacy;
    private Integer maxParticipants;
    private EventTypeResponseDto type;
    private City city;
    private String address;
    private BudgetResponseDto budget;
}
