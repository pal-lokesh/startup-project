package com.example.RecordService.entity;

import java.time.LocalDateTime;

public class Plate {
    private String plateId;
    private String businessId;
    private String dishName;
    private String dishDescription;
    private String plateImage;
    private Double price;
    private String dishType;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Plate() {
        this.dishType = "veg"; // Default to vegetarian
    }

    public Plate(String plateId, String businessId, String dishName, String dishDescription, 
                 String plateImage, Double price, String dishType, Boolean isActive, 
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.plateId = plateId;
        this.businessId = businessId;
        this.dishName = dishName;
        this.dishDescription = dishDescription;
        this.plateImage = plateImage;
        this.price = price;
        this.dishType = dishType;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getPlateId() {
        return plateId;
    }

    public void setPlateId(String plateId) {
        this.plateId = plateId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishDescription() {
        return dishDescription;
    }

    public void setDishDescription(String dishDescription) {
        this.dishDescription = dishDescription;
    }

    public String getPlateImage() {
        return plateImage;
    }

    public void setPlateImage(String plateImage) {
        this.plateImage = plateImage;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDishType() {
        return dishType;
    }

    public void setDishType(String dishType) {
        this.dishType = dishType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    @Override
    public String toString() {
        return "Plate{" +
                "plateId='" + plateId + '\'' +
                ", businessId='" + businessId + '\'' +
                ", dishName='" + dishName + '\'' +
                ", dishDescription='" + dishDescription + '\'' +
                ", plateImage='" + plateImage + '\'' +
                ", price=" + price +
                ", dishType='" + dishType + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
