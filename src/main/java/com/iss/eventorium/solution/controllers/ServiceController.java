package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.services.ServiceService;
import com.iss.eventorium.solution.util.ServiceFilter;
import com.iss.eventorium.solution.dtos.services.CreateServiceRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/services")
public class ServiceController {

    private final ServiceService service;

    @GetMapping("/all")
    public ResponseEntity<List<ServiceResponseDto>> getAllServices() {
        return null;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> getServicesPaged(Pageable pageable) {
        return ResponseEntity.ok(service.getServicesPaged(pageable));
    }

    @GetMapping("/filter/all")
    public ResponseEntity<List<ServiceResponseDto>> filterServices(ServiceFilter filter) {
        return null;
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> filteredServicesPaged(ServiceFilter filter, Pageable pageable) {
        return null;
    }

    @GetMapping("/search/all")
    public ResponseEntity<List<ServiceResponseDto>> searchServices(@RequestParam("keyword") String keyword) {
        return null;
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> searchServicesPaged(
            @RequestParam("keyword") String keyword,
            Pageable pageable
    ) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> getService(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getService(id));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<ImageResponseDto>> getImages(@PathVariable Long id) {
        return ResponseEntity.ok(service.getImages(id));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long id) {
        ImagePath path = service.getImagePath(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(path.getContentType()))
                .body(service.getImage(id, path));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<ServiceSummaryResponseDto>> getBudgetSuggestions(
        @RequestParam("categoryId") Long id,
        @RequestParam("price") Double price
    ) {
        return ResponseEntity.ok(service.getBudgetSuggestions(id, price));
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDto> createService(
            @RequestBody CreateServiceRequestDto createServiceRequestDto
    ) {
        return ResponseEntity.ok(service.createService(createServiceRequestDto));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<String> uploadServiceImages(
            @PathVariable Long id,
            @RequestParam("images") List<MultipartFile> images
    ) {
        service.uploadImages(id, images);
        return ResponseEntity.ok(String.format("Uploaded images for %s service", id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> updateService(
            @PathVariable Long id,
            @RequestBody ServiceRequestDto serviceDto
    ) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable("id") Long id) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top-five-services")
    public ResponseEntity<Collection<ServiceSummaryResponseDto>> getTopServices(){
        return new ResponseEntity<>(service.getTopServices(), HttpStatus.OK);
    }

}
