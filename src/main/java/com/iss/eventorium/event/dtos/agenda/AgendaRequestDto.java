package com.iss.eventorium.event.dtos.agenda;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaRequestDto {

    @Valid @NotNull(message = "Activities are required")
    List<ActivityRequestDto> activities;
}
