package com.example.RecordService.controller;

import com.example.RecordService.model.dto.AvailabilityRequest;
import com.example.RecordService.model.dto.AvailabilityResponse;
import com.example.RecordService.model.dto.CheckAvailabilityRequest;
import com.example.RecordService.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = "*")
public class AvailabilityController {
    
    @Autowired
    private AvailabilityService availabilityService;
    
    /**
     * Create or update availability for an item
     */
    @PostMapping
    public ResponseEntity<?> createOrUpdateAvailability(@RequestBody AvailabilityRequest request) {
        try {
            AvailabilityResponse response = availabilityService.createOrUpdateAvailability(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create/update availability: " + e.getMessage()));
        }
    }
    
    /**
     * Get availability for an item on a specific date
     */
    @GetMapping("/item/{itemId}/type/{itemType}/date/{date}")
    public ResponseEntity<?> getAvailability(
            @PathVariable String itemId,
            @PathVariable String itemType,
            @PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            Optional<AvailabilityResponse> availability = availabilityService.getAvailability(itemId, itemType, localDate);
            if (availability.isPresent()) {
                return ResponseEntity.ok(availability.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get availability: " + e.getMessage()));
        }
    }
    
    /**
     * Get all availabilities for an item
     */
    @GetMapping("/item/{itemId}/type/{itemType}")
    public ResponseEntity<?> getAvailabilitiesForItem(
            @PathVariable String itemId,
            @PathVariable String itemType) {
        try {
            List<AvailabilityResponse> availabilities = availabilityService.getAvailabilitiesForItem(itemId, itemType);
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get availabilities: " + e.getMessage()));
        }
    }
    
    /**
     * Get availabilities for an item within a date range
     */
    @GetMapping("/item/{itemId}/type/{itemType}/range")
    public ResponseEntity<?> getAvailabilitiesInRange(
            @PathVariable String itemId,
            @PathVariable String itemType,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<AvailabilityResponse> availabilities = availabilityService.getAvailabilitiesForItemInRange(
                    itemId, itemType, start, end);
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get availabilities: " + e.getMessage()));
        }
    }
    
    /**
     * Get all availabilities for a business
     */
    @GetMapping("/business/{businessId}")
    public ResponseEntity<?> getAvailabilitiesForBusiness(@PathVariable String businessId) {
        try {
            List<AvailabilityResponse> availabilities = availabilityService.getAvailabilitiesForBusiness(businessId);
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get availabilities: " + e.getMessage()));
        }
    }
    
    /**
     * Check if an item is available on a specific date
     */
    @PostMapping("/check")
    public ResponseEntity<?> checkAvailability(@RequestBody CheckAvailabilityRequest request) {
        try {
            boolean isAvailable = availabilityService.checkAvailability(request);
            return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check availability: " + e.getMessage()));
        }
    }
    
    /**
     * Get available quantity for an item on a specific date
     */
    @GetMapping("/item/{itemId}/type/{itemType}/date/{date}/quantity")
    public ResponseEntity<?> getAvailableQuantity(
            @PathVariable String itemId,
            @PathVariable String itemType,
            @PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            Integer quantity = availabilityService.getAvailableQuantity(itemId, itemType, localDate);
            return ResponseEntity.ok(Map.of("availableQuantity", quantity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get available quantity: " + e.getMessage()));
        }
    }
    
    /**
     * Delete availability for an item on a specific date
     */
    @DeleteMapping("/item/{itemId}/type/{itemType}/date/{date}")
    public ResponseEntity<?> deleteAvailability(
            @PathVariable String itemId,
            @PathVariable String itemType,
            @PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            availabilityService.deleteAvailability(itemId, itemType, localDate);
            return ResponseEntity.ok(Map.of("message", "Availability deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete availability: " + e.getMessage()));
        }
    }
    
    /**
     * Delete all availabilities for an item
     */
    @DeleteMapping("/item/{itemId}/type/{itemType}")
    public ResponseEntity<?> deleteAllAvailabilitiesForItem(
            @PathVariable String itemId,
            @PathVariable String itemType) {
        try {
            availabilityService.deleteAllAvailabilitiesForItem(itemId, itemType);
            return ResponseEntity.ok(Map.of("message", "All availabilities deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete availabilities: " + e.getMessage()));
        }
    }
}

