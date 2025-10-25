package com.example.RecordService.service;

import com.example.RecordService.entity.Chat;
import com.example.RecordService.entity.ChatMessage;
import com.example.RecordService.model.User;
import com.example.RecordService.model.dto.ChatMessageResponse;
import com.example.RecordService.model.dto.ChatResponse;
import com.example.RecordService.model.dto.SendMessageRequest;
import com.example.RecordService.repository.ChatMessageRepository;
import com.example.RecordService.repository.ChatRepository;
import com.example.RecordService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {
    
    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public ChatResponse createOrGetChat(String clientPhone, String vendorPhone, String businessId, String businessName, Long orderId) {
        Optional<Chat> existingChat = chatRepository.findActiveChat(clientPhone, vendorPhone, businessId);
        
        if (existingChat.isPresent()) {
            return convertToChatResponse(existingChat.get());
        }
        
        Chat newChat = new Chat();
        newChat.setClientPhone(clientPhone);
        newChat.setVendorPhone(vendorPhone);
        newChat.setBusinessId(businessId);
        newChat.setBusinessName(businessName);
        newChat.setOrderId(orderId);
        newChat.setIsActive(true);
        newChat.setCreatedAt(LocalDateTime.now());
        newChat.setUpdatedAt(LocalDateTime.now());
        
        Chat savedChat = chatRepository.save(newChat);
        return convertToChatResponse(savedChat);
    }
    
    public List<ChatResponse> getChatsByPhoneNumber(String phoneNumber) {
        List<Chat> chats = chatRepository.findChatsByPhoneNumber(phoneNumber);
        return chats.stream()
                .map(this::convertToChatResponse)
                .collect(Collectors.toList());
    }
    
    public List<ChatResponse> getChatsByClient(String clientPhone) {
        List<Chat> chats = chatRepository.findChatsByClient(clientPhone);
        return chats.stream()
                .map(this::convertToChatResponse)
                .collect(Collectors.toList());
    }
    
    public List<ChatResponse> getChatsByVendor(String vendorPhone) {
        List<Chat> chats = chatRepository.findChatsByVendor(vendorPhone);
        return chats.stream()
                .map(this::convertToChatResponse)
                .collect(Collectors.toList());
    }
    
    public List<ChatResponse> getChatsByBusiness(String businessId) {
        List<Chat> chats = chatRepository.findChatsByBusiness(businessId);
        return chats.stream()
                .map(this::convertToChatResponse)
                .collect(Collectors.toList());
    }
    
    public ChatMessageResponse sendMessage(SendMessageRequest request, String senderPhone) {
        Chat chat = chatRepository.findById(request.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        
        // Verify sender is part of the chat
        if (!chat.getClientPhone().equals(senderPhone) && !chat.getVendorPhone().equals(senderPhone)) {
            throw new RuntimeException("Unauthorized to send message in this chat");
        }
        
        // Check if vendor is trying to send first message (not allowed)
        boolean isVendor = chat.getVendorPhone().equals(senderPhone);
        if (isVendor) {
            // Check if client has sent any messages in this chat
            List<ChatMessage> existingMessages = chatMessageRepository.findMessagesByChatId(chat.getChatId());
            boolean clientHasMessaged = existingMessages.stream()
                    .anyMatch(msg -> msg.getSenderType() == ChatMessage.SenderType.CLIENT);
            
            if (!clientHasMessaged) {
                throw new RuntimeException("Vendors can only reply to client messages. Please wait for the client to send the first message.");
            }
        }
        
        ChatMessage message = new ChatMessage();
        message.setChat(chat);
        message.setSenderPhone(senderPhone);
        message.setSenderType(chat.getClientPhone().equals(senderPhone) ? 
                ChatMessage.SenderType.CLIENT : ChatMessage.SenderType.VENDOR);
        message.setMessage(request.getMessage());
        message.setMessageType(request.getMessageType());
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        // Update chat's updatedAt timestamp
        chat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(chat);
        
        return convertToChatMessageResponse(savedMessage);
    }
    
    public List<ChatMessageResponse> getMessages(Long chatId, String phoneNumber) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        
        // Verify user is part of the chat
        if (!chat.getClientPhone().equals(phoneNumber) && !chat.getVendorPhone().equals(phoneNumber)) {
            throw new RuntimeException("Unauthorized to view messages in this chat");
        }
        
        List<ChatMessage> messages = chatMessageRepository.findMessagesByChatId(chatId);
        return messages.stream()
                .map(this::convertToChatMessageResponse)
                .collect(Collectors.toList());
    }
    
    public void markMessagesAsRead(Long chatId, String phoneNumber) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findUnreadMessages(chatId, phoneNumber);
        
        for (ChatMessage message : unreadMessages) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
        }
        
        chatMessageRepository.saveAll(unreadMessages);
    }
    
    public Long getUnreadCount(Long chatId, String phoneNumber) {
        return chatMessageRepository.countUnreadMessages(chatId, phoneNumber);
    }
    
    public Long getTotalUnreadCount(String phoneNumber) {
        List<Chat> chats = chatRepository.findChatsByPhoneNumber(phoneNumber);
        long totalUnread = 0;
        
        for (Chat chat : chats) {
            totalUnread += chatMessageRepository.countUnreadMessages(chat.getChatId(), phoneNumber);
        }
        
        return totalUnread;
    }
    
    public boolean canVendorSendMessage(Long chatId, String vendorPhone) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        
        // Verify vendor is part of the chat
        if (!chat.getVendorPhone().equals(vendorPhone)) {
            return false;
        }
        
        // Check if client has sent any messages in this chat
        List<ChatMessage> existingMessages = chatMessageRepository.findMessagesByChatId(chatId);
        return existingMessages.stream()
                .anyMatch(msg -> msg.getSenderType() == ChatMessage.SenderType.CLIENT);
    }
    
    private ChatResponse convertToChatResponse(Chat chat) {
        ChatResponse response = new ChatResponse();
        response.setChatId(chat.getChatId());
        response.setClientPhone(chat.getClientPhone());
        response.setVendorPhone(chat.getVendorPhone());
        response.setBusinessId(chat.getBusinessId());
        response.setBusinessName(chat.getBusinessName());
        response.setOrderId(chat.getOrderId());
        response.setIsActive(chat.getIsActive());
        response.setCreatedAt(chat.getCreatedAt());
        response.setUpdatedAt(chat.getUpdatedAt());
        
        // Get last message
        List<ChatMessage> messages = chatMessageRepository.findMessagesByChatId(chat.getChatId());
        if (!messages.isEmpty()) {
            ChatMessage lastMessage = messages.get(messages.size() - 1);
            response.setLastMessage(lastMessage.getMessage());
            response.setLastMessageTime(lastMessage.getCreatedAt());
        }
        
        return response;
    }
    
    private ChatMessageResponse convertToChatMessageResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setMessageId(message.getMessageId());
        response.setChatId(message.getChat().getChatId());
        response.setSenderPhone(message.getSenderPhone());
        response.setSenderType(message.getSenderType());
        response.setMessage(message.getMessage());
        response.setMessageType(message.getMessageType());
        response.setIsRead(message.getIsRead());
        response.setReadAt(message.getReadAt());
        response.setCreatedAt(message.getCreatedAt());
        
        // Get sender name
        Optional<User> sender = userRepository.findById(message.getSenderPhone());
        if (sender.isPresent()) {
            response.setSenderName(sender.get().getFirstName() + " " + sender.get().getLastName());
        } else {
            response.setSenderName("Unknown User");
        }
        
        return response;
    }
}
