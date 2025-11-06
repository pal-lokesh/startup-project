package com.example.RecordService.service;

import com.example.RecordService.entity.StockNotification;
import com.example.RecordService.entity.ClientNotification;
import com.example.RecordService.repository.StockNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockNotificationService {
    
    @Autowired
    private StockNotificationRepository stockNotificationRepository;
    
    @Autowired
    private ClientNotificationService clientNotificationService;
    
    /**
     * Subscribe a user to be notified when an item comes back in stock
     * @param userId the user's phone number
     * @param itemId the item ID (theme, inventory, or plate)
     * @param itemType the item type (THEME, INVENTORY, or PLATE)
     * @param itemName the item name
     * @param businessId the business ID
     * @return the created notification subscription
     */
    public StockNotification subscribeForStockNotification(String userId, String itemId, 
                                                          String itemType, String itemName, 
                                                          String businessId) {
        // Check if user is already subscribed
        Optional<StockNotification> existing = stockNotificationRepository
                .findByUserIdAndItemIdAndItemType(userId, itemId, itemType);
        
        if (existing.isPresent()) {
            // Reset notification status if already subscribed
            StockNotification notification = existing.get();
            notification.setNotified(false);
            notification.setNotifiedAt(null);
            return stockNotificationRepository.save(notification);
        }
        
        // Create new subscription
        StockNotification notification = new StockNotification(userId, itemId, itemType, itemName, businessId);
        return stockNotificationRepository.save(notification);
    }
    
    /**
     * Unsubscribe a user from stock notifications
     * @param userId the user's phone number
     * @param itemId the item ID
     * @param itemType the item type
     * @return true if unsubscribed, false if not found
     */
    public boolean unsubscribeFromStockNotification(String userId, String itemId, String itemType) {
        Optional<StockNotification> notification = stockNotificationRepository
                .findByUserIdAndItemIdAndItemType(userId, itemId, itemType);
        
        if (notification.isPresent()) {
            stockNotificationRepository.delete(notification.get());
            return true;
        }
        return false;
    }
    
    /**
     * Check if user is subscribed to notifications for an item
     * @param userId the user's phone number
     * @param itemId the item ID
     * @param itemType the item type
     * @return true if subscribed, false otherwise
     */
    public boolean isSubscribed(String userId, String itemId, String itemType) {
        return stockNotificationRepository.existsByUserIdAndItemIdAndItemType(userId, itemId, itemType);
    }
    
    /**
     * Get all subscriptions for a user
     * @param userId the user's phone number
     * @return list of subscriptions
     */
    public List<StockNotification> getUserSubscriptions(String userId) {
        return stockNotificationRepository.findByUserId(userId);
    }
    
    /**
     * Notify all subscribed users when an item comes back in stock
     * @param itemId the item ID
     * @param itemType the item type
     * @param itemName the item name
     */
    public void notifySubscribers(String itemId, String itemType, String itemName) {
        List<StockNotification> subscriptions = stockNotificationRepository
                .findByItemIdAndItemTypeAndNotifiedFalse(itemId, itemType);
        
        for (StockNotification subscription : subscriptions) {
            // Create client notification
            String message = String.format(
                "%s is now back in stock! You can now place an order.",
                itemName
            );
            
            ClientNotification notification = new ClientNotification();
            notification.setClientPhone(subscription.getUserId());
            notification.setBusinessId(subscription.getBusinessId());
            notification.setBusinessName(""); // Business name can be fetched if needed
            notification.setOrderId(null); // No order ID for stock notifications
            notification.setCustomerName("");
            notification.setCustomerEmail("");
            notification.setCustomerPhone(subscription.getUserId());
            notification.setTotalAmount(0.0);
            notification.setDeliveryDate("");
            notification.setDeliveryAddress("");
            notification.setNotificationType(ClientNotification.NotificationType.STOCK_AVAILABLE);
            notification.setMessage(message);
            
            clientNotificationService.saveNotification(notification);
            
            // Mark as notified
            subscription.setNotified(true);
            subscription.setNotifiedAt(LocalDateTime.now());
            stockNotificationRepository.save(subscription);
        }
    }
    
    /**
     * Get all subscriptions for an item
     * @param itemId the item ID
     * @param itemType the item type
     * @return list of subscriptions
     */
    public List<StockNotification> getItemSubscriptions(String itemId, String itemType) {
        return stockNotificationRepository.findByItemIdAndItemType(itemId, itemType);
    }
}

