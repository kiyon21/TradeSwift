package com.tradesoncall.backend.controller;

import com.tradesoncall.backend.model.dto.request.ServiceSearchRequest;
import com.tradesoncall.backend.model.dto.response.ApiResponse;
import com.tradesoncall.backend.model.dto.response.SearchResultsResponse;
import com.tradesoncall.backend.security.JwtTokenProvider;
import com.tradesoncall.backend.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Service Search", description = "Search for service providers by location")
public class SearchController {

    private final SearchService searchService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/services")
    @Operation(
            summary = "Search for service providers",
            description = "Search for tradespeople and service providers near a location",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully",
                    content = @Content(schema = @Schema(implementation = SearchResultsResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid search parameters"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing token"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "503",
                    description = "External service unavailable"
            )
    })
    public ResponseEntity<ApiResponse<SearchResultsResponse>> searchServices(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ServiceSearchRequest request
    ) {
        // Extract user ID from JWT token
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        UUID userId = jwtTokenProvider.getUserIdFromAccessToken(token);

        // Perform search
        SearchResultsResponse results = searchService.searchServices(userId, request);

        ApiResponse<SearchResultsResponse> response = ApiResponse.success(
                "Search completed successfully",
                results
        );

        return ResponseEntity.ok(response);
    }
}