package com.iss.eventorium.shared.api;

import com.iss.eventorium.shared.dtos.CityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(
        name="City",
        description =
        """
        Handles the city endpoints.
        """
)
public interface CityApi {

    @Operation(
            summary = "Fetches all available cities.",
            description =
            """
            Returns a list of all available cities.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<List<CityDto>> getAllCities();
}
