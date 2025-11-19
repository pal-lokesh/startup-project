package com.example.RecordService.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "availabilities", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"item_id", "item_type", "availability_date"})
})
public class Availability {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long availabilityId;
    
    @Column(name = "item_id", nullable = false)
    private String itemId; // themeId, inventoryId, or plateId
    
    @Column(name = "item_type", nullable = false)
    private String itemType; // "theme", "inventory", "plate"
    
    @Column(name = "business_id", nullable = false)
    private String businessId;
    
    @Column(name = "availability_date", nullable = false)
    private LocalDate availabilityDate;
    
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity; // How many items available on this date
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable; // Whether the item is available on this date
    
    @Column(name = "price_override")
    private Double priceOverride; // Optional price override for specific dates
    
    // Constructors
    public Availability() {
        this.isAvailable = true;
        this.availableQuantity = 0;
    }
    
    public Availability(String itemId, String itemType, String businessId, 
                       LocalDate availabilityDate, Integer availableQuantity) {
        this();
        this.itemId = itemId;
        this.itemType = itemType;
        this.businessId = businessId;
        this.availabilityDate = availabilityDate;
        this.availableQuantity = availableQuantity;
    }
    
    // Getters and Setters
    public Long getAvailabilityId() {
        return availabilityId;
    }
    
    public void setAvailabilityId(Long availabilityId) {
        this.availabilityId = availabilityId;
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

