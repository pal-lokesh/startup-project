package com.example.RecordService.model.dto;

import com.example.RecordService.entity.ChatMessage;
import java.time.LocalDateTime;

public class ChatMessageResponse {
    private Long messageId;
    private Long chatId;
    private String senderPhone;
    private ChatMessage.SenderType senderType;
    private String message;
    private ChatMessage.MessageType messageType;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private String senderName;

    // Constructors
    public ChatMessageResponse() {}

    public ChatMessageResponse(Long messageId, Long chatId, String senderPhone, ChatMessage.SenderType senderType, String message, ChatMessage.MessageType messageType, Boolean isRead, LocalDateTime readAt, LocalDateTime createdAt, String senderName) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderPhone = senderPhone;
        this.senderType = senderType;
        this.message = message;
        this.messageType = messageType;
        this.isRead = isRead;
        this.readAt = readAt;
        this.createdAt = createdAt;
        this.senderName = senderName;
    }

    // Getters and Setters
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public ChatMessage.SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(ChatMessage.SenderType senderType) {
        this.senderType = senderType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatMessage.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(ChatMessage.MessageType messageType) {
        this.messageType = messageType;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
