package com.iss.eventorium.category.api;

import com.iss.eventorium.category.dtos.CategoryRequestDto;
import com.iss.eventorium.category.dtos.CategoryResponseDto;
import com.iss.eventorium.category.dtos.UpdateStatusRequestDto;
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
public interface CategoryProposalApi {

    @Operation(
            summary = "Fetches all proposed (pending) categories.",
            description =
            """
            Retrieves a list of all proposed (pending) categories.
            Requires authentication and ADMIN authority.
            Only users with the `ADMIN` authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
            }
    )
    ResponseEntity<List<CategoryResponseDto>> getPendingCategories();

    @Operation(
            summary = "Retrieves a paginated list of proposed (pending) categories.",
            description =
            """
            Returns a subset of proposed (pending) categories based on pagination parameters.
            Requires authentication and ADMIN authority.
            Only users with the `ADMIN` authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
            }
    )
    ResponseEntity<PagedResponse<CategoryResponseDto>> getPendingCategoriesPaged(Pageable pageable);

    @Operation(
            summary = "Updates proposed category status.",
            description =
            """
            Accepts or declines the proposed category and updates the solution status to either 'ACCEPTED' or 'DECLINED' accordingly.
            Returns an updated category.
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
                                            name = "InvalidUpdateCategoryStatusExample",
                                            summary = "Status not provided",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Status is mandatory.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
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
    ResponseEntity<CategoryResponseDto> updateCategoryStatus(
            @Parameter(
                    description = "The unique identifier of the category to update.",
                    required = true,
                    example = "123"
            )
            Long id,
            @RequestBody(
                    description = "New category status.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateStatusRequestDto.class))
            )
            UpdateStatusRequestDto request
    );

    @Operation(
            summary = "Modifies the category and sets solution status to 'ACCEPTED'.",
            description =
            """
            Updates category and sets solution status to `ACCEPTED`.
            Returns an updated category.
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
                                            name = "InvalidCategoryExample",
                                            summary = "Category without name",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Name is mandatory.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
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
    ResponseEntity<CategoryResponseDto> updateCategoryProposal(
            @Parameter(
                    description = "The unique identifier of the category to update.",
                    required = true,
                    example = "123"
            )
            Long id,
            @RequestBody(
                    description = "The data used to update the category.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryRequestDto.class))
            )
            CategoryRequestDto request
    );

    @Operation(
            summary = "Replaces the proposed category with an existing category and sets solution status to 'ACCEPTED'.",
            description =
            """
            Replaces the proposed category with an existing category and sets solution status to `ACCEPTED`.
            Returns an existing category.
            Requires authentication and ADMIN authority.
            Only users with the `ADMIN` authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",  useReturnTypeSchema = true),
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
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
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
    ResponseEntity<CategoryResponseDto> changeCategoryProposal(
            @Parameter(
                    description = "The unique identifier of the category to change.",
                    required = true,
                    example = "123"
            )
            Long id,
            @RequestBody(
                    description = "The data used to change the category.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryRequestDto.class))
            )
            CategoryRequestDto request
    );
}
