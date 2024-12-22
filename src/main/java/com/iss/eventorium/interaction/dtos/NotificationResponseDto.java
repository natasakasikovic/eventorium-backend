package com.iss.eventorium.interaction.dtos;

import com.iss.eventorium.interaction.models.NotificationType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private String message;
    private Boolean seen;
    private NotificationType type;
}
