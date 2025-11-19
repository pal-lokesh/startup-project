package com.example.RecordService.model.dto;

import com.example.RecordService.entity.Notification;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class NotificationResponse {
    
    private Long notificationId;
    private String vendorPhone;
    private String businessId;
    private String businessName;
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Double orderTotal;
    private String deliveryDate;
    private String deliveryAddress;
    private String notificationType;
    private String status;
    private boolean isRead;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    
    // Constructors
    public NotificationResponse() {}
    
    public NotificationResponse(Notification notification) {
        this.notificationId = notification.getNotificationId();
        this.vendorPhone = notification.getVendorPhone();
        this.businessId = notification.getBusinessId();
        this.businessName = notification.getBusinessName();
        this.orderId = notification.getOrderId();
        this.customerName = notification.getCustomerName();
        this.customerEmail = notification.getCustomerEmail();
        this.customerPhone = notification.getCustomerPhone();
        this.orderTotal = notification.getOrderTotal();
        this.deliveryDate = notification.getDeliveryDate();
        this.deliveryAddress = notification.getDeliveryAddress();
        this.notificationType = notification.getNotificationType().toString();
        this.status = notification.getStatus().toString();
        this.isRead = notification.getStatus() == Notification.NotificationStatus.READ;
        this.message = notification.getMessage();
        this.createdAt = notification.getCreatedAt();
        this.readAt = notification.getReadAt();
    }
    
    // Getters and Setters
    public Long getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }
    
    public String getVendorPhone() {
        return vendorPhone;
    }
    
    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
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
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
    
    public Double getOrderTotal() {
        return orderTotal;
    }
    
    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }
    
    public String getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public String getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @JsonProperty("isRead")
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
