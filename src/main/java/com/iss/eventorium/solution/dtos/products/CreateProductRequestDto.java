package com.iss.eventorium.solution.dtos.products;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequestDto {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 1000, message = "Description too long. Max number of characters is 1000.")
    private String description;

    @NotNull(message = "Price is mandatory")
    @Min(value = 0, message = "Price must be non-negative")
    private Double price;

    @NotNull(message = "Discount is mandatory")
    @Min(value = 0, message = "Discount must be non-negative")
    @Max(value = 100, message = "Discount cannot exceed 100")
    private Double discount;

    @NotEmpty(message = "Event types are mandatory")
    @Valid
    private List<EventTypeResponseDto> eventTypes;

    @NotNull(message = "Category is mandatory")
    @Valid
    private CategoryResponseDto category;

    @NotNull(message = "Visibility is mandatory")
    private Boolean isVisible;

    @NotNull(message = "Availability is mandatory")
    private Boolean isAvailable;
}
