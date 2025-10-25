package com.example.RecordService.model.dto;

import java.time.LocalDateTime;

public class ChatResponse {
    private Long chatId;
    private String clientPhone;
    private String vendorPhone;
    private String businessId;
    private String businessName;
    private Long orderId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long unreadCount;
    private String otherParticipantName;
    private String otherParticipantPhone;

    // Constructors
    public ChatResponse() {}

    public ChatResponse(Long chatId, String clientPhone, String vendorPhone, String businessId, String businessName, Long orderId, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt, String lastMessage, LocalDateTime lastMessageTime, Long unreadCount, String otherParticipantName, String otherParticipantPhone) {
        this.chatId = chatId;
        this.clientPhone = clientPhone;
        this.vendorPhone = vendorPhone;
        this.businessId = businessId;
        this.businessName = businessName;
        this.orderId = orderId;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
        this.otherParticipantName = otherParticipantName;
        this.otherParticipantPhone = otherParticipantPhone;
    }

    // Getters and Setters
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getOtherParticipantName() {
        return otherParticipantName;
    }

    public void setOtherParticipantName(String otherParticipantName) {
        this.otherParticipantName = otherParticipantName;
    }

    public String getOtherParticipantPhone() {
        return otherParticipantPhone;
    }

    public void setOtherParticipantPhone(String otherParticipantPhone) {
        this.otherParticipantPhone = otherParticipantPhone;
    }
}
