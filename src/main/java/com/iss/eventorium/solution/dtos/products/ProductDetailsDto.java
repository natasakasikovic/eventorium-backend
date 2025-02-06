package com.iss.eventorium.solution.dtos.products;

import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.company.dtos.CompanyResponseDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.interaction.dtos.ratings.RatingResponseDto;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.user.dtos.user.UserDetailsDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailsDto {
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
    private UserDetailsDto provider;
    private CompanyResponseDto company;
}
