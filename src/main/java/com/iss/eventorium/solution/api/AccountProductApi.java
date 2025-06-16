package com.iss.eventorium.solution.api;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.products.ProductFilterDto;
import com.iss.eventorium.solution.dtos.products.ProductSummaryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Tag(
        name="Account Product",
        description = "Handles the account product endpoints."
)
public interface AccountProductApi {

    @Operation(
            summary = "Fetches all provider products.",
            description =
            """
            Returns a list of all provider's products.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<ProductSummaryResponseDto>> getAllProducts();

    @Operation(
            summary = "Retrieves a paginated list of provider's products.",
            description =
            """
            Returns a subset of provider's products.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<PagedResponse<ProductSummaryResponseDto>> getProductsPaged(Pageable pageable);

    @Operation(
            summary = "Filters provider's products based on the provided filter criteria.",
            description =
            """
            Filters provider's products based on various criteria, including name, type, price, etc.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<ProductSummaryResponseDto>> filterAccountProducts(@ModelAttribute ProductFilterDto filter);

    @Operation(
            summary = "Retrieves a paginated list of provider's products based on the provided filter criteria.",
            description =
            """
            Returns a subset of provider's products based on pagination parameters and filter criteria.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<PagedResponse<ProductSummaryResponseDto>> filterAccountProductsPaged(@ModelAttribute ProductFilterDto filter, Pageable pageable);

    @Operation(
            summary = "Search provider's products based on the provided keyword.",
            description =
            """
            Searches provider's products based on the provided keyword. The keyword is only used to match against product names.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<ProductSummaryResponseDto>> searchAccountProducts(String keyword);

    @Operation(
            summary = "Retrieves a paginated list of provider's products based on the provided keyword.",
            description =
            """
            Returns a subset of provider's products based on the provided keyword.
            The keyword is only used to match against product names.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<PagedResponse<ProductSummaryResponseDto>> searchAccountProductsPaged(String keyword, Pageable pageable);

    @Operation(
            summary = "Fetches the user's favourite products.",
            description =
            """
            Returns a list of all favourite products for the currently logged-in user.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            }
    )
    ResponseEntity<List<ProductSummaryResponseDto>> getFavouriteProducts();

    @Operation(
            summary = "Checks if the given product is among the user's favourite products.",
            description =
            """
            Returns 'true' if the given product is marked as a favourite by the currently logged-in user.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
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
    ResponseEntity<Boolean> isFavouriteProduct(
            @Parameter(
                    description = "The unique identifier of the product.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Adds a product to the user's list of favourites.",
            description =
            """
            Adds a product to the currently logged-in user's list of favourite products.
            If the product is already marked as a favourite, no action is taken.
            Returns the product if the operation is successful or if it was already marked as a favourite.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
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
    ResponseEntity<Void> addFavouriteProduct(
            @Parameter(
                    description = "The unique identifier of the product.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Removes a product from the user's list of favourites.",
            description =
            """
            Removes a product from the currently logged-in user's list of favourite products.
            If the product is not marked as a favourite, no action is taken.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
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
    ResponseEntity<Void> removeFavouriteProduct(
            @Parameter(
                    description = "The unique identifier of the product.",
                    required = true,
                    example = "123"
            )
            Long id
    );
}
