package com.example.RecordService.model.dto;

import java.time.LocalDateTime;

public class RatingResponse {
    private String ratingId;
    private String clientPhone;
    private String itemId;
    private String itemType;
    private String businessId;
    private Integer rating;
    private String comment;
    private String orderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;

    // Default constructor
    public RatingResponse() {}

    // Constructor with all fields
    public RatingResponse(String ratingId, String clientPhone, String itemId, String itemType, 
                         String businessId, Integer rating, String comment, String orderId,
                         LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isActive) {
        this.ratingId = ratingId;
        this.clientPhone = clientPhone;
        this.itemId = itemId;
        this.itemType = itemType;
        this.businessId = businessId;
        this.rating = rating;
        this.comment = comment;
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "RatingResponse{" +
                "ratingId='" + ratingId + '\'' +
                ", clientPhone='" + clientPhone + '\'' +
                ", itemId='" + itemId + '\'' +
                ", itemType='" + itemType + '\'' +
                ", businessId='" + businessId + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", orderId='" + orderId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isActive=" + isActive +
                '}';
    }
}
