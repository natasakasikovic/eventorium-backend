package com.iss.eventorium.solution.services;


import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.shared.services.PdfService;
import com.iss.eventorium.solution.dtos.pricelists.PriceListResponseDto;
import com.iss.eventorium.solution.dtos.pricelists.UpdatePriceRequestDto;
import com.iss.eventorium.solution.mappers.PriceListMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ProductRepository;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.solution.specifications.ProductSpecification;
import com.iss.eventorium.solution.specifications.ServiceSpecification;
import com.iss.eventorium.user.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class PriceListService {

    private final HistoryService historyService;
    private final AuthService authService;
    private final PdfService pdfService;
    private final ServiceService serviceService;
    private final ProductService productService;
    private final ServiceRepository serviceRepository;
    private final ProductRepository productRepository;

    private final PriceListMapper mapper;

    public List<PriceListResponseDto> getPriceListServices() {
        Specification<Service> specification = ServiceSpecification.filterForProvider(authService.getCurrentUser());
        return serviceRepository.findAll(specification).stream().map(mapper::toResponse).toList();
    }

    public PagedResponse<PriceListResponseDto> getPriceListServicesPaged(Pageable pageable) {
        Specification<Service> specification = ServiceSpecification.filterForProvider(authService.getCurrentUser());
        return mapper.toPagedResponse(serviceRepository.findAll(specification, pageable));
    }

    public PriceListResponseDto updateService(Long id, UpdatePriceRequestDto updateRequestDto) {
        Service service = serviceService.find(id);

        if(Objects.equals(service.getPrice(), updateRequestDto.getPrice())
                && Objects.equals(service.getDiscount(), updateRequestDto.getDiscount())) {
            return mapper.toResponse(service);
        }

        service.setPrice(updateRequestDto.getPrice());
        service.setDiscount(updateRequestDto.getDiscount());
        historyService.addMemento(service);

        return mapper.toResponse(serviceRepository.save(service));
    }

    public List<PriceListResponseDto> getPriceListProducts() {
        Specification<Product> specification = ProductSpecification.filterForProvider(authService.getCurrentUser());
        return productRepository.findAll(specification).stream().map(mapper::toResponse).toList();
    }

    public PagedResponse<PriceListResponseDto> getPriceListProductsPaged(Pageable pageable) {
        Specification<Product> specification = ProductSpecification.filterForProvider(authService.getCurrentUser());
        return mapper.toPagedResponse(productRepository.findAll(specification, pageable));
    }

    public PriceListResponseDto updateProduct(Long id, UpdatePriceRequestDto updateRequestDto) {
        Product product = productService.find(id);

        if (Objects.equals(product.getPrice(), updateRequestDto.getPrice())
                && Objects.equals(product.getDiscount(), updateRequestDto.getDiscount())) {
            return mapper.toResponse(product);
        }

        product.setPrice(updateRequestDto.getPrice());
        product.setDiscount(updateRequestDto.getDiscount());
        historyService.addMemento(product);

        return mapper.toResponse(productRepository.save(product));
    }

    public byte[] generatePdf() {
        List<PriceListResponseDto> data = new ArrayList<>(getPriceListProducts());
        data.addAll(getPriceListServices());

        return pdfService.generate("/templates/price-list-pdf.jrxml", data, new HashMap<>());
    }
}
