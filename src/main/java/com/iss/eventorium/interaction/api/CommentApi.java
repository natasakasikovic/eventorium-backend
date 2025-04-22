package com.iss.eventorium.interaction.api;

import com.iss.eventorium.interaction.dtos.comment.CommentResponseDto;
import com.iss.eventorium.interaction.dtos.comment.CreateCommentRequestDto;
import com.iss.eventorium.interaction.dtos.comment.UpdateCommentRequestDto;
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

import java.util.List;

@Tag(name = "Comment", description = "Only authorized users can access this endpoints.")
public interface CommentApi {

    @Operation(
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<CommentResponseDto>> getPendingComments();

    @Operation(
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidCommentExample",
                                            summary = "Empty comment",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Comment is mandatory\" }"
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
    ResponseEntity<CommentResponseDto> createServiceComment(
            @RequestBody(
                    description = "The data used to create the service comment.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateCommentRequestDto.class))
            )
            CreateCommentRequestDto request,
            @Parameter(
                    description = "The unique identifier of the service.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidCommentExample",
                                            summary = "Empty comment",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Comment is mandatory\" }"
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
    ResponseEntity<CommentResponseDto> createProductComment(
            @RequestBody(
                    description = "The data used to create the product comment.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateCommentRequestDto.class))
            )
            CreateCommentRequestDto request,
            @Parameter(
                    description = "The unique identifier of the product.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<CommentResponseDto> createEventComment(
            @RequestBody(
                    description = "The data used to create the event comment.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateCommentRequestDto.class))
            )
            CreateCommentRequestDto request,
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<CommentResponseDto> updateComment(
            @Parameter(
                    description = "The unique identifier of the comment.",
                    required = true,
                    example = "123"
            )
            Long id,
            @RequestBody(
                    description = "The data used to accept / decline the comment.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateCommentRequestDto.class))
            )
            UpdateCommentRequestDto request
    );
}
