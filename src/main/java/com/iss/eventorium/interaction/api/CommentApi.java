package com.iss.eventorium.interaction.api;

import com.iss.eventorium.interaction.dtos.comment.CommentResponseDto;
import com.iss.eventorium.interaction.dtos.comment.CreateCommentRequestDto;
import com.iss.eventorium.interaction.dtos.comment.UpdateCommentRequestDto;
import com.iss.eventorium.interaction.models.CommentType;
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

@Tag(
        name = "Comment",
        description =
        """
        Handles the solution and event comment endpoints.
        Access is restricted to *authorized* users only.
        Only users with `ADMIN` authority are allowed to modify a comment's status.
        """
)
public interface CommentApi {

    @Operation(
            summary = "Retrieves all comments with status 'PENDING'.",
            description = "Returns a list of all pending comments.",
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<CommentResponseDto>> getPendingComments();

    @Operation(
            summary = "Creates a comment.",
            description =
            """
            Creates a new comment.
            Returns the created comment if successful.
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
    ResponseEntity<CommentResponseDto> createComment(
            @RequestBody(
                    description = "The data used to create comment.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateCommentRequestDto.class))
            )
            CreateCommentRequestDto request
    );

    @Operation(
            summary = "Updates pending comment status.",
            description =
            """
            Updates pending comment status to `ACCEPTED` or `DECLINED` if exists.
            Returns the updated comment if successful.
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
                                            name = "InvalidCommentStatusExample",
                                            summary = "Update comment without status",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Status is mandatory.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CommentNotFound",
                                            summary = "Comment not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Comment not found.\" }"
                                    )
                            )
                    )
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

    @Operation(
            summary = "Retrieves all comments associated with a specific object type and ID.",
            description =
            """
            Fetches comments linked to an object (service, product, or event) identified by its unique ID and specified
            comment type.
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
                                            name = "InvalidCommentStatusExample",
                                            summary = "Update comment without status",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Status is mandatory.\" }"
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
                                            name = "CommentNotFound",
                                            summary = "Comment not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Product not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<List<CommentResponseDto>> getComments(
            CommentType type,
            @Parameter(
                    description = "The unique identifier for the object (service, product, or event) to which the comments are associated.",
                    required = true,
                    example = "123"
            )
            Long objectId
    );
}
