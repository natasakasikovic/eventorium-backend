package com.iss.eventorium.interaction.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewRequestDto {

    @NotBlank
    private String feedback;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;
}
