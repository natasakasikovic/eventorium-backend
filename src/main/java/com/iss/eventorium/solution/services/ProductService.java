package com.iss.eventorium.solution.services;

import com.iss.eventorium.category.models.Category;
import com.iss.eventorium.category.services.CategoryProposalService;
import com.iss.eventorium.company.repositories.CompanyRepository;
import com.iss.eventorium.event.models.EventType;
import com.iss.eventorium.event.services.EventTypeService;
import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.dtos.RemoveImageRequestDto;
import com.iss.eventorium.shared.exceptions.ImageNotFoundException;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.shared.services.ImageService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository repository;
    private final CompanyRepository companyRepository;
    private final AuthService authService;
    private final ImageService imageService;
    private final HistoryService historyService;
    private final CategoryProposalService categoryProposalService;

    private final ProductMapper mapper;
    private final EventTypeService eventTypeService;

    @PersistenceContext
    private EntityManager entityManager;

    private static final String IMG_DIR_NAME = "products";

    public List<ProductSummaryResponseDto> getTopFiveProducts() {
        Specification<Product> specification = ProductSpecification.filterTopProducts(authService.getCurrentUser());
        List<Product> products = repository.findAll(specification).stream().limit(5).toList();
        return products.stream().map(mapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> getProducts(Pageable pageable) {
        Specification<Product> specification = ProductSpecification.filter(authService.getCurrentUser());
        return mapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<ProductSummaryResponseDto> getProducts() {
        Specification<Product> specification = ProductSpecification.filter(authService.getCurrentUser());
        return repository.findAll(specification).stream().map(mapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> filter(ProductFilterDto filter, Pageable pageable) {
        Specification<Product> specification = ProductSpecification.filterBy(filter, authService.getCurrentUser());
        return mapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<ProductSummaryResponseDto> filter(ProductFilterDto filter) {
        Specification<Product> specification = ProductSpecification.filterBy(filter, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(mapper::toSummaryResponse).toList();
    }

    public PagedResponse<ProductSummaryResponseDto> search(String keyword, Pageable pageable) {
        Specification<Product> specification = ProductSpecification.filterByName(keyword, authService.getCurrentUser());
        return mapper.toPagedResponse(repository.findAll(specification, pageable));
    }

    public List<ProductSummaryResponseDto> search(String keyword) {
        Specification<Product> specification = ProductSpecification.filterByName(keyword, authService.getCurrentUser());
        return repository.findAll(specification).stream().map(mapper::toSummaryResponse).toList();
    }

    public ProductDetailsDto getProduct(Long id) {
        Product product = find(id);
        return mapper.toDetailsResponse(product, companyRepository.getCompanyByProviderId(product.getProvider().getId()));
    }

    public List<ImageResponseDto> getImages(Long id) {
        return imageService.getImages(IMG_DIR_NAME, find(id));
    }

    public ImagePath getImagePath(Long id) {
        Product product = find(id);

        if (product.getImagePaths().isEmpty())
            throw new ImageNotFoundException("Image not found");

        return product.getImagePaths().get(0);
    }

    public byte[] getImage(Long id, ImagePath path) {
        return imageService.getImage(IMG_DIR_NAME, id, path);
    }

    public Product find(Long id) {
        Specification<Product> specification = ProductSpecification.filterById(id, authService.getCurrentUser());
        return repository.findOne(specification).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    public ProductResponseDto createProduct(CreateProductRequestDto request) {
        Product product = mapper.fromCreateRequest(request);
        handleCategoryAndStatus(product);
        product.setProvider(authService.getCurrentUser());

        repository.save(product);
        historyService.addMemento(product);

        return mapper.toResponse(product);
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

    public void uploadImages(Long productId, List<MultipartFile> images) {
        if (images.isEmpty()) return;

        Product product = find(productId);
        List<ImagePath> paths = imageService.uploadImages(IMG_DIR_NAME, productId, images);

        if (!paths.isEmpty()) {
            product.getImagePaths().addAll(paths);
            repository.save(product);
        }
    }

    public void deleteImages(Long id, List<RemoveImageRequestDto> removedImages) {
        Product product = find(id);
        product.getImagePaths().removeIf(image ->
                removedImages.stream().anyMatch(removed -> removed.getId().equals(image.getId()))
        );
        repository.save(product);
    }

    public ProductResponseDto updateProduct(Long id, UpdateProductRequestDto request) {
        Product toUpdate = find(id);

        List<EventType> eventTypes = eventTypeService.findAllById(request.getEventTypesIds());

        if (eventTypes.size() != request.getEventTypesIds().size()) {
            throw new EntityNotFoundException("Event types not found");
        }

        Product product = mapper.fromUpdateRequest(request, toUpdate);
        product.setEventTypes(eventTypes);
        historyService.addMemento(product);
        repository.save(product);
        return mapper.toResponse(product);
    }

    public void deleteProduct(Long id) {
        Product product = find(id);
        product.setIsDeleted(true);
        repository.save(product);
    }

}
