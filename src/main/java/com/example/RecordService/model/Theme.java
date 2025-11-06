package com.example.RecordService.model;

import java.time.LocalDateTime;

public class Theme {
    private String themeId; // Primary key
    private String businessId; // Foreign key to Business
    private String themeName;
    private String themeDescription;
    private String themeCategory;
    private String priceRange;
    private int quantity; // Stock quantity
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Reference to Images (One-to-Many) - will be handled by service layer
    // private List<Image> images;

    // Default constructor
    public Theme() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Constructor with parameters
    public Theme(String businessId, String themeName, String themeDescription, 
                 String themeCategory, String priceRange) {
        this.businessId = businessId;
        this.themeName = themeName;
        this.themeDescription = themeDescription;
        this.themeCategory = themeCategory;
        this.priceRange = priceRange;
        this.quantity = 0; // Default quantity
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and Setters
    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThemeDescription() {
        return themeDescription;
    }

    public void setThemeDescription(String themeDescription) {
        this.themeDescription = themeDescription;
    }

    public String getThemeCategory() {
        return themeCategory;
    }

    public void setThemeCategory(String themeCategory) {
        this.themeCategory = themeCategory;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    // Images relationship handled by service layer
    // public List<Image> getImages() {
    //     return images;
    // }
    //
    // public void setImages(List<Image> images) {
    //     this.images = images;
    // }

    @Override
    public String toString() {
        return "Theme{" +
                "themeId='" + themeId + '\'' +
                ", businessId='" + businessId + '\'' +
                ", themeName='" + themeName + '\'' +
                ", themeDescription='" + themeDescription + '\'' +
                ", themeCategory='" + themeCategory + '\'' +
                ", priceRange='" + priceRange + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
