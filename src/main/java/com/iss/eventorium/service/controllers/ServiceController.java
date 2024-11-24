package com.iss.eventorium.service.controllers;

import com.iss.eventorium.service.dtos.CreateServiceRequestDto;
import com.iss.eventorium.service.dtos.ServiceListResponseDto;
import com.iss.eventorium.service.dtos.ServiceRequestDto;
import com.iss.eventorium.service.dtos.ServiceResponseDto;
import com.iss.eventorium.shared.utils.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/services")
public class ServiceController {

    @GetMapping("/all")
    public ResponseEntity<List<ServiceResponseDto>> getAllServices() {
        return ResponseEntity.ok().body(new ArrayList<>());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ServiceListResponseDto>> getServicesPaged(Pageable pageable) {
        return ResponseEntity.ok().body(new PagedResponse<>());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> getService(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(new ServiceResponseDto());
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDto> createService(
            @RequestBody CreateServiceRequestDto createServiceRequestDto
    ) {
        return new ResponseEntity<>(new ServiceResponseDto(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> updateService(
            @PathVariable Long id,
            @RequestBody ServiceRequestDto service
    ) {
        return new ResponseEntity<>(new ServiceResponseDto(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable("id") Long id) {
        return ResponseEntity.noContent().build();
    }
}
