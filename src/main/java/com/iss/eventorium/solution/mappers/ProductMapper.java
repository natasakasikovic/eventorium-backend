package com.iss.eventorium.solution.mappers;

import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.event.mappers.EventTypeMapper;
import com.iss.eventorium.interaction.models.Review;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.solution.models.Product;

import com.iss.eventorium.user.mappers.UserMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public ProductMapper(ModelMapper modelMapper) {
        ProductMapper.modelMapper = modelMapper;
    }

    public static ProductSummaryResponseDto toSummaryResponse(Product product) {
        ProductSummaryResponseDto dto = modelMapper.map(product, ProductSummaryResponseDto.class);
        try {
            dto.setRating(product.getReviews().stream().mapToInt(Review::getRating).average().orElse(0.0));
        } catch (NullPointerException e) {
            dto.setRating(0.0d);
        }
        return dto;
    }

    public static PagedResponse<ProductSummaryResponseDto> toPagedResponse(Page<Product> page) {
        return new PagedResponse<>(
                page.stream().map(ProductMapper::toSummaryResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public static ProductResponseDto toResponse(Product product) {
        ProductResponseDto dto = modelMapper.map(product, ProductResponseDto.class);
        dto.setCategory(CategoryMapper.toResponse(product.getCategory()));
        dto.setEventTypes(product.getEventTypes().stream().map(EventTypeMapper::toResponse).toList());
        try {
            dto.setRating(product.getReviews().stream().mapToInt(Review::getRating).average().orElse(0.0));
        } catch (NullPointerException e) {
            dto.setRating(0.0d);
        }
        dto.setProvider(UserMapper.toProviderResponse(product.getProvider()));
        return dto;
    }
}
