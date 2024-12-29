package com.iss.eventorium.category.mappers;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.shared.utils.PagedResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public CategoryMapper(ModelMapper modelMapper) {
        CategoryMapper.modelMapper = modelMapper;
    }

    public static Category fromRequest(CategoryRequestDto categoryRequestDto) {
        return modelMapper.map(categoryRequestDto, Category.class);
    }

    public static CategoryResponseDto toResponse(Category category) {
        return modelMapper.map(category, CategoryResponseDto.class);
    }

    public static PagedResponse<CategoryResponseDto> toPagedResponse(Page<Category> page) {
        return new PagedResponse<>(
                page.stream().map(CategoryMapper::toResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public static Category fromResponse(CategoryResponseDto category) {
        return modelMapper.map(category, Category.class);
    }
}
