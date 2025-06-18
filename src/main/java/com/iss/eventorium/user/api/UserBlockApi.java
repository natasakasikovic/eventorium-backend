package com.iss.eventorium.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name="UserBlock",
        description = "Handles the user blocking endpoints."
)
public interface UserBlockApi {

    @Operation(
            summary = "Blocks a user by ID.",
            description =
                    """
                    Blocks the specified user by their unique identifier.
                    This operation requires authentication (user needs to be logged in)!
                    If the blocked user is an ORGANIZER, then the user who performed the 
                    blocking will no longer see events from that organizer in their list
                    of favourite events or events they are attending. 
                    If the blocked user is a PROVIDER, their services and products will
                    be removed from the blocker’s list of favourite services and products.
                    """,
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully blocked"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
            }
    )
    ResponseEntity<Void> blockUser(
            @Parameter(
                    description = "The unique identifier of the user who will blocked.",
                    required = true,
                    example = "123"
            )
            Long id
    );
}
