package com.iss.eventorium.solution.mappers;

import com.iss.eventorium.category.mappers.CategoryMapper;
import com.iss.eventorium.company.mappers.CompanyMapper;
import com.iss.eventorium.company.models.Company;
import com.iss.eventorium.event.mappers.EventTypeMapper;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.products.*;
import com.iss.eventorium.solution.models.Product;

import com.iss.eventorium.user.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ModelMapper modelMapper;
    private final CategoryMapper categoryMapper;
    private final EventTypeMapper eventTypeMapper;
    private final CompanyMapper companyMapper;
    private final UserMapper userMapper;

    public ProductSummaryResponseDto toSummaryResponse(Product product) {
        ProductSummaryResponseDto dto = modelMapper.map(product, ProductSummaryResponseDto.class);
        dto.setRating(product.calculateAverageRating());
        return dto;
    }

    public PagedResponse<ProductSummaryResponseDto> toPagedResponse(Page<Product> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(this::toSummaryResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public ProductResponseDto toResponse(Product product) {
        ProductResponseDto dto = modelMapper.map(product, ProductResponseDto.class);
        dto.setCategory(categoryMapper.toResponse(product.getCategory()));
        dto.setEventTypes(product.getEventTypes().stream().map(eventTypeMapper::toResponse).toList());
        dto.setRating(product.calculateAverageRating());
        dto.setProvider(userMapper.toUserDetails(product.getProvider()));
        return dto;
    }

    public ProductDetailsDto toDetailsResponse(Product product, Company company) {
        ProductDetailsDto dto = modelMapper.map(product, ProductDetailsDto.class);
        dto.setCategory(categoryMapper.toResponse(product.getCategory()));
        dto.setEventTypes(product.getEventTypes().stream().map(eventTypeMapper::toResponse).toList());
        dto.setRating(product.calculateAverageRating());
        dto.setProvider(userMapper.toUserDetails(product.getProvider()));
        dto.setCompany(companyMapper.toResponse(company));
        return dto;
    }

    public Product fromCreateRequest(CreateProductRequestDto request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discount(request.getDiscount())
                .isVisible(request.getIsVisible())
                .isDeleted(false)
                .isAvailable(request.getIsAvailable())
                .ratings(new ArrayList<>())
                .category(categoryMapper.fromResponse(request.getCategory()))
                .eventTypes(request.getEventTypes().stream().map(eventTypeMapper::fromResponse).toList())
                .build();
    }

    public Product fromUpdateRequest(UpdateProductRequestDto request, Product toUpdate) {
        Product product = modelMapper.map(request, Product.class);
        product.setIsAvailable(request.getAvailable());
        product.setIsVisible(request.getVisible());
        product.setId(toUpdate.getId());
        product.setStatus(toUpdate.getStatus());
        product.setCategory(toUpdate.getCategory());
        product.setRatings(toUpdate.getRatings());
        product.setImagePaths(toUpdate.getImagePaths());
        product.setIsDeleted(toUpdate.getIsDeleted());
        product.setProvider(toUpdate.getProvider());
        return product;
    }
}
