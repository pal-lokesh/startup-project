package com.example.RecordService.model.dto;

import com.example.RecordService.entity.OrderItem;
import java.time.LocalDate;

public class OrderItemResponse {
    
    private Long orderItemId;
    private String itemId;
    private String itemName;
    private Double itemPrice;
    private Integer quantity;
    private String itemType;
    private String businessId;
    private String businessName;
    private String imageUrl;
    private Double totalPrice;
    private LocalDate bookingDate;
    private String selectedDishes; // JSON string storing selected dishes for plates
    
    // Constructors
    public OrderItemResponse() {}
    
    public OrderItemResponse(OrderItem orderItem) {
        this.orderItemId = orderItem.getOrderItemId();
        this.itemId = orderItem.getItemId();
        this.itemName = orderItem.getItemName();
        this.itemPrice = orderItem.getItemPrice();
        this.quantity = orderItem.getQuantity();
        this.itemType = orderItem.getItemType();
        this.businessId = orderItem.getBusinessId();
        this.businessName = orderItem.getBusinessName();
        this.imageUrl = orderItem.getImageUrl();
        this.totalPrice = orderItem.getTotalPrice();
        this.bookingDate = orderItem.getBookingDate();
        this.selectedDishes = orderItem.getSelectedDishes();
    }
    
    // Getters and Setters
    public Long getOrderItemId() {
        return orderItemId;
    }
    
    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }
    
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
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public LocalDate getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    public String getSelectedDishes() {
        return selectedDishes;
    }
    
    public void setSelectedDishes(String selectedDishes) {
        this.selectedDishes = selectedDishes;
    }
}
