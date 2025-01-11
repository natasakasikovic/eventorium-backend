package com.iss.eventorium.user.services;

import com.iss.eventorium.user.dtos.UserReportRequestDto;
import com.iss.eventorium.user.mappers.UserReportMapper;
import com.iss.eventorium.user.models.UserReport;
import com.iss.eventorium.user.repositories.UserReportRepository;
import com.iss.eventorium.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserReportRepository repository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public void createReport(UserReportRequestDto request, Long offenderId) {
        UserReport report = UserReportMapper.fromRequest(request);

        report.setReporter(authService.getCurrentUser());
        report.setOffender(userRepository.findById(offenderId).get());

        repository.save(report);
    }
}