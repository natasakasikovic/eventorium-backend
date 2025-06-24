package com.iss.eventorium.solution.dtos.products;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequestDto {

    @NotBlank(message = "Name is mandatory")
    @Size(min = 1, max = 75, message = "Name must be 75 characters or fewer")
    private String name;

    @NotBlank(message = "Description is mandatory")
    @Size(min = 1, max = 750, message = "Description must be 750 characters or fewer")
    private String description;

    @NotNull(message = "Price is mandatory")
    @Min(value = 0, message = "Price must be non-negative")
    private Double price;

    @NotNull(message = "Discount is mandatory")
    @Min(value = 0, message = "Discount must be non-negative")
    @Max(value = 100, message = "Discount cannot exceed 100")
    private Double discount;

    @NotEmpty(message = "Event types are mandatory")
    private List<Long> eventTypesIds;

    @NotNull(message = "Availability is mandatory")
    private Boolean available;

    @NotNull(message = "Visibility is mandatory")
    private Boolean visible;
}
