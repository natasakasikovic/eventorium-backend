package com.iss.eventorium.user.api;

import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.user.dtos.report.UpdateReportRequestDto;
import com.iss.eventorium.user.dtos.report.UserReportRequestDto;
import com.iss.eventorium.user.dtos.report.UserReportResponseDto;
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

import java.util.Collection;

@Tag(
        name="UserReport",
        description = "Handles the user report endpoints."
)
public interface UserReportApi {


    @Operation(
            summary = "Fetches all pending report.",
            description = """ 
            Returns a list of all pending reports.
            Requires authentication and ADMIN authority.
            Only users with the `ADMIN` authority can access this endpoint.""",
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions")
            }
    )
    public ResponseEntity<Collection<UserReportResponseDto>> getPendingReports();


    @Operation(
            summary = "Creates a user report.",
            description = """
                    Creates a report against a specific user (offender) with provided reason.
                    Requires AUTHENTICATION. Only authenticated users can report other users.
                    """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Report successfully created"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "MissingReasonExample",
                                            summary = "Missing report reason",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Report must contain reason!\" }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<Void> createReport(
            @RequestBody(
                    description = "The data used to create report.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserReportRequestDto.class))
            ) UserReportRequestDto report,
            @Parameter(
                    description = "The unique identifier of the user who will be blocked.",
                    required = true,
                    example = "123") Long id);



    @Operation(
            summary = "Updates the status of a report.",
            description = """
                    Allows partial update of a report's status by ID.
                    Requires authentication and ADMIN authority.
                    Only users with the `ADMIN` authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Report status successfully updated"),
                    @ApiResponse( responseCode = "400", description = "Validation error",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "InvalidStatusExample",
                                            summary = "Missing or invalid status",
                                            value = "{ \"error\": \"Bad Request\", \"message\": \"Status must be PENDING, ACCEPTED or DECLINED \" }"
                                    )
                            )
                    ),
                    @ApiResponse( responseCode = "404",
                            description = "Resource not found",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "ReportNotFound",
                                                    summary = "Report not found",
                                                    value = "{ \"error\": \"Not Found\", \"message\": \"Report with ID 12 not found.\" }"
                                            ),
                                            @ExampleObject(
                                                    name = "UserNotFound",
                                                    summary = "User not found",
                                                    value = "{ \"error\": \"Not Found\", \"message\": \"User with ID 5 not found.\" }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - not enough permissions"),
            }
    )
    public ResponseEntity<Void> updateStatus(
            @Parameter(
                    description = "ID of the report to be updated",
                    required = true,
                    example = "12" ) Long id,
            @RequestBody(
                    description = "The data used to update the report.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateReportRequestDto.class))
            ) UpdateReportRequestDto request);
}