package com.iss.eventorium.interaction.api;

import com.iss.eventorium.interaction.dtos.chat.ChatMessageResponseDto;
import com.iss.eventorium.shared.models.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(
        name = "Chat",
        description =
        """
        Handles chat functionality.
        Access to these endpoints is restricted to *authorized* users only.
        """
)
public interface ChatApi {

    @Operation(
            summary = "Retrieve all messages associated with a specific chat room.",
            description =
            """
            Returns all chat messages related to a specific chat room.
            Provides access to the entire message history for a given chat room.
            All messages are sent with web socket. <br/>
            **WebSocket endpoint:** `/api/v1/ws` <br/>
            **Client sends to:** `/app/chat` <br/>
            **Server broadcasts to:** `/user/{recipientId}/queue/messages` <br/>
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "UserNotFound",
                                            summary = "User not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"User not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<List<ChatMessageResponseDto>> getChatMessages(
            @Parameter(
                    description = "The unique identifier of the sender (User).",
                    required = true,
                    example = "123"
            )
            Long senderId,
            @Parameter(
                    description = "The unique identifier of the recipient (User).",
                    required = true,
                    example = "123"
            )
            Long recipientId
    );
}
