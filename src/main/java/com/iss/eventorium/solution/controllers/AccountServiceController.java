package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.solution.api.AccountServiceApi;
import com.iss.eventorium.solution.dtos.services.ServiceFilterDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.services.AccountServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/account/services")
@RequiredArgsConstructor
public class AccountServiceController implements AccountServiceApi {

    private final AccountServiceService accountService;

    @GetMapping("/all")
    public ResponseEntity<List<ServiceSummaryResponseDto>> getProfileServices() {
        return ResponseEntity.ok(accountService.getServices());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> getProfileServicesPaged(Pageable pageable) {
        return ResponseEntity.ok(accountService.getServicesPaged(pageable));
    }

    @GetMapping("/filter/all")
    public ResponseEntity<List<ServiceSummaryResponseDto>> filterAccountServices(
            @ModelAttribute ServiceFilterDto filter
    ) {
        return ResponseEntity.ok(accountService.filterServices(filter));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> filerAccountServicesPaged(
            @ModelAttribute ServiceFilterDto filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(accountService.filterServicesPaged(filter, pageable));
    }

    @GetMapping("/search/all")
    public ResponseEntity<List<ServiceSummaryResponseDto>> searchAccountServices(@RequestParam String keyword) {
        return ResponseEntity.ok(accountService.searchServices(keyword));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> searchAccountServicesPaged(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        return ResponseEntity.ok(accountService.searchServicesPaged(keyword, pageable));
    }

    @GetMapping("/favourites")
    public ResponseEntity<List<ServiceSummaryResponseDto>> getFavouriteServices() {
        return ResponseEntity.ok(accountService.getFavouriteServices());
    }

    @GetMapping("/favourites/{id}")
    public ResponseEntity<Boolean> isFavouriteService(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.isFavouriteService(id));
    }

    @PostMapping("/favourites/{id}")
    public ResponseEntity<ServiceResponseDto> addFavouriteService(@PathVariable Long id) {
        accountService.addFavouriteService(id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/favourites/{id}")
    public ResponseEntity<Void> removeFavouriteService(@PathVariable Long id) {
        accountService.removeFavouriteService(id);
        return ResponseEntity.noContent().build();
    }

}
