package com.iss.eventorium.event.dtos.event;

import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaRequestDto {

    @Valid
    List<ActivityRequestDto> activities;
}
