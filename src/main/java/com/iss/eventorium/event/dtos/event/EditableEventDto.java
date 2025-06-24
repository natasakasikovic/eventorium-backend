package com.iss.eventorium.event.dtos.event;

import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.shared.models.City;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditableEventDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate date;
    private Integer maxParticipants;
    private EventTypeResponseDto type;
    private City city;
    private String address;
}
