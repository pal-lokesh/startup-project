package com.example.RecordService.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "plates")
public class Plate {
    @Id
    @Column(name = "plate_id", length = 100)
    private String plateId;

    @Column(name = "business_id", nullable = false)
    private String businessId;

    @Column(name = "dish_name", nullable = false)
    private String dishName;

    @Column(name = "dish_description", columnDefinition = "TEXT")
    private String dishDescription;

    @Column(name = "plate_image", columnDefinition = "TEXT")
    private String plateImage;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "dish_type")
    private String dishType;

    @Column(name = "quantity")
    private int quantity; // Stock quantity

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Plate() {
        this.dishType = "veg"; // Default to vegetarian
        this.quantity = 0; // Default quantity
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    @PrePersist
    public void prePersist() {
        if (this.plateId == null || this.plateId.trim().isEmpty()) {
            this.plateId = "PLATE_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        }
        if (this.dishType == null || this.dishType.trim().isEmpty()) {
            this.dishType = "veg"; // Default to vegetarian
        }
        if (this.quantity < 0) {
            this.quantity = 0; // Default quantity
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
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
