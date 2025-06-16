package com.iss.eventorium.solution.api;

import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.dtos.RemoveImageRequestDto;
import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.products.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@Tag(
        name="Product",
        description = "Handles the product endpoints."
)
public interface ProductApi {

    @Operation(
            summary = "Retrieve a Product by ID.",
            description =
                    """
                    Returns the details of a single product using its unique identifier.
                    The product information is returned only if it exists and is not from a blocked provider,
                    nor is it hidden or pending.
                    Hidden and pending products are excluded for all users, except when the provider is logged in.
                    In that case, the provider can view their own hidden and pending products, while other users cannot access them.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
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
    ResponseEntity<ProductDetailsDto> getProduct(@PathVariable("id") Long id);

    @Operation(
            summary = "Creates a product",
            description =
                    """
                    Creates a new product. If the product's category is "proposed", the product will be assigned a status of 'PENDING'.
                    To upload product images, use the endpoint `POST /api/v1/products/{id}/images`.
                    Returns the created product if successful.
                    Requires authentication and PROVIDER authority.
                    Only users with the `PROVIDER` authority can access this endpoint.
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
                                            name = "InvalidProductExample",
                                            summary = "Product without name",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Name is mandatory.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions")
            }
    )
    ResponseEntity<ProductResponseDto> createProduct(
            @RequestBody(
                    description = "The data used to create the product.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateProductRequestDto.class))
            )
            CreateProductRequestDto createProductRequestDto
    );

    @Operation(
            summary = "Updates a product.",
            description =
                    """
                    Updates product if exists.
                    Returns the updated product if successful.
                    Requires authentication and PROVIDER authority.
                    Only users with the `PROVIDER` authority can access this endpoint.
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
                                            name = "InvalidProductExample",
                                            summary = "Product without name",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Product is mandatory.\" }"
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
    ResponseEntity<ProductResponseDto> updateProduct(
            @Parameter(
                    description = "The unique identifier of the product to update.",
                    required = true,
                    example = "123"
            )
            Long id,
            @RequestBody(
                    description = "The data used to update the product.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateProductRequestDto.class))
            )
            UpdateProductRequestDto request);


    @Operation(
            summary = "Deletes a product.",
            description =
                    """
                    Deletes product if exists.
                    Requires authentication and PROVIDER authority.
                    Only users with the `PROVIDER` authority can access this endpoint.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content", useReturnTypeSchema = true),
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
    ResponseEntity<Void> deleteProduct(
            @Parameter(
                    description = "The unique identifier of the product to delete.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Deletes a product images.",
            description =
                    """
                    Deletes product images if product exists.
                    Requires authentication and PROVIDER authority.
                    Only users with the `PROVIDER` authority can access this endpoint.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content", useReturnTypeSchema = true),
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
    ResponseEntity<Void> deleteImages(
            @Parameter(
                    description = "The unique identifier of the product.",
                    required = true,
                    example = "123"
            )
            Long id,
            List<RemoveImageRequestDto> removedImages
    );

    @Operation(
            summary = "Retrieves the top 5 products based on their average rating.",
            description =
                    """
                    Returns the top 5 products based on their average rating,
                    excluding those from blocked providers if the user is logged in.
                    Hidden products are excluded for ORGANIZER and unauth user.
                    When PROVIDER is logged in he can view their own hidden products
                    if they are among top five.
                    Among top products hidden will be shown to ADMIN if they are among top five
                    Products without ratings are not included!
                    Pending will be excluded since they don't have ratings!
                    If fewer than 5 products have a rating, fewer than 5 will be returned.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<Collection<ProductSummaryResponseDto>> getTopProducts();

    @Operation(
            summary = "Fetches all available products.",
            description =
                    """
                    Returns a list of all available products, excluding those from blocked providers if the user is logged in.
                    Hidden and pending products are excluded for all users, except when the provider is logged in.
                    In that case, the provider can view their own hidden and pending products, while other users cannot access them.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<Collection<ProductSummaryResponseDto>> getProducts();

    @Operation(
            summary = "Retrieves a paginated list of products.",
            description =
                    """
                    Returns a subset of products based on pagination parameters,
                    excluding those from any blocked providers if the user is logged in.
                    Hidden and pending products are excluded for all users, except when the provider is logged in.
                    In that case, the provider can view their own hidden and pending products, while other users cannot access them.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<PagedResponse<ProductSummaryResponseDto>> getProductsPaged(Pageable pageable);

    @Operation(
            summary = "Retrieve images for a products using its unique product ID.",
            description =
                    """
                    Fetches all images associated with a specific product using its unique identifier (product ID).
                    If images exist for the product, a list of them will be returned.
                    Excludes products from blocked providers if the user is logged in.
                    Hidden and pending products are excluded for all users, except when the provider is logged in.
                    In that case, the provider can get images for their own hidden and pending products, while other users cannot access them.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product or Image not found",
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
    ResponseEntity<List<ImageResponseDto>> getImages(
            @Parameter(
                    description = "The unique identifier of the product whose images are to be retrieved.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Retrieves a paginated list of products based on the provided filter criteria.",
            description =
                    """
                    Returns a subset of products based on pagination parameters and filter criteria.
                    excluding those from any blocked providers if the user is logged in.
                    Hidden and pending products are excluded for all users, except when the provider is logged in.
                    In that case, the provider can view their own hidden and pending products, while other users cannot access them.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidFilterExample",
                                            summary = "Filter with invalid price.",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"The value of maximum price must be greater than zero\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<PagedResponse<ProductSummaryResponseDto>> filterProducts(ProductFilterDto filter, Pageable pageable);

    @Operation(
            summary = "Filters products based on the provided filter criteria.",
            description =
                    """
                    Filters products based on various criteria, including name, type, price, etc.
                    Excludes products from blocked providers if the user is logged in.
                    Hidden and pending products are excluded for all users, except when the provider is logged in.
                    In that case, the provider can view their own hidden and pending products, while other users cannot access them.
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
                                            summary = "Filter with invalid price.",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"The value of maximum price must be greater than zero\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<List<ProductSummaryResponseDto>> filterProducts(ProductFilterDto filter);

    @Operation(
            summary = "Retrieves a paginated list of products based on the provided keyword.",
            description =
                    """
                    Returns a subset of products based on the provided keyword. The keyword is only used to match against product names.
                    Excludes products from blocked providers if the user is logged in.
                    Hidden and pending products are excluded for all users, except when the provider is logged in.
                    In that case, the provider can view their own hidden and pending products, while other users cannot access them.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<PagedResponse<ProductSummaryResponseDto>> searchProducts(String keyword, Pageable pageable);

    @Operation(
            summary = "Search products based on the provided keyword.",
            description =
                    """
                    Searches products based on the provided keyword. The keyword is only used to match against product names.
                    If no keyword is provided (empty string), all products are eligible.
                    Excludes products from blocked providers if the user is logged in.
                    Hidden and pending products are excluded for all users, except when the provider is logged in.
                    In that case, the provider can view their own hidden and pending products, while other users cannot access them.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<List<ProductSummaryResponseDto>> searchProducts(String keyword);

    @Operation(
            summary = "Retrieve images for a product using its unique product ID.",
            description =
                    """
                    Fetches all images associated with a specific product using its unique identifier (product ID).
                    If images exist for the product, a list of them will be returned.
                    Excludes products from blocked providers if the user is logged in.
                    Hidden and pending products are excluded for all users, except when the provider is logged in.
                    In that case, the provider can get images for their own hidden and pending products, while other users cannot access them.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product or Image not found",
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
    ResponseEntity<byte[]> getImage(
            @Parameter(
                    description = "The unique identifier of the product whose image is to be retrieved.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Uploads images for a product using its unique product ID.",
            description =
                    """
                    Uploads images for the specified product. The `id` parameter refers to the product ID.
                    For more details on creating a product, please refer to the endpoint `POST /api/v1/products`.
                    Requires authentication and PROVIDER authority.
                    Only users with the `PROVIDER` authority can access this endpoint.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
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
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Failed to upload image",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "FailedToUploadImage",
                                            summary = "Error while uploading images",
                                            value = "{ \"error\": \"Internal Server Error\", \"message\": \"Error while uploading images.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<Void> uploadImages(
            @Parameter(
                    description = "The unique identifier of the product to upload images.",
                    required = true,
                    example = "123"
            )
            Long id,
            List<MultipartFile> images
    );
}
