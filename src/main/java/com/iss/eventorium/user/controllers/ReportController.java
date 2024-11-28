package com.iss.eventorium.user.controllers;

import com.iss.eventorium.shared.utils.PagedResponse;
import com.iss.eventorium.user.dtos.ReportRequestDto;
import com.iss.eventorium.user.dtos.ReportResponseDto;
import com.iss.eventorium.user.dtos.UpdateReportStatusDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @GetMapping("/all")
    public ResponseEntity<Collection<ReportResponseDto>> getReports() {
        // TODO: call service -> reportService.getAll();
        // NOTE: When fetching, make sure not to fetch reports from those who are suspended.
        Collection<ReportResponseDto> reports = List.of( new ReportResponseDto(1L, "Reason 1", 1L, 2L),
                                                        new ReportResponseDto(2L, "Reason 2", 2L, 4L));
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ReportResponseDto>> getReportsPaged(Pageable pageable) {
        // TODO: call service -> reportService.get(pageable)
        return ResponseEntity.ok().body(new PagedResponse<>(List.of(new ReportResponseDto(1L, "Reason 3", 1L, 2L)), 1, 3));
    }

    @PostMapping
    public ResponseEntity<ReportResponseDto> createReport(@RequestBody ReportRequestDto report){
        // TODO: call -> reportService.createReport(report); and delete line below
        ReportResponseDto reportCreated = new ReportResponseDto();
        return new ResponseEntity<>(reportCreated, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestBody UpdateReportStatusDto status) {
        // reportService.updateStatus(status);
        return ResponseEntity.ok("Status updated successfully!");
    }

}
