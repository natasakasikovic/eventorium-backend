package com.iss.eventorium.solution.api;

import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.dtos.RemoveImageRequestDto;
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
            Hidden services are excluded for ORGANIZER and unauth user.
            When PROVIDER is logged in he can view their own hidden services
            if they are among top five.
            Among top services hidden will be shown to ADMIN if they are among top five
            Services without ratings are not included!
            Pending will be excluded since they dont have ratings!
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
    ResponseEntity<List<ServiceSummaryResponseDto>> searchServices(String keyword);

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
    ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> searchServicesPaged(String keyword, Pageable pageable);

    @Operation(
            summary = "Retrieve a Service by ID.",
            description =
            """
            Returns the details of a single service using its unique identifier.
            The service information is returned only if it exists and is not from a blocked provider,
            nor is it hidden or pending.
            Hidden and pending services are excluded for all users, except when the provider is logged in.
            In that case, the provider can view their own hidden and pending services, while other users cannot access them.
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
            summary = "Recommend services based on the user's budget, considering both price and service category.",
            description =
            """
            Returns list of recommended options based on the user’s budget,
            selecting services from relevant category that fit within the specified price range.
            Requires authentication and EVENT_ORGANIZER authority.
            Only users with the 'EVENT_ORGANIZER' authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
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
    ResponseEntity<List<ServiceSummaryResponseDto>> getBudgetSuggestions(
            @Parameter(
                    description = "The unique identifier of the category.",
                    required = true,
                    example = "123"
            )
            Long id,
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long eventId,
            @Parameter(
                    description = "The maximum price a user is willing to pay for the service.",
                    required = true,
                    example = "123"
            )
            Double price
    );

    @Operation(
            summary = "Creates a service",
            description =
            """
            Creates a new service. If the service's category is "proposed", the service will be assigned a status of 'PENDING'.
            To upload service images, use the endpoint `POST /api/v1/services/{id}/images`.
            Returns the created service if successful.
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
            summary = "Uploads images for a service using its unique service ID.",
            description =
            """
            Uploads images for the specified service. The `id` parameter refers to the service ID.
            For more details on creating a service, please refer to the endpoint `POST /api/v1/services`.
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
                            description = "Service not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "ServiceNotFound",
                                            summary = "Service not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Service not found.\" }"
                                    )
                            )
                    ),
            }
    )
    ResponseEntity<Void> uploadServiceImages(
            @Parameter(
                    description = "The unique identifier of the service to upload images.",
                    required = true,
                    example = "123"
            )
            Long id,
            List<MultipartFile> images
    );

    @Operation(
            summary = "Updates a service.",
            description =
            """
            Updates service if exists.
            Returns the updated service if successful.
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
            summary = "Deletes a service.",
            description =
            """
            Deletes service if exists and  is not associated with any reservation.
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
                            description = "Service not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "ServiceNotFound",
                                            summary = "Service not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Service not found.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Service conflict. This may occur if the service is reserved.",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CategoryInUseExample",
                                            summary = "Category in use",
                                            value = "{ \"error\": \"Conflict\", \"message\": \"The service cannot be deleted because it is currently reserved.\" }"
                                    )
                            )
                    )
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

    @Operation(
            summary = "Deletes a service images.",
            description =
            """
            Deletes service images if service exists.
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
    ResponseEntity<Void> deleteImages(
            @Parameter(
                    description = "The unique identifier of the service.",
                    required = true,
                    example = "123"
            )
            Long id,
            List<RemoveImageRequestDto> removedImages
    );
}
