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
     * @param requestedDate optional specific date the user wants to be notified about (for date-wise availability)
     * @return the created notification subscription
     */
    public StockNotification subscribeForStockNotification(String userId, String itemId, 
                                                          String itemType, String itemName, 
                                                          String businessId, String requestedDate) {
        // Check if user is already subscribed for this specific date (or general if no date)
        Optional<StockNotification> existing;
        if (requestedDate != null && !requestedDate.isEmpty()) {
            existing = stockNotificationRepository
                    .findByUserIdAndItemIdAndItemTypeAndRequestedDate(userId, itemId, itemType, requestedDate);
        } else {
            existing = stockNotificationRepository
                    .findByUserIdAndItemIdAndItemType(userId, itemId, itemType);
        }
        
        if (existing.isPresent()) {
            // Reset notification status if already subscribed
            StockNotification notification = existing.get();
            notification.setNotified(false);
            notification.setNotifiedAt(null);
            // Update requested date if provided
            if (requestedDate != null && !requestedDate.isEmpty()) {
                notification.setRequestedDate(requestedDate);
            }
            return stockNotificationRepository.save(notification);
        }
        
        // Create new subscription
        StockNotification notification;
        if (requestedDate != null && !requestedDate.isEmpty()) {
            notification = new StockNotification(userId, itemId, itemType, itemName, businessId, requestedDate);
        } else {
            notification = new StockNotification(userId, itemId, itemType, itemName, businessId);
        }
        return stockNotificationRepository.save(notification);
    }
    
    /**
     * Subscribe a user to be notified when an item comes back in stock (overload without date)
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
        return subscribeForStockNotification(userId, itemId, itemType, itemName, businessId, null);
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
     * @param requestedDate optional specific date to check subscription for
     * @return true if subscribed, false otherwise
     */
    public boolean isSubscribed(String userId, String itemId, String itemType, String requestedDate) {
        if (requestedDate != null && !requestedDate.isEmpty()) {
            return stockNotificationRepository.existsByUserIdAndItemIdAndItemTypeAndRequestedDate(userId, itemId, itemType, requestedDate);
        } else {
            return stockNotificationRepository.existsByUserIdAndItemIdAndItemType(userId, itemId, itemType);
        }
    }
    
    /**
     * Check if user is subscribed to notifications for an item (overload without date)
     * @param userId the user's phone number
     * @param itemId the item ID
     * @param itemType the item type
     * @return true if subscribed, false otherwise
     */
    public boolean isSubscribed(String userId, String itemId, String itemType) {
        return isSubscribed(userId, itemId, itemType, null);
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
     * @param availabilityDate optional availability date (for date-wise availability)
     */
    public void notifySubscribers(String itemId, String itemType, String itemName, String availabilityDate) {
        // Find relevant subscriptions:
        // 1. General subscriptions (requestedDate is null) - notify for any availability
        // 2. Date-specific subscriptions matching the availability date
        List<StockNotification> subscriptions;
        if (availabilityDate != null && !availabilityDate.isEmpty()) {
            // For date-wise availability, notify both general subscribers and date-specific subscribers
            subscriptions = stockNotificationRepository.findRelevantSubscriptionsForItemAndDate(itemId, itemType, availabilityDate);
        } else {
            // For general stock availability, only notify general subscribers (no specific date)
            subscriptions = stockNotificationRepository
                    .findByItemIdAndItemTypeAndNotifiedFalse(itemId, itemType)
                    .stream()
                    .filter(sub -> sub.getRequestedDate() == null || sub.getRequestedDate().isEmpty())
                    .collect(java.util.stream.Collectors.toList());
        }
        
        System.out.println("Notifying subscribers for " + itemId + " (" + itemType + ") for date " + 
            (availabilityDate != null ? availabilityDate : "general") + ": Found " + subscriptions.size() + " subscriptions");
        
        if (subscriptions.isEmpty()) {
            System.out.println("No subscribers found for " + itemId + " (" + itemType + ") - skipping notification");
            return;
        }
        
        for (StockNotification subscription : subscriptions) {
            try {
                // Only notify if:
                // 1. It's a general subscription (no requestedDate), OR
                // 2. The requestedDate matches the availabilityDate
                boolean shouldNotify = false;
                if (subscription.getRequestedDate() == null || subscription.getRequestedDate().isEmpty()) {
                    // General subscription - notify for any availability
                    shouldNotify = true;
                } else if (availabilityDate != null && availabilityDate.equals(subscription.getRequestedDate())) {
                    // Date-specific subscription - only notify if dates match
                    shouldNotify = true;
                }
                
                if (!shouldNotify) {
                    System.out.println("Skipping notification for user " + subscription.getUserId() + 
                        " - requested date (" + subscription.getRequestedDate() + 
                        ") doesn't match availability date (" + availabilityDate + ")");
                    continue;
                }
                
                // Create client notification message with date if available
                String message;
                String notificationDate = availabilityDate != null ? availabilityDate : subscription.getRequestedDate();
                if (notificationDate != null && !notificationDate.isEmpty()) {
                    message = String.format(
                        "%s is now available for booking on %s! You can now place an order.",
                        itemName,
                        notificationDate
                    );
                } else {
                    message = String.format(
                        "%s is now back in stock! You can now place an order.",
                        itemName
                    );
                }
                
                System.out.println("Creating notification for user " + subscription.getUserId() + 
                    " for item " + itemId + " (" + itemType + ") for date " + notificationDate);
                
                ClientNotification notification = new ClientNotification();
                notification.setClientPhone(subscription.getUserId());
                notification.setBusinessId(subscription.getBusinessId());
                notification.setBusinessName(""); // Business name can be fetched if needed
                notification.setOrderId(null); // No order ID for stock notifications
                notification.setCustomerName("");
                notification.setCustomerEmail("");
                notification.setCustomerPhone(subscription.getUserId());
                notification.setTotalAmount(0.0);
                notification.setDeliveryDate(notificationDate != null ? notificationDate : "");
                notification.setDeliveryAddress("");
                notification.setNotificationType(ClientNotification.NotificationType.STOCK_AVAILABLE);
                notification.setMessage(message);
                
                clientNotificationService.saveNotification(notification);
                System.out.println("Successfully saved notification for user " + subscription.getUserId());
                
                // Mark as notified
                subscription.setNotified(true);
                subscription.setNotifiedAt(LocalDateTime.now());
                stockNotificationRepository.save(subscription);
                System.out.println("Marked subscription as notified for user " + subscription.getUserId());
            } catch (Exception e) {
                System.err.println("Error creating notification for user " + subscription.getUserId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("Completed notification process for " + itemId + " (" + itemType + ")");
    }
    
    /**
     * Notify all subscribed users when an item comes back in stock (overload without date)
     * @param itemId the item ID
     * @param itemType the item type
     * @param itemName the item name
     */
    public void notifySubscribers(String itemId, String itemType, String itemName) {
        notifySubscribers(itemId, itemType, itemName, null);
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

