package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.solution.api.ServiceApi;
import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.dtos.RemoveImageRequestDto;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.services.*;
import com.iss.eventorium.solution.services.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/services")
public class ServiceController implements ServiceApi {

    private final ServiceService service;

    @GetMapping("/top-five-services")
    public ResponseEntity<Collection<ServiceSummaryResponseDto>> getTopServices(){
        return new ResponseEntity<>(service.getTopServices(), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ServiceSummaryResponseDto>> getAllServices() {
        return ResponseEntity.ok(service.getServices());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> getServicesPaged(Pageable pageable) {
        return ResponseEntity.ok(service.getServicesPaged(pageable));
    }

    @GetMapping("/filter/all")
    public ResponseEntity<List<ServiceSummaryResponseDto>> filterServices(@Valid @ModelAttribute ServiceFilterDto filter) {
        return ResponseEntity.ok(service.filter(filter));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> filterServices(@Valid @ModelAttribute ServiceFilterDto filter, Pageable pageable) {
        return ResponseEntity.ok(service.filter(filter, pageable));
    }

    @GetMapping("/search/all")
    public ResponseEntity<List<ServiceSummaryResponseDto>> searchServices(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(service.searchServices(keyword));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>>searchServicesPaged(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(service.searchServices(keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDetailsDto> getService(@PathVariable("id") Long id) {
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
            @RequestParam("eventId") Long eventId,
            @RequestParam("price") Double price
    ) {
        return ResponseEntity.ok(service.getBudgetSuggestions(id, eventId, price));
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDto> createService(
            @Valid @RequestBody CreateServiceRequestDto createServiceRequestDto
    ) {
        return new ResponseEntity<>(service.createService(createServiceRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<Void> uploadServiceImages(
            @PathVariable Long id,
            @RequestParam("images") List<MultipartFile> images
    ) {
        service.uploadImages(id, images);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> updateService(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceRequestDto serviceDto
    ) {
        return ResponseEntity.ok(service.updateService(id, serviceDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable("id") Long id) {
        service.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<Void> deleteImages(@PathVariable("id") Long id, @RequestBody List<RemoveImageRequestDto> removedImages) {
        service.deleteImages(id, removedImages);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
