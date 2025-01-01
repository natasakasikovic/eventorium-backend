package com.iss.eventorium.company.controllers;

import com.iss.eventorium.company.dtos.CompanyRequestDto;
import com.iss.eventorium.company.dtos.CompanyResponseDto;
import com.iss.eventorium.company.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<CompanyResponseDto> createCompany(@RequestBody CompanyRequestDto companyDto) {
        return ResponseEntity.ok(service.createCompany(companyDto));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<Void> uploadImages(@PathVariable Long id, @RequestParam("images") List<MultipartFile> images) {
        service.uploadImages(id, images);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> getCompany(@PathVariable Long id) {
        return null;
    }


    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyResponseDto> updateCompany(@PathVariable Long id, @RequestBody CompanyResponseDto company) throws Exception {
        return null;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable("id") Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
