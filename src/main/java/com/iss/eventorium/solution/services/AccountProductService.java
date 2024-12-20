package com.iss.eventorium.solution.services;

import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.repositories.ProductRepository;
import com.iss.eventorium.user.repositories.UserRepository;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountProductService {

    private final AuthService authService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<ProductSummaryResponseDto> getFavouriteServices() {
        return authService.getCurrentUser()
                .getPerson()
                .getFavouriteProducts()
                .stream()
                .map(ProductMapper::toSummaryResponse)
                .toList();
    }

    public ProductResponseDto addFavouriteService(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Service with id " + id + " not found")
        );
        List<Product> favouriteProduct = authService.getCurrentUser().getPerson().getFavouriteProducts();
        if(favouriteProduct.contains(product)) {
            throw new RuntimeException("This service is already in your favorites list!");
        }
        favouriteProduct.add(product);
        userRepository.save(authService.getCurrentUser());
        return ProductMapper.toResponse(product);
    }

    public void removeFavouriteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Service with id " + id + " not found")
        );
        List<Product> favouriteProduct = authService.getCurrentUser().getPerson().getFavouriteProducts();
        if(!favouriteProduct.contains(product)) {
            throw new EntityNotFoundException("Service with id " + id + " not found in list of favourite services");
        }
        favouriteProduct.remove(product);
        userRepository.save(authService.getCurrentUser());
    }

    public Boolean isFavouriteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Service with id " + id + " not found")
        );
        return authService.getCurrentUser().getPerson().getFavouriteProducts().contains(product);
    }
}
