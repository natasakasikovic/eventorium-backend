package com.iss.eventorium.event.api;

import com.iss.eventorium.event.dtos.agenda.ActivityRequestDto;
import com.iss.eventorium.event.dtos.agenda.ActivityResponseDto;
import com.iss.eventorium.event.dtos.event.*;
import com.iss.eventorium.event.dtos.statistics.EventRatingsStatisticsDto;
import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.shared.models.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;
import java.util.List;

@Tag(
        name="Event",
        description = "Handles the event endpoints."
)
public interface EventApi {

    @Operation(
            summary = "Retrieve editable event data by ID (accessible only to the organizer).",
            description =
            """
            Returns an event object containing attributes that can be edited.
            The event is identified by its unique ID.
            This endpoint is accessible only to the organizer who created the event.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
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
    ResponseEntity<EditableEventDto> getEvent(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Retrieve an event by ID.",
            description =
                    """
                    Returns the details of a single event using its unique identifier.
                    The event information is returned only if it exists and is not from a blocked organizer.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
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
    ResponseEntity<EventDetailsDto> getEventDetails(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Retrieves up to five events from the user's city.",
            description =
                    """
                    Returns up to 5 events from the user's city.
                    If the user is logged in, their city is used to retrieve the events.
                    If the user is not logged in, the default city 'Novi Sad' is used.
                    Only upcoming events are included in the response.
                    Returns up to 5 events; if fewer are available, returns as many as possible.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<List<EventSummaryResponseDto>> getTopEvents();

    @Operation(
            summary = "Fetches all upcoming opened events.",
            description =
            """
            Returns a list of all upcoming events, excluding those from blocked organizer if the user is logged in.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<Collection<EventSummaryResponseDto>> getEvents();

    @Operation(
            summary = "Fetches past events (accessible to ADMIN and ORGANIZER).",
            description =
            """
            Returns a list of past events based on the logged-in user's role.
    
            - If an ADMIN is logged in, all past events are returned.
            - If an ORGANIZER is logged in, only their own past events are returned.
            
            This endpoint is restricted to users with the ADMIN or ORGANIZER role.
            Access is denied for all other roles.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<EventTableOverviewDto>> getPassedEvents();

    @Operation(
            summary = "Retrieves a paginated list of opened events.",
            description =
            """
            Returns a subset of opened events based on pagination parameters,
            excluding those from any blocked organizers if the user is logged in.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<PagedResponse<EventSummaryResponseDto>> getEventsPaged(Pageable pageable);

    @Operation(
            summary = "Retrieves a paginated list of opened upcoming events based on the provided filter criteria.",
            description =
                    """
                    Returns a subset of upcoming events based on pagination parameters and filter criteria.
                    excluding those from any blocked organizers if the user is logged in.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidFilterExample",
                                            summary = "Filter with invalid number of max participants.",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"The value of maximum participants must be greater than zero!\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<PagedResponse<EventSummaryResponseDto>> filterEvents(
            @Valid @RequestBody EventFilterDto filter,
            Pageable pageable
    );

    @Operation(
            summary = "Filters upcoming opened events based on the provided filter criteria.",
            description =
                    """
                    Filters upcoming events based on various criteria, including name, event type, address, etc.
                    Excludes events from blocked organizers if the user is logged in.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidFilterExample",
                                            summary = "Filter with invalid number of max participants.",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"The value of maximum participants must be greater than zero!\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<List<EventSummaryResponseDto>> filterEvents(
            @Valid @RequestBody EventFilterDto filter
    );

    @Operation(
            summary = "Retrieves a paginated list of upcoming opened events based on the provided keyword.",
            description =
                    """
                    Returns a subset of upcoming events based on the provided keyword.
                    Searches upcoming events based on the provided keyword. The keyword is only used to match against event names.
                    If no keyword is provided (empty string), all upcoming events are eligible.
                    Excludes events from blocked organizers if the user is logged in.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<PagedResponse<EventSummaryResponseDto>> searchEvents(String keyword, Pageable pageable);

    @Operation(
            summary = "Search upcoming opened events based on the provided keyword.",
            description =
            """
            Searches upcoming events based on the provided keyword. The keyword is only used to match against event names.
            If no keyword is provided (empty string), all upcoming events are eligible.
            Excludes events from blocked organizers if the user is logged in.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<List<EventSummaryResponseDto>> searchEvents(String keyword);

    @Operation(
            summary = "Fetches all upcoming events for currently logged-in organizer.",
            description =
                    """
                    Returns a list of all upcoming events, excluding those from blocked organizer.
                    Authentication is required. Only accessible to logged-in organizer.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<EventResponseDto>> getFutureEvents();

    @Operation(
            summary = "Creates an event",
            description =
            """
            Creates a new event.
            The drafted flag remains true while the event is still in the creation phase.
            Completing the process involves steps such as budget planning, agenda setup, and, for private events, sending invitations.
            Returns the created event if successful.
            Requires authentication and ORGANIZER authority.
            Only users with the `ORGANIZER` authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidEventExample",
                                            summary = "Event without name",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Event name is required.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions")
            }
    )
    ResponseEntity<EventResponseDto> createEvent(
            @Valid @RequestBody(
                    description = "The data used to create the event.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EventRequestDto.class))
            )
            EventRequestDto eventRequestDto
    );

    @Operation(
            summary = "Creates an event agenda",
            description =
            """
            Creates an agenda for event in drafted state.
            Requires authentication and ORGANIZER authority.
            Only users with the `ORGANIZER` authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidActivitiesExample",
                                            summary = "Activity with empty name",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Name cannot be empty\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions")
            }
    )
    ResponseEntity<Void> createAgenda(
            @Valid @RequestBody(
                    description = "List of activities",
                    required = false
            )
            List<ActivityRequestDto> request,
            @PathVariable(required = true)
            Long id
    );

    @Operation(
            summary = "Retrieve an event agenda by event ID.",
            description =
            """
            Returns the event agenda of a single event using its unique identifier.
            Returns null if the organizer has not completed the agenda during event creation.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
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
    ResponseEntity<List<ActivityResponseDto>> getAgenda(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Updates an event",
            description =
                    """
                    Finds event by unique ID and updates it if data is valid and user has proper authorization.
                    Requires authentication and ORGANIZER authority.
                    
                    Only users with the `ORGANIZER` authority can access this endpoint.
                    Additionally, only the organizer who originally created the event is allowed to edit it.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidEventExample",
                                            summary = "Event without name",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Event name is required.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions")
            }
    )
    ResponseEntity<Void> updateEvent(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id,
            @Valid @RequestBody(
                    description = "The data used to update the event.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateEventRequestDto.class))
            )
            UpdateEventRequestDto request
    );


    @Operation(
            summary = "Generates PDF with event details",
            description =
                """
                Generates a PDF document containing detailed information about the event. Accessible by any user.
                """,
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "PDF generated successfully"),
                    @ApiResponse(responseCode = "404", description = "Event not found")
            }
    )
    ResponseEntity<byte[]> getEventDetailsPdf(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Generates PDF guest list",
            description =
            """
            Generates a PDF document containing the guest list of the event.
            Only accessible by users with the `ORGANIZER` role.
            """,
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "PDF guest list generated successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Event not found")
            }
    )
    ResponseEntity<byte[]> getGuestListPdf(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Gets event rating statistics",
            description =
                """
                Returns statistical data about event ratings. Accessible only by users with `ORGANIZER` or `ADMIN` roles.
                Admins can view statistics of any past event.
                Organizers can only view statistics of their own past events.
                """,
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Event not found")
            }
    )
    ResponseEntity<EventRatingsStatisticsDto> getEventRatingStatistics(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Generates PDF with event statistics",
            description =
            """
            Generates a PDF document containing statistical data about the event.
            Accessible only by users with `ORGANIZER` or `ADMIN` roles.
            Admins can generate pdf statistics of any past event.
            Organizers can only generate pdf statistics of their own past events.
            """,
            security = { @SecurityRequirement(name = "bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "PDF statistics generated successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
                    @ApiResponse(responseCode = "404", description = "Event not found")
            }
    )
    ResponseEntity<byte[]> getEventStatisticsPdf(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id
    );
}
