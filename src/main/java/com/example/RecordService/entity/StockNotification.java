package com.example.RecordService.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_notifications")
public class StockNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    
    @Column(name = "user_id", nullable = false)
    private String userId; // Client's phone number
    
    @Column(name = "item_id", nullable = false)
    private String itemId; // Theme ID, Inventory ID, or Plate ID
    
    @Column(name = "item_type", nullable = false)
    private String itemType; // "THEME", "INVENTORY", or "PLATE"
    
    @Column(name = "item_name", nullable = false)
    private String itemName;
    
    @Column(name = "business_id", nullable = false)
    private String businessId;
    
    @Column(name = "requested_date")
    private String requestedDate; // The specific date the user wants to be notified about (nullable for general stock notifications)
    
    @Column(name = "notified", nullable = false)
    private boolean notified = false; // Whether user has been notified
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;
    
    // Constructors
    public StockNotification() {
        this.createdAt = LocalDateTime.now();
    }
    
    public StockNotification(String userId, String itemId, String itemType, 
                           String itemName, String businessId) {
        this();
        this.userId = userId;
        this.itemId = itemId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.businessId = businessId;
    }
    
    public StockNotification(String userId, String itemId, String itemType, 
                           String itemName, String businessId, String requestedDate) {
        this();
        this.userId = userId;
        this.itemId = itemId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.businessId = businessId;
        this.requestedDate = requestedDate;
    }
    
    // Getters and Setters
    public Long getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
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
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getBusinessId() {
        return businessId;
    }
    
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
    
    public boolean isNotified() {
        return notified;
    }
    
    public void setNotified(boolean notified) {
        this.notified = notified;
        if (notified && this.notifiedAt == null) {
            this.notifiedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getNotifiedAt() {
        return notifiedAt;
    }
    
    public void setNotifiedAt(LocalDateTime notifiedAt) {
        this.notifiedAt = notifiedAt;
    }
    
    public String getRequestedDate() {
        return requestedDate;
    }
    
    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }
}

