package com.iss.eventorium.solution.api;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.pricelists.PriceListResponseDto;
import com.iss.eventorium.solution.dtos.pricelists.UpdatePriceRequestDto;
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

@Tag(name = "Price List", description = "Only users with the 'PROVIDER' authority can access this endpoints.")
public interface PriceListApi {

    @Operation(
            summary = "Retrieves the pricing and available discounts for provider's services.",
            description =
            """
            Returns the pricing and available discounts for provider's services.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<PriceListResponseDto>> getPriceListServices();

    @Operation(
            summary = "Retrieves a paginated list of pricing and available discounts for a provider's services.",
            description =
            """
            Returns a subset of pricing and available discounts for a provider's services.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<PagedResponse<PriceListResponseDto>> getPriceListServicesPaged(Pageable pageable);

    @Operation(
            summary = "Retrieves the pricing and available discounts for provider's products.",
            description =
            """
            Returns the pricing and available discounts for provider's products.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<PriceListResponseDto>> getPriceListProducts();

    @Operation(
            summary = "Retrieves a paginated list of pricing and available discounts for a provider's products.",
            description =
            """
            Returns a subset of pricing and available discounts for a provider's products.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<PagedResponse<PriceListResponseDto>> getPriceListProductsPaged(Pageable pageable);

    @Operation(
            summary = "Updates a service price and discount.",
            description =
            """
            Updates service price and discount if exists.
            Returns the updated service if successful.
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
                                            name = "InvalidServiceExample",
                                            summary = "Service without price",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Price is mandatory.\" }"
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
                                            summary = "Service type not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Service not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<PriceListResponseDto> updateServicePrice(
            @Parameter(
                    description = "The unique identifier of the service to update.",
                    required = true,
                    example = "123"
            )
            Long id,
            @RequestBody(
                    description = "The data used to update the service.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdatePriceRequestDto.class))
            )
            UpdatePriceRequestDto updateRequestDto
    );

    @Operation(
            summary = "Updates a product price and discount.",
            description =
            """
            Updates product price and discount if exists.
            Returns the updated product if successful.
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
                                            summary = "Product without price",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Price is mandatory.\" }"
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
                                            summary = "Product type not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Product not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<PriceListResponseDto> updateProductPrice(
            @Parameter(
                    description = "The unique identifier of the product to update.",
                    required = true,
                    example = "123"
            )
            Long id,
            @RequestBody(
                    description = "The data used to update the product.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdatePriceRequestDto.class))
            )
            UpdatePriceRequestDto updateRequestDto
    );

    @Operation(
            summary = "Generates and returns price list pdf.",
            description = "Generates and returns price list pdf.",
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<byte[]> getPdf();
}
