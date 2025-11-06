package com.example.RecordService.model.dto;

public class RatingRequest {
    private String itemId;
    private String itemType; // "THEME", "INVENTORY", or "PLATE"
    private String businessId;
    private Integer rating; // 1-5 stars
    private String comment;
    private String orderId; // Optional - link to the order that triggered this rating

    // Default constructor
    public RatingRequest() {}

    // Constructor with required fields
    public RatingRequest(String itemId, String itemType, String businessId, Integer rating, String comment, String orderId) {
        this.itemId = itemId;
        this.itemType = itemType;
        this.businessId = businessId;
        this.rating = rating;
        this.comment = comment;
        this.orderId = orderId;
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

    @Override
    public String toString() {
        return "RatingRequest{" +
                "itemId='" + itemId + '\'' +
                ", itemType='" + itemType + '\'' +
                ", businessId='" + businessId + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}
