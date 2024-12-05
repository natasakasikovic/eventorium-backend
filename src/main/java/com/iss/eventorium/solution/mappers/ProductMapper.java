package com.iss.eventorium.solution.mappers;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.solution.models.Product;

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
        return modelMapper.map(product, ProductSummaryResponseDto.class);
    }

    public static PagedResponse<ProductSummaryResponseDto> toPagedResponse(Page<Product> page) {
        return new PagedResponse<>(
                page.stream().map(ProductMapper::toSummaryResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
