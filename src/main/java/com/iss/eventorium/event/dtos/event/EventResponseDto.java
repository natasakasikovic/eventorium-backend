package com.iss.eventorium.event.dtos.event;

import com.iss.eventorium.event.dtos.budget.BudgetResponseDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.event.models.Privacy;
import com.iss.eventorium.shared.models.City;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
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
