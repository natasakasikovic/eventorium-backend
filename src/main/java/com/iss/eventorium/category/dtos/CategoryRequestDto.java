package com.iss.eventorium.category.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDto {

    @NotBlank(message = "Name is mandatory")
    @Size(min = 1, max = 75)
    private String name;

    @NotBlank(message = "Description is mandatory")
    @Size(min = 1, max = 750)
    private String description;
}
