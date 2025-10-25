package com.example.RecordService.model.dto;

import java.util.List;

public class OrderRequest {
    
    private String userId;
    private List<OrderItemRequest> items;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String deliveryAddress;
    private String deliveryDate;
    private String specialNotes;
    
    // Constructors
    public OrderRequest() {}
    
    public OrderRequest(String userId, List<OrderItemRequest> items, String customerName, 
                       String customerEmail, String customerPhone, String deliveryAddress, 
                       String deliveryDate, String specialNotes) {
        this.userId = userId;
        this.items = items;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.deliveryAddress = deliveryAddress;
        this.deliveryDate = deliveryDate;
        this.specialNotes = specialNotes;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public List<OrderItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public String getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public String getSpecialNotes() {
        return specialNotes;
    }
    
    public void setSpecialNotes(String specialNotes) {
        this.specialNotes = specialNotes;
    }
}
