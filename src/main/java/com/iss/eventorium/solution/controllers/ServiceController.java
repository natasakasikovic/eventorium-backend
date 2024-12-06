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
@RestController
@RequestMapping("api/v1/services")
public class ServiceController {

    @Autowired
    private ServiceService service; //TODO: when services attribute is deleted, remove this annotation and do it as in other controllers

    private final List<Service> services = List.of(
            Service.builder()
                    .id(1L)
                    .name("Haircut Service")
                    .description("A professional haircut and styling service")
                    .specialties("Hair Cutting")
                    .price(50.0)
                    .discount(10.0)
                    .status(Status.ACCEPTED)
                    .validFrom(LocalDateTime.now())
                    .isVisible(true)
                    .isAvailable(true)
                    .isDeleted(false)
                    .type(ReservationType.AUTOMATIC)
                    .reservationDeadline(LocalDate.now().plusDays(1))
                    .cancellationDeadline(LocalDate.now().plusDays(2))
                    .minDuration(30)
                    .maxDuration(60)
                    .build(),
            Service.builder()
                    .id(2L)
                    .name("Massage Therapy")
                    .description("Relaxing therapeutic massage")
                    .specialties("Massage")
                    .price(100.0)
                    .discount(20.0)
                    .status(Status.ACCEPTED)
                    .validFrom(LocalDateTime.now())
                    .isVisible(false)
                    .isAvailable(true)
                    .isDeleted(false)
                    .type(ReservationType.AUTOMATIC)
                    .reservationDeadline(LocalDate.now().plusDays(2))
                    .cancellationDeadline(LocalDate.now().plusDays(3))
                    .minDuration(60)
                    .maxDuration(90)
                    .build(),
            Service.builder()
                    .id(3L)
                    .name("Yoga Class")
                    .description("Group yoga classes for all levels")
                    .specialties("Yoga")
                    .price(25.0)
                    .discount(0.0)
                    .status(Status.ACCEPTED)
                    .validFrom(LocalDateTime.now())
                    .isVisible(true)
                    .isAvailable(true)
                    .isDeleted(false)
                    .type(ReservationType.MANUAL)
                    .reservationDeadline(LocalDate.now().plusDays(3))
                    .cancellationDeadline(LocalDate.now().plusDays(4))
                    .minDuration(45)
                    .maxDuration(60)
                    .build()
    );


    @GetMapping("/all")
    public ResponseEntity<List<ServiceResponseDto>> getAllServices() {
        return ResponseEntity.ok().body(services.stream().map(ServiceMapper::toResponse).toList());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> getServicesPaged(Pageable pageable) {
        return ResponseEntity.ok(service.getServicesPaged(pageable));
    }

    @GetMapping("/filter/all")
    public ResponseEntity<List<ServiceResponseDto>> filterServices(ServiceFilter filter) {
        return ResponseEntity
                .ok()
                .body(services.stream().map(ServiceMapper::toResponse).toList());
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> filteredServicesPaged(ServiceFilter filter, Pageable pageable) {
        return ResponseEntity
                .ok()
                .body(new PagedResponse<>(List.of(toSummaryResponse(services.get(0))), 1, 3));
    }

    @GetMapping("/search/all")
    public ResponseEntity<List<ServiceResponseDto>> searchServices(@RequestParam("keyword") String keyword) {
        return ResponseEntity
                .ok()
                .body(services.stream().map(ServiceMapper::toResponse).toList());
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> searchServicesPaged(
            @RequestParam("keyword") String keyword,
            Pageable pageable
    ) {
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(toSummaryResponse(services.get(0))), 1, 3));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> getService(@PathVariable("id") Long id) {
        return services.stream()
                .filter(service -> service.getId().equals(id))
                .findFirst()
                .map(service -> ResponseEntity.ok(ServiceMapper.toResponse(service)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDto> createService(
            @RequestBody CreateServiceRequestDto createServiceRequestDto
    ) {
        Service service = ServiceMapper.fromCreateRequest(createServiceRequestDto);
        service.setId(100L);
        return new ResponseEntity<>(ServiceMapper.toResponse(service), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> updateService(
            @PathVariable Long id,
            @RequestBody ServiceRequestDto serviceDto
    ) {
        if(id > services.size()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Service toUpdate = services.get(Math.toIntExact(id - 1));
        toUpdate.setName(serviceDto.getName());
        return new ResponseEntity<>(ServiceMapper.toResponse(toUpdate), HttpStatus.OK);
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
