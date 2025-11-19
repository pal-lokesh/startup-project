package com.example.RecordService.service;

import com.example.RecordService.entity.Rating;
import com.example.RecordService.model.dto.RatingRequest;
import com.example.RecordService.model.dto.RatingResponse;
import com.example.RecordService.model.dto.RatingStatsResponse;
import com.example.RecordService.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;
    
    @Autowired
    private OrderService orderService;

    // Create a new rating
    public RatingResponse createRating(String clientPhone, RatingRequest request) {
        // Check if client has a DELIVERED order for this item (required for rating)
        boolean hasPurchased = orderService.hasClientPurchasedItem(clientPhone, request.getItemId(), request.getItemType());
        if (!hasPurchased) {
            throw new IllegalArgumentException("You can only rate items after your order has been successfully delivered. Please wait for your order to be delivered before rating.");
        }
        
        // Check if client has already rated this item
        Optional<Rating> existingRating = ratingRepository.findByClientPhoneAndItemIdAndItemType(
            clientPhone, request.getItemId(), request.getItemType()
        );

        if (existingRating.isPresent()) {
            throw new IllegalArgumentException("You have already rated this item. You can update your existing rating instead.");
        }

        // Validate rating value
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars.");
        }

        // Create new rating
        Rating rating = new Rating();
        rating.setRatingId(UUID.randomUUID().toString());
        rating.setClientPhone(clientPhone);
        rating.setItemId(request.getItemId());
        rating.setItemType(request.getItemType());
        rating.setBusinessId(request.getBusinessId());
        rating.setRating(request.getRating());
        rating.setComment(request.getComment());
        rating.setOrderId(request.getOrderId());
        rating.setCreatedAt(LocalDateTime.now());
        rating.setUpdatedAt(LocalDateTime.now());
        rating.setIsActive(true);

        Rating savedRating = ratingRepository.save(rating);
        return convertToResponse(savedRating);
    }

    // Update an existing rating
    public RatingResponse updateRating(String clientPhone, String ratingId, RatingRequest request) {
        Optional<Rating> existingRating = ratingRepository.findById(ratingId);
        
        if (existingRating.isEmpty()) {
            throw new IllegalArgumentException("Rating not found.");
        }

        Rating rating = existingRating.get();
        
        // Check if the client owns this rating
        if (!rating.getClientPhone().equals(clientPhone)) {
            throw new IllegalArgumentException("You can only update your own ratings.");
        }

        // Validate rating value
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars.");
        }

        // Update rating
        rating.setRating(request.getRating());
        rating.setComment(request.getComment());
        rating.setUpdatedAt(LocalDateTime.now());

        Rating updatedRating = ratingRepository.update(rating);
        return convertToResponse(updatedRating);
    }

    // Delete a rating (soft delete)
    public void deleteRating(String clientPhone, String ratingId) {
        Optional<Rating> existingRating = ratingRepository.findById(ratingId);
        
        if (existingRating.isEmpty()) {
            throw new IllegalArgumentException("Rating not found.");
        }

        Rating rating = existingRating.get();
        
        // Check if the client owns this rating
        if (!rating.getClientPhone().equals(clientPhone)) {
            throw new IllegalArgumentException("You can only delete your own ratings.");
        }

        ratingRepository.deleteById(ratingId);
    }

    // Get all ratings for a specific item
    public List<RatingResponse> getRatingsByItem(String itemId, String itemType) {
        List<Rating> ratings = ratingRepository.findByItemIdAndItemType(itemId, itemType);
        return ratings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get all ratings for a business
    public List<RatingResponse> getRatingsByBusiness(String businessId) {
        List<Rating> ratings = ratingRepository.findByBusinessId(businessId);
        return ratings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get all ratings by a client
    public List<RatingResponse> getRatingsByClient(String clientPhone) {
        List<Rating> ratings = ratingRepository.findByClientPhone(clientPhone);
        return ratings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get ratings for a specific order
    public List<RatingResponse> getRatingsByOrder(String orderId) {
        List<Rating> ratings = ratingRepository.findByOrderId(orderId);
        return ratings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get rating statistics for an item
    public RatingStatsResponse getRatingStats(String itemId, String itemType) {
        double averageRating = ratingRepository.getAverageRatingByItemIdAndItemType(itemId, itemType);
        long totalRatings = ratingRepository.countByItemIdAndItemType(itemId, itemType);
        List<Rating> recentRatings = ratingRepository.findByItemIdAndItemType(itemId, itemType);
        
        // Get the first 5 most recent ratings
        List<RatingResponse> recentRatingResponses = recentRatings.stream()
                .limit(5)
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new RatingStatsResponse(
            itemId,
            itemType,
            recentRatings.isEmpty() ? null : recentRatings.get(0).getBusinessId(),
            averageRating,
            totalRatings,
            recentRatingResponses
        );
    }

    // Get rating statistics for a business
    public RatingStatsResponse getBusinessRatingStats(String businessId) {
        double averageRating = ratingRepository.getAverageRatingByBusinessId(businessId);
        long totalRatings = ratingRepository.countByBusinessId(businessId);
        List<Rating> recentRatings = ratingRepository.findByBusinessId(businessId);
        
        // Get the first 5 most recent ratings
        List<RatingResponse> recentRatingResponses = recentRatings.stream()
                .limit(5)
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new RatingStatsResponse(
            null,
            null,
            businessId,
            averageRating,
            totalRatings,
            recentRatingResponses
        );
    }

    // Check if client has rated an item
    public boolean hasClientRatedItem(String clientPhone, String itemId, String itemType) {
        return ratingRepository.findByClientPhoneAndItemIdAndItemType(clientPhone, itemId, itemType).isPresent();
    }

    // Get client's rating for an item
    public Optional<RatingResponse> getClientRatingForItem(String clientPhone, String itemId, String itemType) {
        Optional<Rating> rating = ratingRepository.findByClientPhoneAndItemIdAndItemType(clientPhone, itemId, itemType);
        return rating.map(this::convertToResponse);
    }

    // Convert Rating entity to RatingResponse DTO
    private RatingResponse convertToResponse(Rating rating) {
        return new RatingResponse(
            rating.getRatingId(),
            rating.getClientPhone(),
            rating.getItemId(),
            rating.getItemType(),
            rating.getBusinessId(),
            rating.getRating(),
            rating.getComment(),
            rating.getOrderId(),
            rating.getCreatedAt(),
            rating.getUpdatedAt(),
            rating.getIsActive()
        );
    }
}
