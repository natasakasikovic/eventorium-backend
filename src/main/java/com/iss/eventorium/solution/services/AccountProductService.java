package com.iss.eventorium.solution.services;

import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.products.ProductFilterDto;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.repositories.ProductRepository;
import com.iss.eventorium.solution.specifications.ProductSpecification;
import com.iss.eventorium.user.repositories.UserRepository;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountProductService {

    private final ProductRepository repository;
    private final AuthService authService;
    private final UserRepository userRepository;

    private final ProductMapper mapper;

    public List<ProductSummaryResponseDto> getFavouriteProducts() {
        return authService.getCurrentUser().getPerson().getFavouriteProducts()
                .stream()
                .filter(product -> !product.getIsDeleted())
                .map(mapper::toSummaryResponse).toList();
    }

    public ProductResponseDto addFavouriteProduct(Long id) {
        Product product = find(id);

        List<Product> favouriteProduct = authService.getCurrentUser().getPerson().getFavouriteProducts();
        if(!favouriteProduct.contains(product)) {
            favouriteProduct.add(product);
            userRepository.save(authService.getCurrentUser());
        }
        return mapper.toResponse(product);
    }

    public void removeFavouriteProduct(Long id) {
        Product product = find(id);

        List<Product> favouriteProducts = authService.getCurrentUser().getPerson().getFavouriteProducts();
        if(favouriteProducts.contains(product)) {
            favouriteProducts.remove(product);
            userRepository.save(authService.getCurrentUser());
        }
    }

    public Product find(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found."));
    }

    public Boolean isFavouriteProduct(Long id) {
        Product product = find(id);
        return authService.getCurrentUser().getPerson().getFavouriteProducts().contains(product);
    }

    public List<ProductSummaryResponseDto> getAll() {
        Specification<Product> specification = ProductSpecification.filterForProvider(authService.getCurrentUser());
        return repository.findAll(specification).stream().map(mapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> getProductsPaged(Pageable pageable) {
        Specification<Product> specification = ProductSpecification.filterForProvider(authService.getCurrentUser());
        return mapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<ProductSummaryResponseDto> searchProducts(String keyword) {
        Specification<Product> specification = ProductSpecification.filterByNameForProvider(keyword, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(mapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> searchProducts(String keyword, Pageable pageable) {
        Specification<Product> specification = ProductSpecification.filterByNameForProvider(keyword, authService.getCurrentUser());
        return mapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<ProductSummaryResponseDto> filterProducts(ProductFilterDto filter) {
        Specification<Product> specification = ProductSpecification.filterForProvider(filter, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(mapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> filterProducts(ProductFilterDto filter, Pageable pageable) {
        Specification<Product> specification = ProductSpecification.filterForProvider(filter, authService.getCurrentUser());
        return mapper.toPagedResponse(repository.findAll(specification, pageable));
    }
}
