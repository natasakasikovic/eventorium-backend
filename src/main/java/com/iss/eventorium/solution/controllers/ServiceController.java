package com.iss.eventorium.solution.controllers;

import com.iss.eventorium.solution.mappers.ServiceMapper;
import com.iss.eventorium.solution.models.ReservationType;
import com.iss.eventorium.solution.models.Service;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.solution.services.ServiceService;
import com.iss.eventorium.solution.util.ServiceFilter;
import com.iss.eventorium.solution.dtos.services.CreateServiceRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceRequestDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.iss.eventorium.solution.mappers.ServiceMapper.toSummaryResponse;

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
        return null;
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
        return null;
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDto> createService(
            @RequestBody CreateServiceRequestDto createServiceRequestDto
    ) {
        return ResponseEntity.ok(service.createService(createServiceRequestDto));
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
