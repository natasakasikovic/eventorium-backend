package com.iss.eventorium.company.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iss.eventorium.company.dtos.CompanyRequestDto;
import com.iss.eventorium.company.dtos.CompanyResponseDto;
import com.iss.eventorium.company.dtos.ProviderCompanyDto;
import com.iss.eventorium.company.dtos.UpdateRequestDto;
import com.iss.eventorium.company.services.CompanyService;
import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.dtos.RemoveImageRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService service;

    @PostMapping
    public ResponseEntity<CompanyResponseDto> createCompany(@Valid @RequestBody CompanyRequestDto companyDto) {
        return ResponseEntity.ok(service.createCompany(companyDto));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<Void> uploadImages(@PathVariable Long id, @RequestParam("images") List<MultipartFile> images) {
        service.uploadImages(id, images);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyResponseDto> updateCompany(@PathVariable Long id, @RequestBody CompanyResponseDto company) throws Exception {
        return null;
    }

    @GetMapping("/my-company")
    public ResponseEntity<ProviderCompanyDto> getCompany() {
        return ResponseEntity.ok(service.getCompany());
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<ImageResponseDto>> getImages(@PathVariable Long id) {
        return ResponseEntity.ok(service.getImages(id));
    }

    @PutMapping
    public ResponseEntity<CompanyResponseDto> updateCompany(@Valid @RequestBody UpdateRequestDto request) {
        return ResponseEntity.ok(service.updateCompany(request));
    }

    @PutMapping("/images")
    public ResponseEntity<Void> updateImages(@RequestParam(value="newImages", required = false) List<MultipartFile> newImages,
                                             @RequestParam("removedImages") String removedImagesJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<RemoveImageRequestDto> removedImages = objectMapper.readValue(removedImagesJson, new TypeReference<>() {});
        service.updateImages(newImages, removedImages);
        return ResponseEntity.ok().build();
    }

}
