package com.iss.eventorium.user.api;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.user.dtos.user.AccountDetailsDto;
import com.iss.eventorium.user.dtos.user.ChangePasswordRequestDto;
import com.iss.eventorium.user.dtos.user.UpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(
        name = "User",
        description = "Handles the user endpoints."
)
public interface UserApi {

    @Operation(
            summary = "Changes the password",
            description =
                    """
                    Changes the password of the currently logged-in user.
                    The minimum required role is 'USER', meaning the user must be authenticated.
                    """,
            security = { @SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidRequestExample",
                                            summary = "Old password does not match",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Old password does not match.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
            }
    )
    ResponseEntity<Void> changePassword(
            @Valid @RequestBody(
                description = "The data used to change password.",
                required = true,
                content = @Content(schema = @Schema(implementation = ChangePasswordRequestDto.class))
            )
            ChangePasswordRequestDto request
    );


    @Operation(
            summary = "Updates personal user information",
            description =
                    """
                    Updates the personal information of the currently logged-in user.
                    Only personal data can be modified using this endpoint.
                    
                    To change the password, use: PUT api/v1/users/password. \s
                    To update the profile photo, use: PUT api/v1/users/profile-photo.
            
                    Accessible only to authenticated users.
                    """,
            security = { @SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidPersonExample",
                                            summary = "User without address",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Address is required.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
            }
    )
    ResponseEntity<Void> update(
            @Valid @RequestBody(
                description = "The data used to update personal user data.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateRequestDto.class))
            ) UpdateRequestDto person
    );

    @Operation(
            summary = "Retrieves current user details",
            description =
                    """
                    Returns the details of the currently logged-in user.
                    Authorization is required.
                    """,
            security = { @SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
            }
    )
    ResponseEntity<AccountDetailsDto> getCurrentUser();

    @Operation(
            summary = "Retrieves public account information",
            description =
                    """
                    Returns basic public information about a specific user account.
                    Authentication is not required.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "UserNotFound",
                                            summary = "User not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"User not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<AccountDetailsDto> getUser(
            @Parameter(
                    description = "The unique identifier of the user.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Retrieves a user's profile photo",
            description = """
                     Returns the profile photo of a specific user if available.
                     Returns 404 if the user does not exist or is blocked by the currently authenticated user.
                     """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(mediaType = "image/jpeg")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "UserNotFound",
                                            summary = "User not found",
                                            value = "{ \"error\": \"Not found\", \"message\": \"User not found.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<byte[]> getProfilePhoto(
            @Parameter(
                    description = "The unique identifier of the user.",
                    required = true,
                    example = "123"
            )
            Long id
    );

    @Operation(
            summary = "Uploads a new profile photo",
            description = """
                     Allows an authenticated user to upload a new profile photo.
                     The file must be sent as multipart/form-data.
                     Returns 500 if the upload fails due to invalid file format, size, or internal error.
                     Requires authentication.
                     """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile photo uploaded successfully"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Upload failed due to file size, format, or server error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "UploadError",
                                            summary = "Upload failed",
                                            value = "{ \"error\": \"Internal Server Error\", \"message\": \"Profile photo could not be uploaded.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<byte[]> uploadProfilePhoto(
            @RequestParam("profilePhoto") MultipartFile file
    );

    @Operation(
            summary = "Deactivates the currently authenticated user's account",
            description = """
                     Deactivates the account of the currently authenticated user.
                     Depending on the user's role, certain conditions must be met in order to allow deactivation.
                     Returns 204 if deactivation is successful, or 409 if deactivation is not allowed.
                     Requires authentication.
                     """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Account deactivated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Deactivation not allowed due to role-based restrictions",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "DeactivationConflict",
                                            summary = "Deactivation conflict",
                                            value = "{ \"error\": \"Conflict\", \"message\": \"Account deactivation is not possible due to upcoming events.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<Void> deactivateAccount();
}
