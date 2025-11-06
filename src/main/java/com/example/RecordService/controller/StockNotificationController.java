package com.example.RecordService.controller;

import com.example.RecordService.entity.StockNotification;
import com.example.RecordService.service.StockNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock-notifications")
@CrossOrigin(origins = "*")
public class StockNotificationController {
    
    @Autowired
    private StockNotificationService stockNotificationService;
    
    /**
     * Subscribe a user to be notified when an item comes back in stock
     * @param request the subscription request
     * @return the created subscription
     */
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String itemId = request.get("itemId");
            String itemType = request.get("itemType");
            String itemName = request.get("itemName");
            String businessId = request.get("businessId");
            
            if (userId == null || itemId == null || itemType == null || itemName == null || businessId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing required fields: userId, itemId, itemType, itemName, businessId"));
            }
            
            StockNotification notification = stockNotificationService.subscribeForStockNotification(
                    userId, itemId, itemType, itemName, businessId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to subscribe: " + e.getMessage()));
        }
    }
    
    /**
     * Unsubscribe a user from stock notifications
     * @param userId the user's phone number
     * @param itemId the item ID
     * @param itemType the item type
     * @return success status
     */
    @DeleteMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestParam String userId, 
                                        @RequestParam String itemId, 
                                        @RequestParam String itemType) {
        try {
            boolean deleted = stockNotificationService.unsubscribeFromStockNotification(userId, itemId, itemType);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Successfully unsubscribed"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to unsubscribe: " + e.getMessage()));
        }
    }
    
    /**
     * Check if user is subscribed to notifications for an item
     * @param userId the user's phone number
     * @param itemId the item ID
     * @param itemType the item type
     * @return subscription status
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkSubscription(@RequestParam String userId, 
                                              @RequestParam String itemId, 
                                              @RequestParam String itemType) {
        try {
            boolean isSubscribed = stockNotificationService.isSubscribed(userId, itemId, itemType);
            return ResponseEntity.ok(Map.of("subscribed", isSubscribed));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check subscription: " + e.getMessage()));
        }
    }
    
    /**
     * Get all subscriptions for a user
     * @param userId the user's phone number
     * @return list of subscriptions
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StockNotification>> getUserSubscriptions(@PathVariable String userId) {
        try {
            List<StockNotification> subscriptions = stockNotificationService.getUserSubscriptions(userId);
            return ResponseEntity.ok(subscriptions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

