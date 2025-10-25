package com.example.RecordService.model.dto;

import com.example.RecordService.entity.ChatMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SendMessageRequest {
    
    @NotNull(message = "Chat ID is required")
    private Long chatId;
    
    @NotBlank(message = "Message content is required")
    private String message;
    
    private ChatMessage.MessageType messageType = ChatMessage.MessageType.TEXT;

    // Constructors
    public SendMessageRequest() {}

    public SendMessageRequest(Long chatId, String message, ChatMessage.MessageType messageType) {
        this.chatId = chatId;
        this.message = message;
        this.messageType = messageType;
    }

    // Getters and Setters
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
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
}
