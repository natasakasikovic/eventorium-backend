package com.iss.eventorium.company.api;

import com.iss.eventorium.company.dtos.*;
import com.iss.eventorium.shared.dtos.ImageResponseDto;
import com.iss.eventorium.shared.dtos.RemoveImageRequestDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(
        name="Company",
        description =
        """
        Handles the company endpoints.
        """
)
public interface CompanyApi {

    @Operation(
            summary = "Creates a company.",
            description =
            """
            Creates a new company.
            To upload company images, use the endpoint `POST /api/v1/companies/{id}/images`.
            Returns the created company if successful.
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidCompanyExample",
                                            summary = "Company without name",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Name is mandatory.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "502",
                            description = "Bad gateway",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "EmailNotSent",
                                            summary = "Failed to send email",
                                            value = "{\"error\": \"Bad gateway\", \n \"message\": \"Failed to send email to provider@gmail.com\"}"
                                    )
                            )
                    ),

            }
    )
    ResponseEntity<CompanyResponseDto> createCompany(
            @RequestBody(
                    description = "The data used to create the company.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CompanyRequestDto.class))
            )
            CompanyRequestDto companyDto
    );

    @Operation(
            summary = "Uploads images for a company using its unique ID.",
            description =
            """
            Uploads images for the specified company. The `id` parameter refers to the company ID.
            For more details on creating a company, please refer to the endpoint `POST /api/v1/companies`.
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Company not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CompanyNotFound",
                                            summary = "Company not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Company not found.\" }"
                                    )
                            )
                    ),
            }
    )
    ResponseEntity<Void> uploadImages(
            @Parameter(
                    description = "The unique identifier of the company to upload images.",
                    required = true,
                    example = "123"
            )
            Long id,
            List<MultipartFile> images
    );

    @Operation(
            summary = "Retrieve company details associated with a provider.",
            description =
            """
            Returns the details of a company using the provider's unique identifier.
            Requires authentication and PROVIDER authority.
            Only users with the 'PROVIDER' authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Company not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CompanyNotFound",
                                            summary = "Company not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Company not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<ProviderCompanyDto> getCompany();

    @Operation(
            summary = "Retrieve a company details by ID.",
            description =
            """
            Returns the details of a company using its unique identifier.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Company not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CompanyNotFound",
                                            summary = "Company not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Company not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<CompanyDetailsDto> getCompany(
            @Parameter(
                    description = "The unique identifier of the company.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Retrieve images for a company using its unique ID.",
            description =
            """
            Fetches all images associated with a specific company using its unique identifier (company ID).
            If images exist for the company, a list of them will be returned.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Company or Image not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CompanyNotFound",
                                            summary = "Company not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Company not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<List<ImageResponseDto>> getImages(
            @Parameter(
                    description = "The unique identifier of the company.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Updates a company.",
            description =
            """
            Updates company if exists.
            To update company images, use the endpoint `PUT /api/v1/companies/{id}/images`.
            Returns the updated company id and name if successful.
            Requires authentication and PROVIDER authority.
            Only users with the 'PROVIDER' authority can access this endpoint.
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
                                            name = "InvalidCompanyExample",
                                            summary = "Company without address",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Address is mandatory.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Company not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CompanyNotFound",
                                            summary = "Company not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Company not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<CompanyResponseDto> updateCompany(
            @RequestBody(
                    description = "The data used to update the company.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateCompanyRequestDto.class))
            )
            UpdateCompanyRequestDto request
    );

    @Operation(
            summary = "Updates images for a company associated with a provider.",
            description =
            """
            Updates images for the specified company using the provider's unique identifier.
            For more details on updating a company, please refer to the endpoint `PUT /api/v1/companies`.
            Requires authentication and PROVIDER authority.
            Only users with the 'PROVIDER' authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Company not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CompanyNotFound",
                                            summary = "Company not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Company not found.\" }"
                                    )
                            )
                    ),
            }
    )
    ResponseEntity<Void> uploadNewImages(List<MultipartFile> newImages);

    @Operation(
            summary = "Deletes a company images.",
            description =
            """
            Deletes company images if company exists.
            Requires authentication and PROVIDER authority.
            Only users with the 'PROVIDER' authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Company not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "CompanyNotFound",
                                            summary = "Company not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"Company not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<Void> deleteImages(List<RemoveImageRequestDto> removedImages);
}
