package com.example.RecordService.model.dto;

import java.util.List;

public class RatingStatsResponse {
    private String itemId;
    private String itemType;
    private String businessId;
    private double averageRating;
    private long totalRatings;
    private List<RatingResponse> recentRatings;

    // Default constructor
    public RatingStatsResponse() {}

    // Constructor with required fields
    public RatingStatsResponse(String itemId, String itemType, String businessId, 
                              double averageRating, long totalRatings, List<RatingResponse> recentRatings) {
        this.itemId = itemId;
        this.itemType = itemType;
        this.businessId = businessId;
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
        this.recentRatings = recentRatings;
    }

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public long getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(long totalRatings) {
        this.totalRatings = totalRatings;
    }

    public List<RatingResponse> getRecentRatings() {
        return recentRatings;
    }

    public void setRecentRatings(List<RatingResponse> recentRatings) {
        this.recentRatings = recentRatings;
    }

    @Override
    public String toString() {
        return "RatingStatsResponse{" +
                "itemId='" + itemId + '\'' +
                ", itemType='" + itemType + '\'' +
                ", businessId='" + businessId + '\'' +
                ", averageRating=" + averageRating +
                ", totalRatings=" + totalRatings +
                ", recentRatings=" + recentRatings +
                '}';
    }
}
