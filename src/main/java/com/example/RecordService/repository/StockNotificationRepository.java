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
    
    // Find all non-notified subscriptions for an item
    List<StockNotification> findByItemIdAndItemTypeAndNotifiedFalse(String itemId, String itemType);
    
    // Check if user is already subscribed
    boolean existsByUserIdAndItemIdAndItemType(String userId, String itemId, String itemType);
}

