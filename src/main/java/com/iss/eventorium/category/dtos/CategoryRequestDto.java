package com.iss.eventorium.category.dtos;

import com.iss.eventorium.shared.models.Status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDto {
    private String name;
    private String description;
}
