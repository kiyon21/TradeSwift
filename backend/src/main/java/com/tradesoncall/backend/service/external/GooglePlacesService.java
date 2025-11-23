package com.tradesoncall.backend.service.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradesoncall.backend.exception.ExternalServiceException;
import com.tradesoncall.backend.model.dto.response.ServiceSearchResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GooglePlacesService {

    @Value("${google.places.api-key}")
    private String apiKey;

    @Value("${google.places.base-url}")
    private String baseUrl;

    private final WebClient.Builder webClientBuilder;

    /**
     * Search for service providers near a location
     */
    public List<ServiceSearchResponse> searchNearby(
            String query,
            String location,
            Integer radiusMeters,
            Integer maxResults
    ) {
        try {
            // Step 1: Geocode the location to get coordinates
            GeocodingResponse geocoding = geocodeLocation(location);

            if (geocoding == null || geocoding.getResults().isEmpty()) {
                throw new ExternalServiceException("Could not find location: " + location);
            }

            double lat = geocoding.getResults().get(0).getGeometry().getLocation().getLat();
            double lng = geocoding.getResults().get(0).getGeometry().getLocation().getLng();

            // Step 2: Search for places near those coordinates
            PlacesSearchResponse placesResponse = searchPlaces(query, lat, lng, radiusMeters);

            if (placesResponse == null || placesResponse.getResults() == null) {
                return List.of();
            }

            // Step 3: Convert to our DTO and limit results
            return placesResponse.getResults().stream()
                    .limit(maxResults)
                    .map(place -> convertToServiceResponse(place, lat, lng))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error searching Google Places", e);
            throw new ExternalServiceException("Failed to search for services: " + e.getMessage());
        }
    }

    /**
     * Geocode a location string to coordinates
     */
    private GeocodingResponse geocodeLocation(String location) {
        WebClient webClient = webClientBuilder
                .baseUrl("https://maps.googleapis.com/maps/api")
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geocode/json")
                        .queryParam("address", location)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(GeocodingResponse.class)
                .block();
    }

    /**
     * Search for places using Places API
     */
    private PlacesSearchResponse searchPlaces(
            String query,
            double lat,
            double lng,
            Integer radiusMeters
    ) {
        WebClient webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/nearbysearch/json")
                        .queryParam("location", lat + "," + lng)
                        .queryParam("radius", radiusMeters)
                        .queryParam("keyword", query)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(PlacesSearchResponse.class)
                .block();
    }

    /**
     * Convert Google Place to our DTO
     */
    private ServiceSearchResponse convertToServiceResponse(
            Place place,
            double searchLat,
            double searchLng
    ) {
        // Calculate distance
        double distance = calculateDistance(
                searchLat, searchLng,
                place.getGeometry().getLocation().getLat(),
                place.getGeometry().getLocation().getLng()
        );

        return ServiceSearchResponse.builder()
                .name(place.getName())
                .address(place.getVicinity())
                .rating(place.getRating())
                .totalReviews(place.getUserRatingsTotal())
                .priceLevel(place.getPriceLevel())
                .openNow(place.getOpeningHours() != null && place.getOpeningHours().getOpenNow())
                .distanceMiles(distance)
                .latitude(place.getGeometry().getLocation().getLat())
                .longitude(place.getGeometry().getLocation().getLng())
                .placeId(place.getPlaceId())
                .googleMapsUrl("https://www.google.com/maps/place/?q=place_id:" + place.getPlaceId())
                .serviceTypes(place.getTypes())
                .build();
    }

    /**
     * Calculate distance between two coordinates (Haversine formula)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 3959; // Radius of the earth in miles

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in miles
    }

    // ===== Google Places API Response DTOs =====

    @Data
    private static class GeocodingResponse {
        private List<GeocodingResult> results;
    }

    @Data
    private static class GeocodingResult {
        private Geometry geometry;
    }

    @Data
    private static class PlacesSearchResponse {
        private List<Place> results;
        private String status;
    }

    @Data
    private static class Place {
        private String name;
        @JsonProperty("place_id")
        private String placeId;
        private String vicinity;
        private Double rating;
        @JsonProperty("user_ratings_total")
        private Integer userRatingsTotal;
        @JsonProperty("price_level")
        private Integer priceLevel;
        @JsonProperty("opening_hours")
        private OpeningHours openingHours;
        private Geometry geometry;
        private List<String> types;
    }

    @Data
    private static class Geometry {
        private Location location;
    }

    @Data
    private static class Location {
        private Double lat;
        private Double lng;
    }

    @Data
    private static class OpeningHours {
        @JsonProperty("open_now")
        private Boolean openNow;
    }
}