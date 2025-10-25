package com.example.RecordService.repository;

import com.example.RecordService.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a vendor
     * @param vendorPhone the vendor's phone number
     * @return list of notifications for the vendor
     */
    List<Notification> findByVendorPhoneOrderByCreatedAtDesc(String vendorPhone);
    
    /**
     * Find all unread notifications for a vendor
     * @param vendorPhone the vendor's phone number
     * @return list of unread notifications
     */
    List<Notification> findByVendorPhoneAndStatusOrderByCreatedAtDesc(String vendorPhone, Notification.NotificationStatus status);
    
    /**
     * Find notifications by business ID
     * @param businessId the business ID
     * @return list of notifications for the business
     */
    List<Notification> findByBusinessIdOrderByCreatedAtDesc(String businessId);
    
    /**
     * Find notifications by order ID
     * @param orderId the order ID
     * @return list of notifications for the order
     */
    List<Notification> findByOrderIdOrderByCreatedAtDesc(Long orderId);
    
    /**
     * Count unread notifications for a vendor
     * @param vendorPhone the vendor's phone number
     * @return count of unread notifications
     */
    long countByVendorPhoneAndStatus(String vendorPhone, Notification.NotificationStatus status);
    
    /**
     * Find notifications by type and vendor
     * @param vendorPhone the vendor's phone number
     * @param notificationType the notification type
     * @return list of notifications
     */
    List<Notification> findByVendorPhoneAndNotificationTypeOrderByCreatedAtDesc(String vendorPhone, Notification.NotificationType notificationType);
    
    /**
     * Find recent notifications for a vendor (last 30 days)
     * @param vendorPhone the vendor's phone number
     * @return list of recent notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.vendorPhone = :vendorPhone AND n.createdAt >= :startDate ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotificationsByVendor(@Param("vendorPhone") String vendorPhone, @Param("startDate") java.time.LocalDateTime startDate);
}
