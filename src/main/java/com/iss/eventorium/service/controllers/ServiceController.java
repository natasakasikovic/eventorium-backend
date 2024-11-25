package com.iss.eventorium.service.controllers;

import com.iss.eventorium.service.dtos.*;
import com.iss.eventorium.service.mappers.ServiceMapper;
import com.iss.eventorium.service.models.ReservationType;
import com.iss.eventorium.service.models.Service;
import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.shared.utils.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("api/v1/services")
public class ServiceController {

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
        return ResponseEntity.ok().body(
                new PagedResponse<>(List.of(ServiceMapper.toSummaryResponse(services.get(0))), 1, 3));
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
        //Collection<ServiceSummaryResponseDto> topServices = serviceService.getTopServices();
        // TODO: uncomment line above once service is implemented and also delete dummy services below

        Collection<ServiceSummaryResponseDto> topServices = List.of(
                new ServiceSummaryResponseDto(1L, "Service 1", 4.5, true, true),
                new ServiceSummaryResponseDto(2L, "Service 2", 4.3, true, true),
                new ServiceSummaryResponseDto(3L, "Service 3", 4.0, true, true),
                new ServiceSummaryResponseDto(4L, "Service 4", 3.9, true, true),
                new ServiceSummaryResponseDto(5L, "Service 5", 3.8, true, true)
        );

        return new ResponseEntity<>(topServices, HttpStatus.OK);
    }
}
