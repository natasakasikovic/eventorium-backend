package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.solution.dtos.services.CreateServiceRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.shared.utils.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/profile/services")
public class ProfileServiceController {

    @GetMapping("/all")
    public ResponseEntity<List<ServiceSummaryResponseDto>> getProfileServices() {
        return ResponseEntity.ok().body(List.of(new ServiceSummaryResponseDto()));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> getProfileServicesPaged(Pageable pageable) {
        return ResponseEntity.ok().body(
                new PagedResponse<>(List.of(new ServiceSummaryResponseDto()), 3, 100));
    }

    @GetMapping("/filter/all")
    public ResponseEntity<List<ServiceSummaryResponseDto>> getProfileServicesFiltered() {
        return ResponseEntity.ok().body(List.of(new ServiceSummaryResponseDto()));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> getProfileServicesFilteredPaged(Pageable pageable) {
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(new ServiceSummaryResponseDto()), 3, 100));
    }

    @GetMapping("/search/all")
    public ResponseEntity<List<ServiceSummaryResponseDto>> getProfileServicesSearched(@RequestParam String keyword) {
        return ResponseEntity.ok().body(List.of(new ServiceSummaryResponseDto()));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> getProfileServicesSearched(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(new ServiceSummaryResponseDto()), 3, 100));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> getProfileService(@PathVariable Long id) {
        return ResponseEntity.ok().body(new ServiceResponseDto());
    }

    @GetMapping("/favourites/all")
    public ResponseEntity<List<ServiceSummaryResponseDto>> getFavouriteServices() {
        return ResponseEntity.ok().body(List.of(new ServiceSummaryResponseDto()));
    }

    @GetMapping("/favourites")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> getFavouriteServicesPaged(Pageable pageable) {
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(new ServiceSummaryResponseDto()), 3, 100));
    }

    @GetMapping("/favourites/{id}")
    public ResponseEntity<ServiceResponseDto> getFavouriteService(@PathVariable Long id) {
        return ResponseEntity.ok().body(new ServiceResponseDto());
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDto> createProfileService(@RequestBody CreateServiceRequestDto serviceRequestDto) {
        Service service = ServiceMapper.fromCreateRequest(serviceRequestDto);
        service.setId(5523L);
        return new ResponseEntity<>(ServiceMapper.toResponse(service), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> updateProfileService(
            @PathVariable Long id,
            @RequestBody ServiceRequestDto serviceRequestDto
    ) {
        Service service = ServiceMapper.fromRequest(serviceRequestDto);
        service.setId(id);
        return new ResponseEntity<>(ServiceMapper.toResponse(service), HttpStatus.OK);
    }

    @PutMapping("/favourites/{id}")
    public ResponseEntity<ServiceResponseDto> addFavouriteService(@PathVariable Long id) {
        return ResponseEntity.ok().body(new ServiceResponseDto());
    }

    @DeleteMapping("/favourites/{id}")
    public ResponseEntity<Void> removeFavouriteService(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfileService(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
