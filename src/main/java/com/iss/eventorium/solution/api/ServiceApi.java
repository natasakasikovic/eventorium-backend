package com.iss.eventorium.solution.api;

import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.services.*;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@Tag(name = "Service")
public interface ServiceApi {

    @Operation(
            summary = "Fetches all available services.",
            description =
            """
            Returns a list of all available services, excluding those from blocked providers if the user is logged in.
            Hidden and pending services are excluded for all users, except when the provider is logged in.
            In that case, the provider can view their own hidden and pending services, while other users cannot access them.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<List<ServiceSummaryResponseDto>> getAllServices();

    @Operation(
            summary = "Retrieves a paginated list of services.",
            description =
            """
            Returns a subset of services based on pagination parameters,
            excluding those from any blocked providers if the user is logged in.
            Hidden and pending services are excluded for all users, except when the provider is logged in.
            In that case, the provider can view their own hidden and pending services, while other users cannot access them.
            """,
            responses = {
                @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> getServicesPaged(Pageable pageable);

    @Operation(
            summary = "Retrieves the top 5 services based on their average rating.",
            description =
            """
            Returns the top 5 services based on their average rating,
            excluding those from blocked providers if the user is logged in.
            Hidden and pending services are excluded for all users, except when the provider is logged in.
            In that case, the provider can view their own hidden and pending services, while other users cannot access them.
            Services without ratings are not included.
            If fewer than 5 services have a rating, fewer than 5 will be returned.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<Collection<ServiceSummaryResponseDto>> getTopServices();

    @Operation(
            summary = "Filters services based on the provided filter criteria.",
            description =
            """
            Filters services based on various criteria, including name, type, price, etc.
            Excludes services from blocked providers if the user is logged in.
            Hidden and pending services are excluded for all users, except when the provider is logged in.
            In that case, the provider can view their own hidden and pending services, while other users cannot access them.
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
    ResponseEntity<List<ServiceSummaryResponseDto>> filterServices(ServiceFilterDto filter);

    @Operation(
            summary = "Retrieves a paginated list of services based on the provided filter criteria.",
            description =
            """
            Returns a subset of services based on pagination parameters and filter criteria.
            excluding those from any blocked providers if the user is logged in.
            Hidden and pending services are excluded for all users, except when the provider is logged in.
            In that case, the provider can view their own hidden and pending services, while other users cannot access them.
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
    ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> filterServices(ServiceFilterDto filter, Pageable pageable);

    @Operation(
            summary = "Retrieves a paginated list of services based on the provided keyword.",
            description =
            """
            Returns a subset of services based on the provided keyword. The keyword is only used to match against service names.
            Excludes services from blocked providers if the user is logged in.
            Hidden and pending services are excluded for all users, except when the provider is logged in.
            In that case, the provider can view their own hidden and pending services, while other users cannot access them.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<List<ServiceSummaryResponseDto>> searchServices(String keyword);

    @Operation(
            summary = "Search services based on the provided keyword.",
            description =
            """
            Searches services based on the provided keyword. The keyword is only used to match against service names.
            If no keyword is provided (empty string), all services are eligible.
            Excludes services from blocked providers if the user is logged in.
            Hidden and pending services are excluded for all users, except when the provider is logged in.
            In that case, the provider can view their own hidden and pending services, while other users cannot access them.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> searchServicesPaged(String keyword, Pageable pageable);

    @Operation(
            summary = "Retrieve a Service by ID.",
            description =
            """
            Returns the details of a single service using its unique identifier.
            The service information is returned only if it exists and is not from a blocked provider,
            nor is it hidden or pending.
            If the provider is logged in, their hidden and pending services will also be retrieved.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
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
    ResponseEntity<ServiceDetailsDto> getService(
            @Parameter(
                    description = "The unique identifier of the service to retrieve.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Retrieve images for a service using its unique service ID.",
            description =
            """
            Fetches all images associated with a specific service using its unique identifier (service ID).
            If images exist for the service, a list of them will be returned.
            Excludes services from blocked providers if the user is logged in.
            Hidden and pending services are excluded for all users, except when the provider is logged in.
            In that case, the provider can get images for their own hidden and pending services, while other users cannot access them.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Service or Image not found",
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
    ResponseEntity<List<ImageResponseDto>> getImages(
            @Parameter(
                    description = "The unique identifier of the service whose images are to be retrieved.",
                    required = true,
                    example = "123"
            )
            Long id
    );


    @Operation(
            summary = "Retrieve main image for a service using its unique service ID.",
            description =
            """
            Fetches main image associated with a specific service using its unique identifier (service ID).
            Excludes services from blocked providers if the user is logged in.
            Hidden and pending services are excluded for all users, except when the provider is logged in.
            In that case, the provider can get image for their own hidden and pending services, while other users cannot access them.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Service or Image not found",
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
    ResponseEntity<byte[]> getImage(
            @Parameter(
                    description = "The unique identifier of the service whose image is to be retrieved.",
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
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions")
            }
    )
    ResponseEntity<List<ServiceSummaryResponseDto>> getBudgetSuggestions(
            @RequestParam("categoryId") Long id,
            @RequestParam("eventId") Long eventId,
            @RequestParam("price") Double price
    );

    @Operation(
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
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions")
            }
    )
    ResponseEntity<ServiceResponseDto> createService(
            @RequestBody(
                    description = "The data used to create the service.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateServiceRequestDto.class))
            )
            CreateServiceRequestDto createServiceRequestDto
    );

    @Operation(
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions")
            }
    )
    ResponseEntity<Void> uploadServiceImages(
            @Parameter(
                    description = "The unique identifier of the service to upload images.",
                    required = true,
                    example = "123"
            )
            Long id,
            @RequestParam("images") List<MultipartFile> images
    );

    @Operation(
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
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions")
            }
    )
    ResponseEntity<ServiceResponseDto> updateService(
            @Parameter(
                    description = "The unique identifier of the service to update.",
                    required = true,
                    example = "123"
            )
            Long id,
            @RequestBody(
                    description = "The data used to update the service.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateServiceRequestDto.class))
            )
            UpdateServiceRequestDto serviceDto
    );

    @Operation(
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions")
            }
    )
    ResponseEntity<Void> deleteService(
            @Parameter(
                    description = "The unique identifier of the service to delete.",
                    required = true,
                    example = "123"
            )
            Long id
    );
}
