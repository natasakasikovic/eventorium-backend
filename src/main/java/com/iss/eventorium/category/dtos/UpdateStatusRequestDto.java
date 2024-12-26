package com.iss.eventorium.category.dtos;

import com.iss.eventorium.shared.models.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequestDto {
    @NotNull(message = "Status is mandatory")
    private Status status;
}
