package com.example.demo.controller;

import com.example.demo.dto.AMapIpGeolocationResponse;
import com.example.demo.dto.AMapReverseGeocodeResponse;
import com.example.demo.dto.GeocodeResponse;
import com.example.demo.service.AMapGeocodeService;
import com.example.demo.service.AMapIpGeolocationService;
import com.example.demo.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/geocode")
@RequiredArgsConstructor
public class GeocodeController {

    private final AMapGeocodeService geocodeService;
    private final AMapIpGeolocationService aMapIpGeolocationService;

    @GetMapping("/reverse")
    public ResponseEntity<?> reverseGeocode(
            @RequestParam double longitude,
            @RequestParam double latitude) {

        validateCoordinates(longitude, latitude);

        try {
            AMapReverseGeocodeResponse response = geocodeService.reverseGeocode(longitude, latitude).block();

            if (response == null) {
                log.error("Geocoding service returned null response");
                return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE,
                        "Geocoding service unavailable");
            }

            if (!"1".equals(response.getStatus())) {
                log.warn("Geocoding service returned error status: {}", response.getInfo());
                return createErrorResponse(HttpStatus.BAD_GATEWAY,
                        "Geocoding service error: " + response.getInfo());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Geocoding request failed", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error");
        }
    }

    public ResponseEntity<Map<String, String>> createErrorResponse(HttpStatus status, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @GetMapping("/address")
    public ResponseEntity<?> getAddress(
            @RequestParam double longitude,
            @RequestParam double latitude) {

        validateCoordinates(longitude, latitude);

        try {
            GeocodeResponse address = geocodeService.getFormattedAddress(longitude, latitude);
            if (address == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("Address not found"));
            }
            return ResponseEntity.ok(createSuccessResponse("address", address));
        } catch (Exception e) {
            logError(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error"));
        }
    }

    private void validateCoordinates(double longitude, double latitude) {
        if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid coordinates: longitude must be between -180 and 180, latitude must be between -90 and 90");
        }
    }

    private Map<String, Object> createSuccessResponse(String key, Object value) {
        return Collections.singletonMap(key, value);
    }

    private Map<String, String> createErrorResponse(String message) {
        return Collections.singletonMap("error", message);
    }

    private void logError(Exception e) {
        // 这里可以集成日志框架，例如 SLF4J
        System.err.println("Error occurred: " + e.getMessage());
    }

    @GetMapping("/ip")
    public ResponseEntity<?> getLocationByIp(@RequestParam String ip) {

        String response = aMapIpGeolocationService.getCityByIp(ip);

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("IP not found");
        }


    }

    /**
     * 获取IP地址对应的地理位置
     */
    @GetMapping("/fullLocation")
    public String getFullLocationByIp(String ip) {
        return aMapIpGeolocationService.getFullLocationByIp(ip);
    }
}