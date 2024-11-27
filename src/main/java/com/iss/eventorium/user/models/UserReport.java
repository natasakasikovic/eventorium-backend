package com.iss.eventorium.user.models;

import com.iss.eventorium.shared.models.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReport {
    private Long id;
    private String reason;
    private LocalDateTime timestamp;
    private User respondent;
    private User complainant;
    private Status status;
}
