package com.example.RecordService.controller;

import com.example.RecordService.model.dto.ChatMessageResponse;
import com.example.RecordService.model.dto.ChatResponse;
import com.example.RecordService.model.dto.SendMessageRequest;
import com.example.RecordService.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    @PostMapping("/create")
    public ResponseEntity<?> createOrGetChat(
            @RequestParam String clientPhone,
            @RequestParam String vendorPhone,
            @RequestParam String businessId,
            @RequestParam String businessName,
            @RequestParam(required = false) Long orderId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Extract caller phone from token if available
            String callerPhone = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Extract phone from token (simplified - in production use proper JWT decoding)
                // For now, we'll check if vendorPhone matches the caller to prevent vendors from creating chats
                callerPhone = extractPhoneFromToken(authHeader);
            }
            
            ChatResponse chat = chatService.createOrGetChat(clientPhone, vendorPhone, businessId, businessName, orderId, callerPhone);
            return ResponseEntity.ok(chat);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create or get chat: " + e.getMessage()));
        }
    }
    
    // Helper method to extract phone number from JWT token
    private String extractPhoneFromToken(String token) {
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // This is a simplified implementation
        // In production, properly decode JWT token to extract phone number
        // For now, return null to indicate we couldn't extract it
        // The service will still validate based on vendorPhone parameter
        return null;
    }
    
    @GetMapping("/user/{phoneNumber}")
    public ResponseEntity<List<ChatResponse>> getChatsByUser(@PathVariable String phoneNumber) {
        List<ChatResponse> chats = chatService.getChatsByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(chats);
    }
    
    @GetMapping("/client/{clientPhone}")
    public ResponseEntity<List<ChatResponse>> getChatsByClient(@PathVariable String clientPhone) {
        List<ChatResponse> chats = chatService.getChatsByClient(clientPhone);
        return ResponseEntity.ok(chats);
    }
    
    @GetMapping("/vendor/{vendorPhone}")
    public ResponseEntity<List<ChatResponse>> getChatsByVendor(@PathVariable String vendorPhone) {
        List<ChatResponse> chats = chatService.getChatsByVendor(vendorPhone);
        return ResponseEntity.ok(chats);
    }
    
    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<ChatResponse>> getChatsByBusiness(@PathVariable String businessId) {
        List<ChatResponse> chats = chatService.getChatsByBusiness(businessId);
        return ResponseEntity.ok(chats);
    }
    
    @PostMapping("/message")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @RequestParam String senderPhone) {
        
        ChatMessageResponse message = chatService.sendMessage(request, senderPhone);
        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long chatId,
            @RequestParam String phoneNumber) {
        
        List<ChatMessageResponse> messages = chatService.getMessages(chatId, phoneNumber);
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/{chatId}/read")
    public ResponseEntity<String> markMessagesAsRead(
            @PathVariable Long chatId,
            @RequestParam String phoneNumber) {
        
        chatService.markMessagesAsRead(chatId, phoneNumber);
        return ResponseEntity.ok("Messages marked as read");
    }
    
    @GetMapping("/{chatId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable Long chatId,
            @RequestParam String phoneNumber) {
        
        Long unreadCount = chatService.getUnreadCount(chatId, phoneNumber);
        return ResponseEntity.ok(unreadCount);
    }
    
    @GetMapping("/user/{phoneNumber}/total-unread")
    public ResponseEntity<Long> getTotalUnreadCount(@PathVariable String phoneNumber) {
        Long totalUnread = chatService.getTotalUnreadCount(phoneNumber);
        return ResponseEntity.ok(totalUnread);
    }
    
    @GetMapping("/{chatId}/can-vendor-send")
    public ResponseEntity<Boolean> canVendorSendMessage(
            @PathVariable Long chatId,
            @RequestParam String vendorPhone) {
        
        Boolean canSend = chatService.canVendorSendMessage(chatId, vendorPhone);
        return ResponseEntity.ok(canSend);
    }
}
