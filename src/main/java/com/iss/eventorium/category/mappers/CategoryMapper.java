package com.iss.eventorium.category.mappers;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.shared.models.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    private final ModelMapper modelMapper;

    public Category fromRequest(CategoryRequestDto categoryRequestDto) {
        return modelMapper.map(categoryRequestDto, Category.class);
    }

    public CategoryResponseDto toResponse(Category category) {
        return modelMapper.map(category, CategoryResponseDto.class);
    }

    public PagedResponse<CategoryResponseDto> toPagedResponse(Page<Category> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public Category fromResponse(CategoryResponseDto category) {
        return modelMapper.map(category, Category.class);
    }
}
