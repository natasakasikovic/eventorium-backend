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
    @Size(min = 1, max = 75, message = "Name must be 75 characters or fewer")
    private String name;

    @NotBlank(message = "Description is mandatory")
    @Size(min = 1, max = 750, message = "Description must be 750 characters or fewer")
    private String description;
}
