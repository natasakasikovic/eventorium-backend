package com.iss.eventorium.solution.services;

import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository repository;

    public List<ProductSummaryResponseDto> getTopProducts(){
        Pageable pageable = PageRequest.of(0, 5); // TODO: think about getting pageable object from frontend
        List<Product> products = repository.findTopFiveProducts(pageable);
        return products.stream().map(ProductMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> getProducts(Pageable pageable) {
        Page<Product> products = repository.findAll(pageable);
        return ProductMapper.toPagedResponse(products);
    }

}
