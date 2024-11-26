package com.iss.eventorium.product.dtos;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.shared.models.EventType;
import com.iss.eventorium.shared.models.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private String specialties;
    private double price;
    private double discount;
    private Status status;
    private List<EventType> eventTypes;
    private Category category;
}
