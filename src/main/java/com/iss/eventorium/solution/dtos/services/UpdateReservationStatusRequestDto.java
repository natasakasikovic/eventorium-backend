package com.iss.eventorium.solution.dtos.services;

import com.iss.eventorium.shared.models.Status;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReservationStatusRequestDto {

    @NotNull(message = "Status is mandatory")
    private Status status;
}
