package com.iss.eventorium.user.api;

import com.iss.eventorium.user.dtos.role.RoleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

@Tag(
        name="Role",
        description =
        """
        Handles the role endpoints.
        """
)
public interface RoleApi {

    @Operation(
            summary = "Fetch all available registration roles.",
            description =
            """
            Returns a list of all roles that users can register for.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<Collection<RoleDto>> getRegistrationRoles();
}
