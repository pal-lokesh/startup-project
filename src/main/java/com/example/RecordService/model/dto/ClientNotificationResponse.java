package com.example.RecordService.model.dto;

import com.example.RecordService.entity.ClientNotification;

import java.time.LocalDateTime;

public class ClientNotificationResponse {
    private Long notificationId;
    private String clientPhone;
    private String businessId;
    private String businessName;
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Double totalAmount;
    private String deliveryDate;
    private String deliveryAddress;
    private ClientNotification.NotificationType notificationType;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public ClientNotificationResponse(ClientNotification notification) {
        this.notificationId = notification.getNotificationId();
        this.clientPhone = notification.getClientPhone();
        this.businessId = notification.getBusinessId();
        this.businessName = notification.getBusinessName();
        this.orderId = notification.getOrderId();
        this.customerName = notification.getCustomerName();
        this.customerEmail = notification.getCustomerEmail();
        this.customerPhone = notification.getCustomerPhone();
        this.totalAmount = notification.getTotalAmount();
        this.deliveryDate = notification.getDeliveryDate();
        this.deliveryAddress = notification.getDeliveryAddress();
        this.notificationType = notification.getNotificationType();
        this.message = notification.getMessage();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt();
    }

    // Getters and Setters
    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }
    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }
    public String getBusinessId() { return businessId; }
    public void setBusinessId(String businessId) { this.businessId = businessId; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public ClientNotification.NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(ClientNotification.NotificationType notificationType) { this.notificationType = notificationType; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
