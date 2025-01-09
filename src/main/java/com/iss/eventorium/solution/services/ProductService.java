package com.iss.eventorium.solution.services;

import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.dtos.products.ProductFilterDto;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.repositories.ProductRepository;
import com.iss.eventorium.solution.specifications.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository repository;

    @Value("${image-path}")
    private String imagePath;

    public List<ProductSummaryResponseDto> getTopProducts(){
        Pageable pageable = PageRequest.of(0, 5); // TODO: think about getting pageable object from frontend
        List<Product> products = repository.findTopFiveProducts(pageable);
        return products.stream().map(ProductMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> getProducts(Pageable pageable) {
        Page<Product> products = repository.findAll(pageable);
        return ProductMapper.toPagedResponse(products);
    }

    public ProductResponseDto getProduct(Long id) {
        return ProductMapper.toResponse(repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id " + id + " not found")
        ));
    }

    public List<ProductSummaryResponseDto> getBudgetSuggestions(Long categoryId, Double price) {
        return repository.getBudgetSuggestions(categoryId, price).stream().map(ProductMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> search(String keyword, Pageable pageable) {
        if (keyword.isBlank()){
            return ProductMapper.toPagedResponse(repository.findAll(pageable));
        }
        return  ProductMapper.toPagedResponse(repository.findByNameContainingAllIgnoreCase(keyword, pageable));
    }

    public List<ImageResponseDto> getImages(Long id) {
        Product product = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Product with id %s not found", id)));

        List<ImageResponseDto> images = new ArrayList<>();
        for(ImagePath imagePath : product.getImagePaths()) {
            byte[] image = getImage(id, imagePath);
            images.add(new ImageResponseDto(image, imagePath.getContentType()));
        }
        return images;
    }

    public ImagePath getImagePath(Long id) {
        Product product = repository.findById(id)
                .orElseThrow( () -> new EntityNotFoundException(String.format("Product with id %s not found", id)));
        if(product.getImagePaths().isEmpty()) {
            throw new ImageNotFoundException("Image not found");
        }
        return product.getImagePaths().get(0);
    }

    public List<ProductSummaryResponseDto> search(String keyword) {
        List<Product> products = keyword.isBlank()
                ? repository.findAll()
                : repository.findByNameContainingAllIgnoreCase(keyword);

        return products.stream().map(ProductMapper::toSummaryResponse).toList();
    }

    public byte[] getImage(Long productId, ImagePath path) {
        String uploadDir = StringUtils.cleanPath(imagePath + "products/" + productId + "/");
        try {
            File file = new File(uploadDir + path.getPath());
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new ImageNotFoundException("Fail to read image" + path.getPath() + ":" + e.getMessage());
        }
    }

    public Collection<ProductSummaryResponseDto> getProducts() {
        return repository.findAll().stream().map(ProductMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> filter(ProductFilterDto filter, Pageable pageable) {
        Specification<Product> specification = ProductSpecification.filterBy(filter);
        return ProductMapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<ProductSummaryResponseDto> filter(ProductFilterDto filter) {
        List<Product> products = repository.findAll(ProductSpecification.filterBy(filter));
        return products.stream().map(ProductMapper::toSummaryResponse).toList();
    }
}
