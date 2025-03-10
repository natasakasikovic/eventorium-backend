package com.iss.eventorium.company.controllers;

import com.iss.eventorium.company.dtos.*;
import com.iss.eventorium.company.services.CompanyService;
import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.dtos.RemoveImageRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService service;

    @PostMapping
    public ResponseEntity<CompanyResponseDto> createCompany(@Valid @RequestBody CompanyRequestDto companyDto) {
        return new ResponseEntity<>(service.createCompany(companyDto), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<Void> uploadImages(@PathVariable Long id, @RequestParam("images") List<MultipartFile> images) {
        service.uploadImages(id, images);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/my-company")
    public ResponseEntity<ProviderCompanyDto> getCompany() {
        return ResponseEntity.ok(service.getCompany());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDetailsDto> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCompany(id));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<ImageResponseDto>> getImages(@PathVariable Long id) {
        return ResponseEntity.ok(service.getImages(id));
    }

    @PutMapping
    public ResponseEntity<CompanyResponseDto> updateCompany(@Valid @RequestBody UpdateCompanyRequestDto request) {
        return ResponseEntity.ok(service.updateCompany(request));
    }

    @PutMapping("/images")
    public ResponseEntity<Void> uploadNewImages(@RequestParam(value="newImages") List<MultipartFile> newImages) {
        service.uploadNewImages(newImages);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/images")
    public ResponseEntity<Void> removeImages(@RequestBody List<RemoveImageRequestDto> removedImages) {
        service.removeImages(removedImages);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
