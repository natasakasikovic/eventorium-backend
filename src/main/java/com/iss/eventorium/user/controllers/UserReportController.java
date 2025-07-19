package com.iss.eventorium.user.controllers;

import com.iss.eventorium.user.api.UserReportApi;
import com.iss.eventorium.user.dtos.report.UpdateReportRequestDto;
import com.iss.eventorium.user.dtos.report.UserReportRequestDto;
import com.iss.eventorium.user.dtos.report.UserReportResponseDto;
import com.iss.eventorium.user.services.UserReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-reports")
public class UserReportController implements UserReportApi {

    private final UserReportService service;

    @GetMapping
    public ResponseEntity<Collection<UserReportResponseDto>> getPendingReports() {
        return ResponseEntity.ok(service.getPendingReports());
    }

    @PostMapping("/{offender-id}")
    public ResponseEntity<Void> createReport(@Valid @RequestBody UserReportRequestDto report, @PathVariable("offender-id") Long id) {
        service.createReport(report, id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateReportRequestDto status) {
        service.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}