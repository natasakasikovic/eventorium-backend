package com.iss.eventorium.event.api;

import com.iss.eventorium.event.dtos.budget.*;
import com.iss.eventorium.shared.models.ExceptionResponse;
import com.iss.eventorium.solution.dtos.products.ProductResponseDto;
import com.iss.eventorium.solution.dtos.products.SolutionReviewResponseDto;
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

import java.util.List;

@Tag(
        name = "Budget",
        description = "Handles the budget endpoints."
)
public interface BudgetApi {

    @Operation(
            summary = "Get event budget.",
            description =
            """
            Retrieves the budget details for a specific event by its ID.
            Requires authentication and ORGANIZER authority.
            Only users with the `ORGANIZER` authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
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
    ResponseEntity<BudgetResponseDto> getBudget(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long eventId
    );

    @Operation(
            summary = "Get event budget items.",
            description =
            """
            Retrieves the budget items associated with a specific event by its ID.
            If a budget item is marked as `PROCESSED`, it returns the version of the service or product as it existed at
            the time it was processed.
            Requires authentication and ORGANIZER authority.
            Only users with the `ORGANIZER` authority can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
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
    ResponseEntity<List<BudgetItemResponseDto>> getBudgetItems(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long eventId
    );

    @Operation(
            summary = "Get budget suggestions for an event based on price and category.",
            description =
            """
            Retrieves products and services that match the specified category and do not exceed the given price.
            Excludes services that are not reservable, based on their reservation deadline and the event's start date.
            Requires authentication and the `ORGANIZER` authority.
            Only users with the `ORGANIZER` role can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
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
    ResponseEntity<List<BudgetSuggestionResponseDto>> getBudgetSuggestions(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long eventId,
            @Parameter(
                    description = "The unique identifier of the category.",
                    required = true,
                    example = "123"
            )
            Long categoryId,
            @Parameter(
                    description = "Maximum price that products and services should not exceed.",
                    required = true,
                    example = "125.0"
            )
            Double price
    );

    @Operation(
            summary = "Get all budget items associated with the organizer's events.",
            description =
            """
            Retrieves all budget items associated with the organizer's events.
            All items are processed, with duplicates and those from blocked providers excluded.
            Requires authentication and the `ORGANIZER` authority.
            Only users with the `ORGANIZER` role can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
            }
    )
    ResponseEntity<List<SolutionReviewResponseDto>> getAllBudgetItems();


    @Operation(
            summary = "Create a new budget item and add it to the event budget.",
            description =
            """
            Creates a new budget item and adds it to the specified event's budget.
            If a solution with the same ID is already added to the budget and has not been processed,
            update its planned amount field instead of creating a new item.
            The planned amount cannot be lower than the net price of the product or service.
            Every created budget item is initially marked as `PLANNED`.
            Returns created budget item if successful.
            Requires authentication and the `ORGANIZER` authority.
            Only users with the `ORGANIZER` role can access this endpoint.
            <br/>
            To process an item directly:<br/>
            - Use `POST /api/v1/events/{event-id}/budget/purchase` for products.<br/>
            - Use `POST /api/v1/events/{event-id}/services/{service-id}/reservation` for services.<br/>
            <br/>
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
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
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Already processed",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "BudgetConflictExample",
                                            summary = "Solution is already precessed",
                                            value = "{ \"error\": \"Conflict\", \"message\": \"Solution is already precessed\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Insufficient funds",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(value =
                                            """
                                            {
                                              "error": "Unprocessable Entity",
                                              "message": "You didn't plan to invest this much money."
                                            }
                                            """
                                    )
                            )
                    ),
            }
    )
    ResponseEntity<BudgetItemResponseDto> createBudgetItem(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long eventId,
            @RequestBody(
                    description = "The data used to create the budget item.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BudgetItemRequestDto.class))
            )
            BudgetItemRequestDto request
    );

    @Operation(
            summary = "Purchase a product and add it to the event budget",
            description =
            """
            Creates a new budget item for the specified product and immediately marks it as `PROCESSED`.
            Unlike the standard creation endpoint, this endpoint is used when the product is being purchased directly,
            bypassing the `PLANNED` state. If you want to add the product to the budget as a planned item instead, use
            `POST /api/v1/events/{eventId}/budget/items`
            Returns purchased product.
            Requires authentication and the `ORGANIZER` authority.
            Only users with the `ORGANIZER` role can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
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
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Already processed",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "BudgetConflictExample",
                                            summary = "Solution is already precessed",
                                            value = "{ \"error\": \"Conflict\", \"message\": \"Solution is already precessed\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Insufficient funds",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(value =
                                        """
                                        {
                                          "error": "Unprocessable Entity",
                                          "message": "You do not have enough funds for this purchase/reservation!"
                                        }
                                        """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<ProductResponseDto> purchaseProduct(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long eventId,
            @RequestBody(
                    description = "The data used to purchase product.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BudgetItemRequestDto.class))
            )
            BudgetItemRequestDto budgetItemRequestDto
    );

    @Operation(
            summary = "Update planned amount of a budget item.",
            description =
            """
            If a solution with the same ID is already added to the budget and is not processed,
            its planned amount field will be updated.
            Only items that are not processed (including pending services) can be updated.
            The updated price cannot be lower than the net price of the product or service.
            Requires authentication and the `ORGANIZER` authority.
            Only users with the `ORGANIZER` role can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
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
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Already processed",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "BudgetConflictExample",
                                            summary = "Solution is already precessed",
                                            value = "{ \"error\": \"Conflict\", \"message\": \"Solution is already precessed\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Insufficient funds",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(value =
                                        """
                                        {
                                          "error": "Unprocessable Entity",
                                          "message": "You do not have enough funds for this purchase/reservation!"
                                        }
                                        """
                                    )
                            )
                    ),
            }
    )
    ResponseEntity<BudgetItemResponseDto> updateBudgetItemPlannedAmount(
            @Parameter(
                    description = "The unique identifier of the event.",
                    required = true,
                    example = "123"
            )
            Long eventId,
            @Parameter(
                    description = "The unique identifier of the item id.",
                    required = true,
                    example = "123"
            )
            Long itemId,
            @RequestBody(
                    description = "The data used to update the budget item.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateBudgetItemRequestDto.class))
            )
            UpdateBudgetItemRequestDto request
    );

    @Operation(
            summary = "Delete a budget item from a budget.",
            description =
            """
            Removes a planned budget item from the budget of a specific event, only if its status is `PLANNED`.
            Items with any other status cannot be deleted.
            Requires authentication and the `ORGANIZER` authority.
            Only users with the `ORGANIZER` role can access this endpoint.
            """,
            security = { @SecurityRequirement(name="bearerAuth") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/UnauthorizedResponse"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/ForbiddenResponse"),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Already processed",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @ExampleObject(
                                            name = "BudgetConflictExample",
                                            summary = "Solution is already precessed",
                                            value = "{ \"error\": \"Conflict\", \"message\": \"Solution is already precessed\" }"
                                    )
                            )
                    ),
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
    ResponseEntity<Void> deleteBudgetItem(
            @Parameter(
                    description = "The unique identifier of the item id.",
                    required = true,
                    example = "123"
            )
            Long eventId,
            @Parameter(
                    description = "The unique identifier of the item id.",
                    required = true,
                    example = "123"
            )
            Long itemId
    );
}
