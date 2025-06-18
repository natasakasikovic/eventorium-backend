package com.iss.eventorium.user.api;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.user.dtos.auth.*;
import com.iss.eventorium.user.dtos.user.UpgradeAccountRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface AuthApi {

    @Operation(
            summary = "Authenticate user and issue access token",
            description =
             """
             Authenticates a user with provided credentials and returns a JWT access token.
             """,
            security = { @SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Account not verified",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "AccountNotVerified",
                                            summary = "Account is not verified",
                                            value = "{ \"error\": \"Unauthorized\", \"message\": \"Account is not verified. Check your email.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            ref = "#/components/responses/ForbiddenResponse"
                    )
            }
    )
    ResponseEntity<UserTokenState> createAuthenticationToken(
            @RequestBody(
                    description = "Email and password.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequestDto.class))
            )
            LoginRequestDto request);


    @Operation(
            summary = "Creates new unverified user",
            description =
            """
            Creates a new unverified account.
            If an account with the same credentials already exists and is verified, a 409 Conflict error is returned.
            If a registration request was previously submitted but the email verification period has not yet expired,
            the request is also rejected with a 403 Forbidden response, as the account can still be verified.
            However, if the previous verification period has expired, a new registration request is successfully created.
            
            """,
            security = { @SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - activation timeout",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "ActivationTimeout",
                                            summary = "Activation timeout",
                                            value = "{ \"error\": \"Unauthorized\", \"message\": \"Activation time has expired.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict - Email already taken",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "EmailAlreadyTaken",
                                            summary = "Email Already Taken",
                                            value = "{ \"error\": \"Conflict\", \"message\": \"This email is already taken. Please log in or activate your account via the email we sent you.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<AuthResponseDto> createAccount(
            @Valid @RequestBody(
                    description = "Data used to create new user.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthRequestDto.class))
            )
            AuthRequestDto user);

    @Operation(
            summary = "Uploads a profile photo",
            description =
             """
             Allows an unauthenticated user to upload a profile photo.
             The file must be sent as multipart/form-data.
             Returns 500 if the upload fails due to invalid file format, size, or internal error.
             Do not requires authentication.
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
            @Parameter(
                    description = "The unique identifier of a user who has previously submitted a registration request. The user is not verified yet.",
                    required = true,
                    example = "123"
            )
            Long id,
            @RequestParam("profilePhoto") MultipartFile file
    );

    @Operation(
            summary = "Creates new verified user",
            description =
            """
            Creates an account.
            After quick registration, the user can access the system with the 'USER' role.
            No additional verification is required.
            """,
            security = { @SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict - Email already taken",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "EmailAlreadyTaken",
                                            summary = "Email Already Taken",
                                            value = "{ \"error\": \"Conflict\", \"message\": \"Registration with this" +
                                                    " email address has already been completed. " +
                                                    "Please log in to access the application.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<Void> quickRegister(
            @Valid @RequestBody(
                    description = "Data used to create new user.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = QuickRegistrationRequestDto.class))
            )
            QuickRegistrationRequestDto request);

    @Operation(
            summary = "Verifies user's account",
            description =
                """
                Based on the hash, the system determines whether the user is eligible to verify the account.
                If so, upon successful verification, the user gains access to the system 
                with the role selected during registration.
                """,
            security = { @SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "303", description = "Redirect to login page", useReturnTypeSchema = true),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "ActivationTimeoutException",
                                            summary = "Activation timeout",
                                            value = "{ \"error\": \"Conflict\", \"message\": \"Activation time has expired.\" }"
                                    )
                            )
                    )
            }
    )
    ResponseEntity<Void> activateAccount(
            @Parameter(
                    description = "A unique hash provided to the user during registration.",
                    required = true,
                    example = "123"
            )
            String hash
    );

    @Operation(
            summary = "Upgrades account",
            description =
                    """
                    A user with the 'USER' role can be granted the 'PROVIDER' or 'ORGANIZER' role.
                    A new token is generated to reflect the user's elevated permissions based on the newly assigned role.
                    """,
            security = { @SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidDataExample",
                                            summary = "Address not provided",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Address is required\" }"
                                    )
                            )
                    ),
            }
    )
    ResponseEntity<UserTokenState> upgradeAccount(
            @Valid @RequestBody(
                    description = "Additional user information not provided during quick registration, along with a newly selected role.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = QuickRegistrationRequestDto.class))
            ) UpgradeAccountRequestDto request
    );
}
