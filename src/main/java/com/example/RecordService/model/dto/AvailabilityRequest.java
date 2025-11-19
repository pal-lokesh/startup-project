package com.example.RecordService.model.dto;

import java.time.LocalDate;

public class AvailabilityRequest {
    private String itemId;
    private String itemType; // "theme", "inventory", "plate"
    private String businessId;
    private LocalDate availabilityDate;
    private Integer availableQuantity;
    private Boolean isAvailable;
    private Double priceOverride;

    // Constructors
    public AvailabilityRequest() {}

    public AvailabilityRequest(String itemId, String itemType, String businessId, 
                               LocalDate availabilityDate, Integer availableQuantity) {
        this.itemId = itemId;
        this.itemType = itemType;
        this.businessId = businessId;
        this.availabilityDate = availabilityDate;
        this.availableQuantity = availableQuantity;
        this.isAvailable = true;
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

    public LocalDate getAvailabilityDate() {
        return availabilityDate;
    }

    public void setAvailabilityDate(LocalDate availabilityDate) {
        this.availabilityDate = availabilityDate;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Double getPriceOverride() {
        return priceOverride;
    }

    public void setPriceOverride(Double priceOverride) {
        this.priceOverride = priceOverride;
    }
}

