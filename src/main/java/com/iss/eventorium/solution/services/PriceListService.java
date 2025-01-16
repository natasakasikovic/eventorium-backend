package com.iss.eventorium.solution.services;


import com.iss.eventorium.shared.services.PdfService;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.pricelists.PriceListResponseDto;
import com.iss.eventorium.solution.dtos.pricelists.UpdatePriceRequestDto;
import com.iss.eventorium.solution.mappers.PriceListMapper;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.solution.repositories.ProductRepository;
import com.iss.eventorium.solution.repositories.ServiceRepository;
import com.iss.eventorium.user.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Pageable;

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

    private final ServiceRepository serviceRepository;
    private final ProductRepository productRepository;

    public List<PriceListResponseDto> getPriceListServices() {
        return serviceRepository.findByProvider_Id(authService.getCurrentUser().getId()).stream()
                .map(PriceListMapper::toResponse)
                .toList();
    }

    public PagedResponse<PriceListResponseDto> getPriceListServicesPaged(Pageable pageable) {
        return PriceListMapper.toPagedResponse(
                serviceRepository.findByProvider_Id(authService.getCurrentUser().getId(), pageable)
        );
    }

    public PriceListResponseDto updateService(Long id, UpdatePriceRequestDto updateRequestDto) {
        Service service = serviceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Service with id " + id + " not found")
        );

        if(Objects.equals(service.getPrice(), updateRequestDto.getPrice())
                && Objects.equals(service.getDiscount(), updateRequestDto.getDiscount())) {
            return PriceListMapper.toResponse(service);
        }

        service.setPrice(updateRequestDto.getPrice());
        service.setDiscount(updateRequestDto.getDiscount());
        historyService.addServiceMemento(service);

        return PriceListMapper.toResponse(serviceRepository.save(service));
    }

    public List<PriceListResponseDto> getPriceListProducts() {
        return productRepository.findByProvider_Id(authService.getCurrentUser().getId()).stream()
                .map(PriceListMapper::toResponse)
                .toList();
    }

    public PagedResponse<PriceListResponseDto> getPriceListProductsPaged(Pageable pageable) {
        return PriceListMapper.toPagedResponse(
                productRepository.findByProvider_Id(authService.getCurrentUser().getId(), pageable)
        );
    }

    public PriceListResponseDto updateProduct(Long id, UpdatePriceRequestDto updateRequestDto) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id " + id + " not found")
        );

        if(Objects.equals(product.getPrice(), updateRequestDto.getPrice())
                && Objects.equals(product.getDiscount(), updateRequestDto.getDiscount())) {
            return PriceListMapper.toResponse(product);
        }

        product.setPrice(updateRequestDto.getPrice());
        product.setDiscount(updateRequestDto.getDiscount());
        historyService.addProductMemento(product);

        return PriceListMapper.toResponse(productRepository.save(product));
    }

    public byte[] generatePdf() throws JRException {
        List<PriceListResponseDto> data = new ArrayList<>(getPriceListProducts());
        data.addAll(getPriceListServices());

        return pdfService.generate("/templates/price-list-pdf.jrxml", data, new HashMap<>());
    }
}
