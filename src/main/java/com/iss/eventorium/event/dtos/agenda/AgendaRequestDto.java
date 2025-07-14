package com.iss.eventorium.event.dtos.agenda;

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
