package com.example.RecordService.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;
    
    @Column(name = "sender_phone", nullable = false)
    private String senderPhone;
    
    @Column(name = "sender_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SenderType senderType;
    
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "message_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public enum SenderType {
        CLIENT, VENDOR
    }
    
    public enum MessageType {
        TEXT, IMAGE, FILE, SYSTEM
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(SenderType senderType) {
        this.senderType = senderType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
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
}
