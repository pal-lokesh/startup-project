package com.example.RecordService.repository;

import com.example.RecordService.entity.StockNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockNotificationRepository extends JpaRepository<StockNotification, Long> {
    
    // Find all notifications for a specific item
    List<StockNotification> findByItemIdAndItemType(String itemId, String itemType);
    
    // Find all notifications for a user
    List<StockNotification> findByUserId(String userId);
    
    // Find a specific notification by user and item
    Optional<StockNotification> findByUserIdAndItemIdAndItemType(String userId, String itemId, String itemType);
    
    // Find a specific notification by user, item, and requested date
    Optional<StockNotification> findByUserIdAndItemIdAndItemTypeAndRequestedDate(String userId, String itemId, String itemType, String requestedDate);
    
    // Find all non-notified subscriptions for an item
    List<StockNotification> findByItemIdAndItemTypeAndNotifiedFalse(String itemId, String itemType);
    
    // Find all non-notified subscriptions for an item and specific date
    List<StockNotification> findByItemIdAndItemTypeAndRequestedDateAndNotifiedFalse(String itemId, String itemType, String requestedDate);
    
    // Find all non-notified subscriptions for an item (general or date-specific)
    // This includes subscriptions with null requestedDate (general) or matching requestedDate
    @org.springframework.data.jpa.repository.Query("SELECT sn FROM StockNotification sn WHERE sn.itemId = ?1 AND sn.itemType = ?2 AND sn.notified = false AND (sn.requestedDate IS NULL OR sn.requestedDate = ?3)")
    List<StockNotification> findRelevantSubscriptionsForItemAndDate(String itemId, String itemType, String requestedDate);
    
    // Check if user is already subscribed (general subscription)
    boolean existsByUserIdAndItemIdAndItemType(String userId, String itemId, String itemType);
    
    // Check if user is already subscribed for a specific date
    boolean existsByUserIdAndItemIdAndItemTypeAndRequestedDate(String userId, String itemId, String itemType, String requestedDate);
}

