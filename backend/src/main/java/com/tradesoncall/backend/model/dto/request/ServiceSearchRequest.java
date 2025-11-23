package com.tradesoncall.backend.model.dto.request;

import com.tradesoncall.backend.model.enums.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Service search request")
public class ServiceSearchRequest {

    @NotNull(message = "Service type is required")
    @Schema(description = "Type of service to search for", example = "PLUMBER", required = true)
    private ServiceType serviceType;

    @NotBlank(message = "Location is required")
    @Schema(description = "Location to search in (address, city, or zip code)",
            example = "New York, NY", required = true)
    private String location;

    @Min(value = 1, message = "Radius must be at least 1 mile")
    @Max(value = 50, message = "Radius cannot exceed 50 miles")
    @Schema(description = "Search radius in miles", example = "10", defaultValue = "10")
    private Integer radiusMiles = 10;

    @Min(value = 1, message = "Must request at least 1 result")
    @Max(value = 20, message = "Cannot request more than 20 results")
    @Schema(description = "Maximum number of results", example = "10", defaultValue = "10")
    private Integer maxResults = 10;

    @Schema(description = "Minimum rating (1-5)", example = "3.5")
    private Double minRating;

    @Schema(description = "Only show currently open businesses", example = "false")
    private Boolean openNow = false;
}