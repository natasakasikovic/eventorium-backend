package com.iss.eventorium.category.dtos;

import com.iss.eventorium.shared.models.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequestDto {
    @NotBlank(message = "Status is mandatory")
    private Status status;
}
