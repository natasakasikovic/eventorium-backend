package com.iss.eventorium.interaction.api;

import com.iss.eventorium.interaction.dtos.ratings.CreateRatingRequestDto;
import com.iss.eventorium.interaction.dtos.ratings.RatingResponseDto;
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

@Tag(
        name = "Rating",
        description =
        """
        Handles the solution and event rating endpoints.
        Access to these endpoints is restricted to *authorized* users only.
        """
)
public interface RatingApi {

    @Operation(
            summary = "Creates and attaches a user rating to a service.",
            description =
            """
            Creates a new user rating and associates it with a service.
            Returns the created rating if successful.
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
                                            name = "InvalidRatingExample",
                                            summary = "Empty rating",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Rating is mandatory\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Service not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "ServiceNotFound",
                                            summary = "Service not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Service not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<RatingResponseDto> createServiceRating(
            @RequestBody(
                    description = "The data used to create the service rating.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateRatingRequestDto.class))
            )
            CreateRatingRequestDto request,
            @Parameter(
                    description = "The unique identifier of the service.",
                    required = true,
                    example = "123"
            )
            Long serviceId
    );

    @Operation(
            summary = "Creates and attaches a user rating to a product.",
            description =
            """
            Creates a new user rating and associates it with a product.
            Returns the created rating if successful.
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
                                            name = "InvalidRatingExample",
                                            summary = "Empty rating",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Rating is mandatory\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "ProductNotFound",
                                            summary = "Product not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Product not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<RatingResponseDto> createProductRating(
            @RequestBody(
                    description = "The data used to create the product rating.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateRatingRequestDto.class))
            )
            CreateRatingRequestDto request,
            @Parameter(
                    description = "The unique identifier of the product.",
                    required = true,
                    example = "123"
            )
            Long productId
    );

    @Operation(
            summary = "Creates and attaches a user rating to a event.",
            description =
            """
            Creates a new user rating and associates it with a event.
            Returns the created rating if successful.
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
                                            name = "InvalidRatingExample",
                                            summary = "Empty rating",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Rating is mandatory\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
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
    ResponseEntity<RatingResponseDto> createEventRating(
            @RequestBody(
                    description = "The data used to create the event rating.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateRatingRequestDto.class))
            )
            CreateRatingRequestDto request,
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long eventId
    );
}
