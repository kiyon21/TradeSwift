package com.tradesoncall.backend.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Search results with metadata")
public class SearchResultsResponse {

    @Schema(description = "Search location that was used")
    private String location;

    @Schema(description = "Service type searched for")
    private String serviceType;

    @Schema(description = "Total number of results found")
    private Integer totalResults;

    @Schema(description = "List of service providers")
    private List<ServiceSearchResponse> results;

    @Schema(description = "Search center coordinates")
    private LocationCoordinates searchCenter;

    @Data
    @Builder
    public static class LocationCoordinates {
        private Double latitude;
        private Double longitude;
    }
}