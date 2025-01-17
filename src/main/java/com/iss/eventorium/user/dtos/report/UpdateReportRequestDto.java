package com.iss.eventorium.user.dtos.report;

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
public class UpdateReportRequestDto {
    @NotNull(message ="Status must not be null!")
    private Status status;
}
