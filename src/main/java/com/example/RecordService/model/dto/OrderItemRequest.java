package com.example.RecordService.model.dto;

public class OrderItemRequest {
    
    private String itemId;
    private String itemName;
    private Double itemPrice;
    private Integer quantity;
    private String itemType; // "theme", "inventory", "plate"
    private String businessId;
    private String businessName;
    private String imageUrl;
    
    // Constructors
    public OrderItemRequest() {}
    
    public OrderItemRequest(String itemId, String itemName, Double itemPrice, Integer quantity, 
                           String itemType, String businessId, String businessName, String imageUrl) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        this.itemType = itemType;
        this.businessId = businessId;
        this.businessName = businessName;
        this.imageUrl = imageUrl;
    }
    
    // Getters and Setters
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public Double getItemPrice() {
        return itemPrice;
    }
    
    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
    
    public String getBusinessName() {
        return businessName;
    }
    
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
