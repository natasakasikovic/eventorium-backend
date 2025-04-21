package com.iss.eventorium.category.api;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name="Category")
public interface CategoryApi {

    @Operation(
            description = "Retrieves a list of all available categories.",
            summary = "Fetches all categories.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<List<CategoryResponseDto>> getCategories();

    @Operation(
            description = "Retrieves a paged list of all available categories.",
            summary =
            """
            Fetches one page of categories.
            Requires authentication and ADMIN authority.
            Only users with the 'ADMIN' authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<PagedResponse<CategoryResponseDto>> getCategoriesPaged(Pageable pageable);

    @Operation(
            summary = "Retrieve a Category by ID",
            description =
            """
            Fetches the details of a single category using its unique identifier.
            Returns the category information if it exists.
            Requires authentication and ADMIN authority.
            Only users with the 'ADMIN' authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Category not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CategoryNotFound",
                                            summary = "Category not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Category not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<CategoryResponseDto> getCategory(
            @Parameter(
                    description = "The unique identifier of the category to retrieve.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Creates a category.",
            description =
            """
            Creates a new category.
            Returns the created category if successful.
            Requires authentication and ADMIN authority.
            Only users with the 'ADMIN' authority can access this endpoint.
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
                                            name = "InvalidCategoryExample",
                                            summary = "Category without name",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Name is mandatory.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Category conflict. This may occur if the category with the same name already exists.",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CategoryConflictExample",
                                            summary = "Category name already exists",
                                            value = "{ \"error\": \"Conflict\", \"message\": \"Category with the name 'Food' already exists.\" }"
                                    )
                            )
                    )

            }
    )
    ResponseEntity<CategoryResponseDto> createCategory(
            @RequestBody(
                    description = "The data used to create the category.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryRequestDto.class))
            )
            CategoryRequestDto requestDto
    );

    @Operation(
            summary = "Updates a category.",
            description =
            """
            Updates category if exists.
            Returns the updated category if successful.
            Requires authentication and ADMIN authority.
            Only users with the 'ADMIN' authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Category not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CategoryNotFound",
                                            summary = "Category not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Category not found.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidCategoryExample",
                                            summary = "Category without name",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Name is mandatory.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "407",
                            description = "Category conflict. This may occur if the category with the same name already exists.",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                        name = "CategoryConflictExample",
                                        summary = "Category name already exists",
                                        value = "{ \"error\": \"Conflict\", \"message\": \"Category with the name 'Food' already exists.\" }"
                                    )
                            )
                    )

            }
    )
    ResponseEntity<CategoryResponseDto> updateCategory(
            @RequestBody(
                    description = "The data used to update the category.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryRequestDto.class))
            )
            CategoryRequestDto requestDto,
            @Parameter(
                    description = "The unique identifier of the category to update.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Deletes a category.",
            description =
            """
            Deletes category if exists.
            Requires authentication and ADMIN authority.
            Only users with the 'ADMIN' authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Category not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CategoryNotFound",
                                            summary = "Category not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Category not found.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "407",
                            description = "Category conflict. This may occur if the category is in use.",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CategoryInUseExample",
                                            summary = "Category in use",
                                            value = "{ \"error\": \"Conflict\", \"message\": \"Unable to delete category because it is currently associated with an active solution.\" }"
                                    )
                            )
                    )

            }
    )
    ResponseEntity<Void> deleteCategory(Long id);
}
