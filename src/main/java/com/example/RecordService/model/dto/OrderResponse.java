package com.example.RecordService.model.dto;

import com.example.RecordService.entity.Order;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    
    private Long orderId;
    private String userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String deliveryAddress;
    private String deliveryDate;
    private String specialNotes;
    private Double totalAmount;
    private Order.OrderStatus status;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> orderItems;
    
    // Constructors
    public OrderResponse() {}
    
    public OrderResponse(Order order) {
        this.orderId = order.getOrderId();
        this.userId = order.getUserId();
        this.customerName = order.getCustomerName();
        this.customerEmail = order.getCustomerEmail();
        this.customerPhone = order.getCustomerPhone();
        this.deliveryAddress = order.getDeliveryAddress();
        this.deliveryDate = order.getDeliveryDate();
        this.specialNotes = order.getSpecialNotes();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus();
        this.orderDate = order.getOrderDate();
    }
    
    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
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
    
    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Order.OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public List<OrderItemResponse> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItemResponse> orderItems) {
        this.orderItems = orderItems;
    }
}
