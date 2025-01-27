package com.iss.eventorium.notifications.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private String message;
    private Boolean seen;
    private LocalDateTime timestamp;
}
