package com.iss.eventorium.solution.mappers;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.solution.models.Product;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

}
