package com.iss.eventorium.interaction.dtos.chat;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequestDto {

    @NotNull(message = "SenderId is mandatory")
    private Long senderId;

    @NotNull(message = "RecipientId is mandatory")
    private Long recipientId;

    @NotBlank(message = "Message is mandatory")
    @Size(min = 1, max = 100, message = "Message must be 100 characters or fewer")
    private String message;
}
