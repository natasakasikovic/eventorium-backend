package com.iss.eventorium.event.dtos.agenda;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaRequestDto {
    @Valid
    @NotEmpty(message = "Agenda must contain at least one activity.")
    @NotNull(message = "Activities are required")
    private List<ActivityRequestDto> activities;
}
