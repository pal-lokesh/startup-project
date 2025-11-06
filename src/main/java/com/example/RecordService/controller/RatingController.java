package com.example.RecordService.controller;

import com.example.RecordService.model.dto.RatingRequest;
import com.example.RecordService.model.dto.RatingResponse;
import com.example.RecordService.model.dto.RatingStatsResponse;
import com.example.RecordService.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    // Create a new rating
    @PostMapping
    public ResponseEntity<?> createRating(
            @RequestHeader("Authorization") String token,
            @RequestBody RatingRequest request) {
        try {
            String clientPhone = extractPhoneFromToken(token);
            RatingResponse response = ratingService.createRating(clientPhone, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create rating: " + e.getMessage()));
        }
    }

    // Update an existing rating
    @PutMapping("/{ratingId}")
    public ResponseEntity<?> updateRating(
            @RequestHeader("Authorization") String token,
            @PathVariable String ratingId,
            @RequestBody RatingRequest request) {
        try {
            String clientPhone = extractPhoneFromToken(token);
            RatingResponse response = ratingService.updateRating(clientPhone, ratingId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update rating: " + e.getMessage()));
        }
    }

    // Delete a rating
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<?> deleteRating(
            @RequestHeader("Authorization") String token,
            @PathVariable String ratingId) {
        try {
            String clientPhone = extractPhoneFromToken(token);
            ratingService.deleteRating(clientPhone, ratingId);
            return ResponseEntity.ok(Map.of("message", "Rating deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete rating: " + e.getMessage()));
        }
    }

    // Get all ratings for a specific item
    @GetMapping("/item/{itemId}/{itemType}")
    public ResponseEntity<?> getRatingsByItem(
            @PathVariable String itemId,
            @PathVariable String itemType) {
        try {
            List<RatingResponse> ratings = ratingService.getRatingsByItem(itemId, itemType);
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get ratings: " + e.getMessage()));
        }
    }

    // Get all ratings for a business
    @GetMapping("/business/{businessId}")
    public ResponseEntity<?> getRatingsByBusiness(@PathVariable String businessId) {
        try {
            List<RatingResponse> ratings = ratingService.getRatingsByBusiness(businessId);
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get business ratings: " + e.getMessage()));
        }
    }

    // Get all ratings by a client
    @GetMapping("/client")
    public ResponseEntity<?> getRatingsByClient(@RequestHeader("Authorization") String token) {
        try {
            String clientPhone = extractPhoneFromToken(token);
            List<RatingResponse> ratings = ratingService.getRatingsByClient(clientPhone);
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get client ratings: " + e.getMessage()));
        }
    }

    // Get ratings for a specific order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getRatingsByOrder(@PathVariable String orderId) {
        try {
            List<RatingResponse> ratings = ratingService.getRatingsByOrder(orderId);
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get order ratings: " + e.getMessage()));
        }
    }

    // Get rating statistics for an item
    @GetMapping("/stats/item/{itemId}/{itemType}")
    public ResponseEntity<?> getRatingStats(
            @PathVariable String itemId,
            @PathVariable String itemType) {
        try {
            RatingStatsResponse stats = ratingService.getRatingStats(itemId, itemType);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get rating stats: " + e.getMessage()));
        }
    }

    // Get rating statistics for a business
    @GetMapping("/stats/business/{businessId}")
    public ResponseEntity<?> getBusinessRatingStats(@PathVariable String businessId) {
        try {
            RatingStatsResponse stats = ratingService.getBusinessRatingStats(businessId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get business rating stats: " + e.getMessage()));
        }
    }

    // Check if client has rated an item
    @GetMapping("/check/{itemId}/{itemType}")
    public ResponseEntity<?> hasClientRatedItem(
            @RequestHeader("Authorization") String token,
            @PathVariable String itemId,
            @PathVariable String itemType) {
        try {
            String clientPhone = extractPhoneFromToken(token);
            boolean hasRated = ratingService.hasClientRatedItem(clientPhone, itemId, itemType);
            return ResponseEntity.ok(Map.of("hasRated", hasRated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check rating status: " + e.getMessage()));
        }
    }

    // Get client's rating for an item
    @GetMapping("/client/{itemId}/{itemType}")
    public ResponseEntity<?> getClientRatingForItem(
            @RequestHeader("Authorization") String token,
            @PathVariable String itemId,
            @PathVariable String itemType) {
        try {
            String clientPhone = extractPhoneFromToken(token);
            Optional<RatingResponse> rating = ratingService.getClientRatingForItem(clientPhone, itemId, itemType);
            return ResponseEntity.ok(rating.orElse(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get client rating: " + e.getMessage()));
        }
    }

    // Helper method to extract phone number from JWT token
    private String extractPhoneFromToken(String token) {
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // For now, we'll use a simple approach
        // In a real application, you would decode the JWT token and extract the phone number
        // This is a placeholder implementation
        try {
            // You would typically use JWT library to decode the token
            // For now, we'll assume the token contains the phone number
            // This should be replaced with proper JWT decoding
            return "2222222222"; // Placeholder - replace with actual JWT decoding
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token");
        }
    }
}
