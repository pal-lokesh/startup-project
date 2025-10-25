package com.example.RecordService.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    
    @Column(name = "vendor_phone", nullable = false)
    private String vendorPhone;
    
    @Column(name = "business_id", nullable = false)
    private String businessId;
    
    @Column(name = "business_name", nullable = false)
    private String businessName;
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;
    
    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;
    
    @Column(name = "order_total", nullable = false)
    private Double orderTotal;
    
    @Column(name = "delivery_date", nullable = false)
    private String deliveryDate;
    
    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;
    
    @Column(name = "message", nullable = false)
    private String message;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    // Constructors
    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.status = NotificationStatus.UNREAD;
    }
    
    public Notification(String vendorPhone, String businessId, String businessName, 
                      Long orderId, String customerName, String customerEmail, 
                      String customerPhone, Double orderTotal, String deliveryDate, 
                      String deliveryAddress, NotificationType notificationType, String message) {
        this();
        this.vendorPhone = vendorPhone;
        this.businessId = businessId;
        this.businessName = businessName;
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.orderTotal = orderTotal;
        this.deliveryDate = deliveryDate;
        this.deliveryAddress = deliveryAddress;
        this.notificationType = notificationType;
        this.message = message;
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
    
    public NotificationType getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
    
    public NotificationStatus getStatus() {
        return status;
    }
    
    public void setStatus(NotificationStatus status) {
        this.status = status;
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
    
    // Enums
    public enum NotificationType {
        NEW_ORDER,
        ORDER_UPDATED,
        ORDER_CANCELLED,
        ORDER_DELIVERED
    }
    
    public enum NotificationStatus {
        UNREAD,
        READ
    }
}
