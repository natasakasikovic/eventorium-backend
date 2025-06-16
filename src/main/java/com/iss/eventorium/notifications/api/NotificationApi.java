package com.iss.eventorium.notifications.api;

import com.iss.eventorium.notifications.dtos.NotificationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(
        name = "Notification",
        description =
        """
        Handles notification functionality.
        Access to these endpoints is restricted to *authorized* users only.
        """
)
public interface NotificationApi {

    @Operation(
            summary = "Retrieves all user's notifications.",
            description =
            """
            Returns all user's notifications.
            Notifications are send with websockets. <br/>
            **Server broadcasts to:** <br/>
            `/user/{userId}/notifications` - all users. <br/>
            `/topic/admin` - users with `ADMIN` authority only.
            <br/>
            Note: Users with silenced notifications will not receive any notifications.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<NotificationResponseDto>> getNotifications();

    @Operation(
            summary = "Marks all unseen notifications as seen.",
            description =
            """
            Marks all user's unseen notifications as seen.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<Void> markNotificationsAsSeen();

    @Operation(
            summary = "Check if the user has silenced notifications.",
            description = "Returns a boolean indicating whether the authenticated user has silenced notifications.",
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success - returns true if notifications are silenced, false otherwise", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid authentication token")
            }
    )
    ResponseEntity<Boolean> getSilenceStatus();

    @Operation(
            summary = "Silence or unsilence user notifications.",
            description =
            """
            Updates the user's notification settings. Set `silence` to `true` to silence notifications or `false`
            to enable them.,
            """,
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notification settings updated successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid authentication token"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - invalid parameter")
            }
    )
    ResponseEntity<Void> silenceNotifications(
            @Parameter(
                    name = "silence",
                    description = "Boolean flag to silence (`true`) or unsilence (`false`) notifications",
                    required = true
            )
            boolean silence
    );
}
