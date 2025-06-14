package com.iss.eventorium.event.api;

import com.iss.eventorium.event.dtos.eventtype.EventTypeRequestDto;
import com.iss.eventorium.event.dtos.eventtype.EventTypeResponseDto;
import com.iss.eventorium.shared.models.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(
        name="Event Type",
        description =
                """
                Handles the event types endpoints.
                """
)
public interface EventTypeApi {

    @Operation(
            summary = "Fetches all event types.",
            description = "Returns a list of all available event types.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<List<EventTypeResponseDto>> getEventTypes();

    @Operation(
            summary = "Retrieve a event type by ID.",
            description =
            """
            Fetches the details of a single event type using its unique identifier.
            Returns the event type information if it exists.
            Requires authentication and ADMIN authority.
            Only users with the `ADMIN` authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Event type not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "EventTypeNotFound",
                                            summary = "Event type not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Event type not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<EventTypeResponseDto> getEventType(
            @Parameter(
                    description = "The unique identifier of the event type to retrieve.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Creates a event type.",
            description =
            """
            Creates a new event type.
            To upload event type image, use the endpoint `POST /api/v1/event-type/{id}/images`.
            Returns the created event type if successful.
            Requires authentication and ADMIN authority.
            Only users with the `ADMIN` authority can access this endpoint.
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
                                            name = "InvalidEventTypeExample",
                                            summary = "Event type without name",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Name is mandatory.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<EventTypeResponseDto> createEventType(
            @RequestBody(
                    description = "The data used to create the event type.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EventTypeRequestDto.class))
            )
            EventTypeRequestDto requestDto
    );

    @Operation(
            summary = "Uploads image for a event type using its unique ID.",
            description =
            """
            Uploads image for the specified event type. The `id` parameter refers to the event type ID.
            The uploaded image will be displayed for all events associated with this event type.
            For more details on creating a event type, please refer to the endpoint `POST /api/v1/event-types`.
            Requires authentication and ADMIN authority.
            Only users with the `ADMIN` authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Event type not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "EventTypeNotFound",
                                            summary = "Event type not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Event type not found.\" }"
                                    )
                            )
                    ),
            }
    )
    ResponseEntity<Void> uploadImage(
            @Parameter(
                    description = "The unique identifier of the event type to retrieve.",
                    required = true,
                    example = "123"
            )
            Long id,
            MultipartFile file
    );

    @Operation(
            summary = "Retrieve the image for a event (type) using its unique service ID.",
            description =
            """
            Fetches image associated with a specific event (type) using its unique identifier (event type ID).
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Image not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "ImageNotFound",
                                            summary = "Event type not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Image not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<byte[]> getImage(
            @Parameter(
                    description = "The unique identifier of the event type to retrieve.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Updates event type image.",
            description =
            """
            Updates image for the specified event type. The `id` parameter refers to the event type ID.
            For more details on updating a event type, please refer to the endpoint `PUT /api/v1/event-types`.
            Requires authentication and ADMIN authority.
            Only users with the `ADMIN` authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Event type not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "EventTypeNotFound",
                                            summary = "Event type not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Event type not found.\" }"
                                    )
                            )
                    ),
            }
    )
    ResponseEntity<byte[]> updateImage(
            @Parameter(
                    description = "The unique identifier of the event type to retrieve.",
                    required = true,
                    example = "123"
            )
            Long id,
            MultipartFile file
    );

    @Operation(
            summary = "Updates a event type.",
            description =
            """
            Updates event type if exists.
            Returns the updated event type if successful.
            Requires authentication and ADMIN authority.
            Only users with the `ADMIN` authority can access this endpoint.
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
                                            name = "InvalidEventTypeExample",
                                            summary = "Event type without name",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Name is mandatory.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Event type not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "EventTypeNotFound",
                                            summary = "Event type not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Event type not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<EventTypeResponseDto> updateEventType(
            @Parameter(
                    description = "The unique identifier of the event type to update.",
                    required = true,
                    example = "123"
            )
            Long id,
            @RequestBody(
                    description = "The data used to update the event type.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EventTypeRequestDto.class))
            )
            EventTypeRequestDto requestDto
    );

    @Operation(
            summary = "Deletes a event type.",
            description =
            """
            Deletes the event type if it exists.
            Requires authentication and ADMIN authority.
            Only users with the `ADMIN` authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Event type not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "EventTypeNotFound",
                                            summary = "Event type not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Event type not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<Void> deleteEventType(
            @Parameter(
                    description = "The unique identifier of the event type to delete.",
                    required = true,
                    example = "123"
            )
            Long id
    );
}
