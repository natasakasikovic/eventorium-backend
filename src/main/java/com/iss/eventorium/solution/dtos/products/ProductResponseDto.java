package com.iss.eventorium.solution.dtos.products;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.shared.models.Status;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private String specialties;
    private Double price;
    private Double discount;
    private Status status;
    private Boolean available;
    private Boolean visible;
    private List<EventTypeResponseDto> eventTypes;
    private CategoryResponseDto category;
    private Double rating;
}
