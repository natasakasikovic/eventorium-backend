package com.iss.eventorium.users.dtos;

import com.iss.eventorium.shared.models.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportStatusDto {
    private Long reportId;
    private Status status;
}
