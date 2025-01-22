package com.iss.eventorium.solution.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.services.CategoryProposalService;
import com.iss.eventorium.company.repositories.CompanyRepository;
import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.solution.dtos.products.*;
import com.iss.eventorium.solution.mappers.ProductMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.repositories.ProductRepository;
import com.iss.eventorium.solution.specifications.ProductSpecification;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
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

    private final ProductRepository repository;
    private final CompanyRepository companyRepository;
    private final AuthService authService;
    private final CategoryProposalService categoryProposalService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${image-path}")
    private String imagePath;

    public List<ProductSummaryResponseDto> getTopProducts(){
        Pageable pageable = PageRequest.of(0, 5);
        List<Product> products = repository.findTopFiveProducts(pageable);
        return products.stream().map(ProductMapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> getProducts(Pageable pageable) {
        Page<Product> products = repository.findAll(pageable);
        return ProductMapper.toPagedResponse(products);
    }

    public ProductDetailsDto getProduct(Long id) {
        Product product = find(id);
        return ProductMapper.toDetailsResponse(product, companyRepository.getCompanyByProviderId(product.getProvider().getId()));
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
        Product product = find(id);

        List<ImageResponseDto> images = new ArrayList<>();
        for(ImagePath imagePath : product.getImagePaths()) {
            byte[] image = getImage(id, imagePath);
            images.add(new ImageResponseDto(image, imagePath.getContentType()));
        }
        return images;
    }

    public ImagePath getImagePath(Long id) {
        Product product = find(id);
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

    private Product find(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product not found")
        );
    }

    public ProductResponseDto createProduct(CreateProductRequestDto request) {
        Product product = ProductMapper.fromCreateRequest(request);
        handleCategoryAndStatus(product);
        product.setProvider(authService.getCurrentUser());

        repository.save(product);
        return ProductMapper.toResponse(product);
    }

    private void handleCategoryAndStatus(Product product) {
        if (product.getCategory().getId() == null) {
            product.setStatus(Status.PENDING);
            categoryProposalService.handleCategoryProposal(product.getCategory());
        } else {
            product.setStatus(Status.ACCEPTED);
            Category category = entityManager.getReference(Category.class, product.getCategory().getId());
            product.setCategory(category);
        }
    }
}
