package com.iss.eventorium.user.services;

import com.iss.eventorium.shared.models.Status;
import com.iss.eventorium.user.dtos.report.UserReportResponseDto;
import com.iss.eventorium.user.dtos.report.UpdateReportRequestDto;
import com.iss.eventorium.user.dtos.report.UserReportRequestDto;
import com.iss.eventorium.user.mappers.UserReportMapper;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserReport;
import com.iss.eventorium.user.repositories.UserReportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserReportRepository repository;
    private final AuthService authService;
    private final UserService userService;

    public void createReport(UserReportRequestDto request, Long offenderId) {
        UserReport report = UserReportMapper.fromRequest(request);

        report.setReporter(authService.getCurrentUser());
        report.setOffender(userService.find(offenderId));

        repository.save(report);
    }

    public Collection<UserReportResponseDto> getPendingReports() {
        List<UserReport> reports = repository.findByStatusPending();
        return reports.stream().map(UserReportMapper::toResponse).toList();
    }

    public void updateStatus(Long id, UpdateReportRequestDto request) {
        UserReport report = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("User report not found"));
        report.setStatus(request.getStatus());

        if (request.getStatus() == Status.ACCEPTED)
            suspendUser(report);

        repository.save(report);
    }

    private void suspendUser(UserReport report) {
        User user = userService.find(report.getOffender().getId());
        user.setSuspended(LocalDateTime.now());
    }
}