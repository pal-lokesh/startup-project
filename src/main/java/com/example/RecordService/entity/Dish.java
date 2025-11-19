package com.example.RecordService.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dishes")
public class Dish {
    @Id
    @Column(name = "dish_id", length = 100)
    private String dishId;

    @Column(name = "business_id", nullable = false)
    private String businessId;

    @Column(name = "dish_name", nullable = false)
    private String dishName;

    @Column(name = "dish_description", columnDefinition = "TEXT")
    private String dishDescription;

    @Column(name = "dish_image", columnDefinition = "TEXT")
    private String dishImage;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "dish_availability_dates", joinColumns = @JoinColumn(name = "dish_id"))
    @Column(name = "availability_date")
    private List<String> availabilityDates = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Dish() {
        this.isAvailable = true;
        this.quantity = 0;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
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

    public String getDishImage() {
        return dishImage;
    }

    public void setDishImage(String dishImage) {
        this.dishImage = dishImage;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public List<String> getAvailabilityDates() {
        return availabilityDates;
    }

    public void setAvailabilityDates(List<String> availabilityDates) {
        this.availabilityDates = availabilityDates;
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
        if (this.dishId == null || this.dishId.trim().isEmpty()) {
            this.dishId = "DISH_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        }
        if (this.isAvailable == null) {
            this.isAvailable = true;
        }
        if (this.quantity == null) {
            this.quantity = 0;
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.availabilityDates == null) {
            this.availabilityDates = new ArrayList<>();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

