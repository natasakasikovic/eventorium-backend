package com.iss.eventorium.solution.services;

import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.products.ProductFilterDto;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.repositories.ProductRepository;
import com.iss.eventorium.solution.specifications.ProductSpecification;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.repositories.UserRepository;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.iss.eventorium.solution.mappers.ProductMapper.toPagedResponse;

@Service
@RequiredArgsConstructor
public class AccountProductService {

    private final AuthService authService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<ProductSummaryResponseDto> getFavouriteProducts() {
        return authService.getCurrentUser().getPerson().getFavouriteProducts()
                .stream().map(ProductMapper::toSummaryResponse).toList();
    }

    public ProductResponseDto addFavouriteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id " + id + " not found")
        );
        List<Product> favouriteProduct = authService.getCurrentUser().getPerson().getFavouriteProducts();
        if(favouriteProduct.contains(product)) {
            throw new RuntimeException("This product is already in your favorites list!");
        }
        favouriteProduct.add(product);
        userRepository.save(authService.getCurrentUser());
        return ProductMapper.toResponse(product);
    }

    public void removeFavouriteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id " + id + " not found")
        );
        List<Product> favouriteProduct = authService.getCurrentUser().getPerson().getFavouriteProducts();
        if(!favouriteProduct.contains(product)) {
            throw new EntityNotFoundException("Product with id " + id + " not found in list of favourite services");
        }
        favouriteProduct.remove(product);
        userRepository.save(authService.getCurrentUser());
    }

    public Boolean isFavouriteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id " + id + " not found")
        );
        return authService.getCurrentUser().getPerson().getFavouriteProducts().contains(product);
    }

    public List<ProductSummaryResponseDto> getAll() {
        User provider = authService.getCurrentUser();
        return productRepository.findByProvider_Id(provider.getId()).stream()
                .map(ProductMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> getProductsPaged(Pageable pageable) {
        User provider = authService.getCurrentUser();
        return toPagedResponse(productRepository.findByProvider_Id(provider.getId(), pageable));
    }

    public List<ProductSummaryResponseDto> searchProducts(String keyword) {
        User provider = authService.getCurrentUser();
        return productRepository.findAll(
                ProductSpecification.search(keyword, provider.getId()))
                .stream().map(ProductMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> searchProducts(String keyword, Pageable pageable) {
        User provider = authService.getCurrentUser();
        return toPagedResponse(productRepository.findAll(
                ProductSpecification.search(keyword, provider.getId()), pageable));
    }

    public List<ProductSummaryResponseDto> filterProducts(ProductFilterDto filter) {
        User provider = authService.getCurrentUser();
        return productRepository.findAll(
                ProductSpecification.filterBy(filter, provider.getId()))
                .stream().map(ProductMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> filterProducts(ProductFilterDto filter, Pageable pageable) {
        User provider = authService.getCurrentUser();
        return toPagedResponse(productRepository.findAll(
                ProductSpecification.filterBy(filter, provider.getId()), pageable));
    }
}
