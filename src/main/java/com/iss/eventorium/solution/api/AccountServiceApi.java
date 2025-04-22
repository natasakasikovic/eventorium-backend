package com.iss.eventorium.solution.api;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.shared.models.PagedResponse;
import com.iss.eventorium.solution.dtos.services.ServiceFilterDto;
import com.iss.eventorium.solution.dtos.services.ServiceResponseDto;
import com.iss.eventorium.solution.dtos.services.ServiceSummaryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Account Service", description = "Only users with the 'PROVIDER' authority can access this endpoints.")
public interface AccountServiceApi {

    @Operation(
            summary = "Fetches all provider services.",
            description =
            """
            Returns a list of all provider's services.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<ServiceSummaryResponseDto>> getProfileServices();

    @Operation(
            summary = "Retrieves a paginated list of provider's services.",
            description =
            """
            Returns a subset of provider's services.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> getProfileServicesPaged(Pageable pageable);

    @Operation(
            summary = "Filters provider's services based on the provided filter criteria.",
            description =
            """
            Filters provider's services based on various criteria, including name, type, price, etc.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<ServiceSummaryResponseDto>> filterAccountServices(ServiceFilterDto filter);

    @Operation(
            summary = "Retrieves a paginated list of provider's services based on the provided filter criteria.",
            description =
            """
            Returns a subset of provider's services based on pagination parameters and filter criteria.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> filerAccountServicesPaged(
            ServiceFilterDto filter,
            Pageable pageable
    );

    @Operation(
            summary = "Search provider's services based on the provided keyword.",
            description =
            """
            Searches provider's services based on the provided keyword. The keyword is only used to match against service names.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<ServiceSummaryResponseDto>> searchAccountServices(String keyword);


    @Operation(
            summary = "Retrieves a paginated list of provider's services based on the provided keyword.",
            description =
            """
            Returns a subset of provider's services based on the provided keyword.
            The keyword is only used to match against service names.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<PagedResponse<ServiceSummaryResponseDto>> searchAccountServicesPaged(
            String keyword,
            Pageable pageable
    );

    @Operation(
            summary = "Fetches provider's favourite services.",
            description =
            """
            Returns a list of all provider's favourite services.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    ResponseEntity<List<ServiceSummaryResponseDto>> getFavouriteServices();

    @Operation(
            summary = "Checks if the given service is one of the provider's favorite services.",
            description =
            """
            Returns 'true' if the given service is marked as one of the provider's favorite services.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
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
    ResponseEntity<Boolean> isFavouriteService(
            @Parameter(
                    description = "The unique identifier of the service.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Adds a service to the provider's list of favorites",
            description =
            """
            Adds a service to the provider's list of favorites. If the service is already a favorite, no action is taken.
            Returns the service if the operation is successful, or if the service is already a favorite.
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
                    )
            }
    )
    ResponseEntity<ServiceResponseDto> addFavouriteService(Long id);

    @Operation(
            summary = "Removes a service to the provider's list of favorites",
            description =
            """
            Removes a service from the provider's list of favorites. If the service is not already a favorite, no action is taken.
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
    ResponseEntity<Void> removeFavouriteService(
            @Parameter(
                    description = "The unique identifier of the service.",
                    required = true,
                    example = "123"
            )
            Long id
    );
}
