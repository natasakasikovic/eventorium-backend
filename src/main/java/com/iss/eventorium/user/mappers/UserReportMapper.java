package com.iss.eventorium.user.mappers;

import com.iss.eventorium.user.dtos.report.UserReportRequestDto;
import com.iss.eventorium.user.dtos.report.UserReportResponseDto;
import com.iss.eventorium.user.models.Person;
import com.iss.eventorium.user.models.User;
import com.iss.eventorium.user.models.UserReport;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReportMapper {

    private final ModelMapper modelMapper;

    public UserReport fromRequest(UserReportRequestDto request) {
        return modelMapper.map(request, UserReport.class);
    }

    public UserReportResponseDto toResponse (UserReport report) {
        return new UserReportResponseDto(report.getId(),
                report.getTimestamp(),
                report.getReason(),
                getFullName(report.getReporter()),
                getFullName(report.getOffender()));
    }

    private String getFullName(User user) {
        if (user == null || user.getPerson() == null)
            return "Unknown";

        Person person = user.getPerson();
        return String.format("%s %s", person.getName(), person.getLastname());
    }
}