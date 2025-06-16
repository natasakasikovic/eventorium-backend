package com.iss.eventorium.event.api;

import com.iss.eventorium.event.dtos.invitation.InvitationDetailsDto;
import com.iss.eventorium.event.dtos.invitation.InvitationRequestDto;
import com.iss.eventorium.event.dtos.invitation.InvitationResponseDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
        name="Invitation",
        description = "Handles the invitation endpoints."
)
public interface InvitationApi {


    @Operation(summary = "Verify invitation by hash",
            description = """
        Verifies whether an invitation with the provided hash exists. 
        This endpoint is accessible WITHOUT AUTHENTICATION. 
        """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invitation successfully verified"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invitation not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvitationNotFound",
                                            summary = "Invitation not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Invitation not found.\" }"
                                    )
                            )
                    )

            }
    )
    ResponseEntity<Void> verifyInvitation(@Parameter(
            description = "Unique hash identifier of the invitation",
            example = "a1b2c3d4e5f6g7h8") String hash);



    @Operation(
            summary = "Retrieve invitation email by invitation hash",
            description = """
                Retrieves the email address associated with the invitation identified by the given hash.
                This endpoint is primarily used for quick registration on the UI, 
                where the email field is auto-filled based on the invitation hash, 
                and the user provides the remaining details.
                
                No authentication is required.
                """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invitation found and email retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Invitation not found"),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Email already associated with an existing account",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "EmailAlreadyTaken",
                                            summary = "Email already registered",
                                            value = "{ \"error\": \"Conflict\", \"message\": \"An email you are trying to use within this invitation is already associated with an account. Please log in to your account.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<InvitationResponseDto> getInvitation(
            @Parameter(
                    description = "Unique hash identifier of the invitation used to retrieve the associated email for quick registration",
                    example = "a1b2c3d4e5f6g7h8",
                    required = true
            )
            @PathVariable String hash
    );


    @Operation( summary = "Send invitations for an event",
            description =
            """
            Sends one or more invitations for the specified event.
            Requires AUTHENTICATION.
            User must have the ORGANIZER role to access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User does not have permission to send invitations (must be organizer)",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "Forbidden",
                                            summary = "Access denied",
                                            value = "{ \"error\": \"Forbidden\", \"message\": \"Only users with the ORGANIZER role can send invitations.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Event not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "EventNotFound",
                                            summary = "Event not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Event not found.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "502",
                            description = "Error occurred while sending emails",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "EmailSendingError",
                                            summary = "Email sending failed",
                                            value = "{ \"error\": \"Bad Gateway\", \"message\": \"An error occurred while sending invitation emails.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<Void> sendInvitations(
            @Parameter(
                    description = "List of invitation request objects containing recipient details (email)",
                    required = true,
                    examples = @ExampleObject(
                            name = "InvitationListExample",
                            summary = "Example invitation list",
                            value = """
                [
                    { "email": "john.doe@example.com" },
                    { "email": "jane.smith@example.com" }
                ]
                """
                    )
            )
            @RequestBody List<InvitationRequestDto> invitations,
            @Parameter(
                    description = "Unique identifier of the event for which invitations are being sent",
                    example = "123",
                    required = true
            )
            @PathVariable("event-id") Long id
    );


    @Operation(
            summary = "Retrieves all invitations for the authenticated user",
            description =
            """
            Retrieves all invitations for the currently AUTHENTICATED user. 
            If the user has blocked an event organizer, invitations from that organizer will not be shown.
            Only AUTHORIZED USERS can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
            }
    )
    ResponseEntity<List<InvitationDetailsDto>> getInvitations();

}