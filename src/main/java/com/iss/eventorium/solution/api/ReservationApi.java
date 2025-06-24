package com.iss.eventorium.solution.api;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.solution.dtos.services.CalendarReservationDto;
import com.iss.eventorium.solution.dtos.services.ReservationRequestDto;
import com.iss.eventorium.solution.dtos.services.ReservationResponseDto;
import com.iss.eventorium.solution.dtos.services.UpdateReservationStatusRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
        name = "Reservation",
        description = "Handles the reservation endpoints."
)
public interface ReservationApi {

    @Operation(
            summary = "Creates a reservation for service",
            description =
            """
            Creates a reservation for a specific service under a specific event and sends a confirmation email
            to the user who made the reservation. The reservation is automatically added to the event's budget.
            If the reservation type is `AUTOMATIC`, it is immediately marked as `PROCESSED`.
            If it is `MANUAL`, it remains `PENDING` until the provider approves or rejects the reservation.
            To update the status of a `MANUAL` reservation, use `PATCH /api/v1/reservations/{id}`.
            Requires authentication and the `ORGANIZER` authority.
            Only users with the `ORGANIZER` role can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Reservation successfully created", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error - missing or invalid fields",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing Starting Time",
                                                    value = "{ \"startingTime\": \"Starting time is required!\" }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Event or service not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Event Not Found",
                                                    value = "{ \"error\": \"Event not found\" }"
                                            ),
                                            @ExampleObject(
                                                    name = "Service Not Found",
                                                    value = "{ \"error\": \"Service not found\" }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Reservation conflict detected - overlapping time slot",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Overlapping Reservation Conflict",
                                                    value = "{\"error\": \"The selected time slot for this service is already occupied. Please choose a different time.}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Insufficient funds to complete the reservation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(value = """
                                    {
                                      "error": "Unprocessable Entity",
                                      "message": "You do not have enough funds to make this reservation."
                                    }
                                """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "502",
                            description = "Failed to send confirmation email",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Email Failure",
                                                    value = "{ \"error\": \"Failed to send confirmation email.\" }"
                                            )
                                    }
                            )
                    )
            }

    )
    ResponseEntity<Void> createReservation(
            @Parameter(description = "Reservation request body with staring, ending time and planned amount", required = true)
            @RequestBody ReservationRequestDto reservation,

            @Parameter(description = "ID of the event for which the reservation is being created", required = true, example = "42")
            Long eventId,

            @Parameter(description = "ID of the service to be reserved", required = true, example = "7")
            Long serviceId
    );


    @Operation(
            summary = "Get accepted reservations for current provider",
            description = """
            Returns a list of accepted reservations for the currently authenticated provider. Used for calendar display."
            Requires authentication and PROVIDER authority.
            Only users with the `PROVIDER` authority can access this endpoint.""",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of accepted reservations",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = CalendarReservationDto.class))
                            )
                    ),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
            }
    )
    ResponseEntity<List<CalendarReservationDto>> getProviderReservations();


    @Operation(
            summary = "Get pending reservations for upcoming events",
            description = """
                    Returns a list of pending reservations for the currently authenticated provider, limited to future (upcoming) events.
                    Requires authentication and PROVIDER authority.
                    Only users with the `PROVIDER` authority can access this endpoint.""",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of pending reservations",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ReservationResponseDto.class))
                            )
                    ),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
            }
    )
    ResponseEntity<List<ReservationResponseDto>> getPendingReservations();


    @Operation(
            summary = "Update reservation status",
            description = "Updates the status of an existing reservation. ONLY THE AUTHENTICATED PROVIDER who owns the reservation can perform this operation.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Reservation status successfully updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Reservation not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Reservation Not Found",
                                            value = "{ \"error\": \"Reservation with given ID not found\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
            }
    )
    ResponseEntity<ReservationResponseDto> updateReservation(
            @Parameter(description = "ID of the reservation to be updated", required = true)
            Long id,

            @Parameter(description = "New status to update the reservation", required = true)
            @RequestBody UpdateReservationStatusRequestDto request);
}
