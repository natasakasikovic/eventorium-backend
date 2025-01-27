package com.iss.eventorium.notifications.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private Long id;
    private String message;
    private Boolean seen;
    private LocalDateTime timestamp;
}
