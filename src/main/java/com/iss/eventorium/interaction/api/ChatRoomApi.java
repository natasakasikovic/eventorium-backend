package com.iss.eventorium.interaction.api;

import com.iss.eventorium.interaction.dtos.chat.ChatRoomResponseDto;
import com.iss.eventorium.shared.models.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Chat", description = "Only authorized users can access this endpoints.")
public interface ChatRoomApi {

    @Operation(
            summary = "Fetches all user's chat rooms.",
            description = "Returns a list of the user's chat rooms, excluding any involving blocked users.",
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<ChatRoomResponseDto>> getChatRooms();

    @Operation(
            summary = "Retrieves a paginated list of user's chat rooms.",
            description = "Returns a subset of user's chat rooms based on pagination parameters, excluding any involving blocked users.",
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<PagedResponse<ChatRoomResponseDto>> getChatRooms(Pageable pageable);
}
