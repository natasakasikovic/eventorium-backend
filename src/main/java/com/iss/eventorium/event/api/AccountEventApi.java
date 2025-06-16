package com.iss.eventorium.event.api;

import com.iss.eventorium.event.dtos.event.CalendarEventDto;
import com.iss.eventorium.event.dtos.event.EventSummaryResponseDto;
import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.shared.models.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(
        name="Account Event",
        description = "Handles the account event endpoints."
)
public interface AccountEventApi {

    @Operation(
            summary = "Fetches events the user is attending.",
            description =
                    """
                    Returns a list of events that the currently authenticated user has marked attendance for.
                    Requires authentication.
                    """,
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
            }
    )
    ResponseEntity<List<CalendarEventDto>> getAttendingEvents();

    @Operation(
            summary = "Fetches all organizer's upcoming events.",
            description =
                    """
                    Returns a list of events created by the currently authenticated user with the ORGANIZER role.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<CalendarEventDto>> getOrganizerEvents();


    @Operation(
            summary = "Marks attendance for the specified event.",
            description =
                    """
                    Marks the currently authenticated user's attendance for the event with the given ID.
                    Requires authentication.
                    
                    If the event does not exist, a 404 Not Found response is returned.
                    If the user has already marked attendance, no action is taken.
                    """,
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success - attendance marked or already present"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "404", description = "Not Found - event with given ID does not exist")
            }
    )
    ResponseEntity<Void> markAttendance(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Checks if the user is eligible to rate the event.",
            description =
                    """
                    Determines whether the currently authenticated user is allowed to rate the event with the given ID.
                    The user can rate the event only if:
                    - The event has already passed,
                    - They have not already rated it,
                    - They had previously marked attendance for it.
                    
                    Requires authentication.
                    """,
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success - returns true or false"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "404", description = "Not Found - event with given ID does not exist")
            }
    )
    ResponseEntity<Boolean> getRatingEligibility(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Fetches the user's favourite events.",
            description =
                    """
                    Returns a list of all favourite events for the currently logged-in user.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            }
    )
    ResponseEntity<List<EventSummaryResponseDto>> getFavouriteEvents();

    @Operation(
            summary = "Adds an event to the user's list of favourites.",
            description =
                    """
                    Adds an event to the currently logged-in user's list of favourite events.
                    If the event is already marked as a favourite, no action is taken.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
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
                    )
            }
    )
    ResponseEntity<Void> addFavouriteEvent(Long id);

    @Operation(
            summary = "Removes an event from the user's list of favourites.",
            description =
                    """
                    Removes an event from the currently logged-in user's list of favourite events.
                    If the event is not marked as a favourite, no action is taken.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
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
                    )
            }
    )
    ResponseEntity<Void> removeFavouriteEvent(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Checks if the given event is among the user's favourite events.",
            description =
                    """
                    Returns 'true' if the given event is marked as a favourite by the currently logged-in user.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
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
                    )
            }
    )
    ResponseEntity<Boolean> isFavouriteEvent(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Fetches all organizer's upcoming events.",
            description =
                    """
                    Returns a list of all organizer's events.
                    Requires authentication and ORGANIZER authority.
                    Only users with the `ORGANIZER` authority can access this endpoint.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<EventSummaryResponseDto>> getAllEvents();

    @Operation(
            summary = "Retrieves a paginated list of organizer's events.",
            description =
                    """
                    Returns a subset of organizer's events.
                    Requires authentication and ORGANIZER authority.
                    Only users with the `ORGANIZER` authority can access this endpoint.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<PagedResponse<EventSummaryResponseDto>> getEvents(Pageable pageable);

    @Operation(
            summary = "Search organizer's events based on the provided keyword.",
            description =
                    """
                    Searches organizer's events based on the provided keyword. The keyword is only used to match against event names.
                    Requires authentication and ORGANIZER authority.
                    Only users with the `ORGANIZER` authority can access this endpoint.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<EventSummaryResponseDto>> searchEvents(String keyword);

    @Operation(
            summary = "Retrieves a paginated list of organizer's events based on the provided keyword.",
            description =
                    """
                    Returns a subset of organizer's events based on the provided keyword.
                    The keyword is only used to match against event names.
                    Requires authentication and ORGANIZER authority.
                    Only users with the `ORGANIZER` authority can access this endpoint.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<PagedResponse<EventSummaryResponseDto>> searchEventsPaged(String keyword, Pageable pageable);
}
