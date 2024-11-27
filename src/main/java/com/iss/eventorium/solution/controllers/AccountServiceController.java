package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import com.iss.eventorium.shared.utils.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/account/services")
public class AccountServiceController {

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
    public ResponseEntity<List<ServiceSummaryResponseDto>> filterAccountServices() {
        return ResponseEntity.ok().body(List.of(new ServiceSummaryResponseDto()));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> filerAccountServicesPaged(Pageable pageable) {
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(new ServiceSummaryResponseDto()), 3, 100));
    }

    @GetMapping("/search/all")
    public ResponseEntity<List<ServiceSummaryResponseDto>> searchAccountServices(@RequestParam String keyword) {
        return ResponseEntity.ok().body(List.of(new ServiceSummaryResponseDto()));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> searchAccountServicesPaged(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(new ServiceSummaryResponseDto()), 3, 100));
    }

    @GetMapping("/favourites/all")
    public ResponseEntity<List<ServiceSummaryResponseDto>> getFavouriteServices() {
        return ResponseEntity.ok().body(List.of(new ServiceSummaryResponseDto()));
    }

    @GetMapping("/favourites")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> getFavouriteServicesPaged(Pageable pageable) {
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(new ServiceSummaryResponseDto()), 3, 100));
    }

    @PutMapping("/favourites/{id}")
    public ResponseEntity<ServiceResponseDto> addFavouriteService(@PathVariable Long id) {
        return ResponseEntity.ok().body(new ServiceResponseDto());
    }

    @DeleteMapping("/favourites/{id}")
    public ResponseEntity<Void> removeFavouriteService(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

}
