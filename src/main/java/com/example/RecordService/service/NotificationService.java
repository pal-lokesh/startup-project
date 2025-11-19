package com.example.RecordService.service;

import com.example.RecordService.entity.Notification;
import com.example.RecordService.entity.Order;
import com.example.RecordService.model.dto.NotificationResponse;
import com.example.RecordService.model.Business;
import com.example.RecordService.repository.NotificationRepository;
import com.example.RecordService.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private BusinessRepository businessRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * Create a new order notification for vendors
     * @param order the order that was created
     */
    public void createOrderNotification(Order order) {
        try {
            // Get all unique business IDs from the order items
            List<String> businessIds = order.getOrderItems().stream()
                    .map(item -> item.getBusinessId())
                    .distinct()
                    .collect(Collectors.toList());
            
            // Create notifications for each business
            for (String businessId : businessIds) {
                // Get business details
                Business business = businessRepository.findByBusinessId(businessId);
                if (business != null) {
                    // Create notification message
                    String itemSummary = getOrderItemSummary(order);
                    String message = String.format(
                        "New %s order from %s for ₹%.2f. Delivery: %s",
                        itemSummary,
                        order.getCustomerName(),
                        order.getTotalAmount(),
                        order.getDeliveryDate()
                    );
                    
                    // Create notification
                    Notification notification = new Notification(
                        business.getPhoneNumber(),
                        businessId,
                        business.getBusinessName(),
                        order.getOrderId(),
                        order.getCustomerName(),
                        order.getCustomerEmail(),
                        order.getCustomerPhone(),
                        order.getTotalAmount(),
                        order.getDeliveryDate(),
                        order.getDeliveryAddress(),
                        Notification.NotificationType.NEW_ORDER,
                        message
                    );
                    
                    notificationRepository.save(notification);
                }
            }
        } catch (Exception e) {
            // Log error but don't fail order creation
            System.err.println("Error creating order notification: " + e.getMessage());
        }
    }
    
    /**
     * Create notification for order status updates
     * @param order the updated order
     * @param notificationType the type of notification
     */
    public void createOrderUpdateNotification(Order order, Notification.NotificationType notificationType) {
        try {
            // Get all unique business IDs from the order items
            List<String> businessIds = order.getOrderItems().stream()
                    .map(item -> item.getBusinessId())
                    .distinct()
                    .collect(Collectors.toList());
            
            // Create notifications for each business
            for (String businessId : businessIds) {
                Business business = businessRepository.findByBusinessId(businessId);
                if (business != null) {
                    String message = createOrderUpdateMessage(order, notificationType);
                    
                    Notification notification = new Notification(
                        business.getPhoneNumber(),
                        businessId,
                        business.getBusinessName(),
                        order.getOrderId(),
                        order.getCustomerName(),
                        order.getCustomerEmail(),
                        order.getCustomerPhone(),
                        order.getTotalAmount(),
                        order.getDeliveryDate(),
                        order.getDeliveryAddress(),
                        notificationType,
                        message
                    );
                    
                    notificationRepository.save(notification);
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating order update notification: " + e.getMessage());
        }
    }
    
    /**
     * Get all notifications for a vendor
     * @param vendorPhone the vendor's phone number
     * @return list of notifications
     */
    public List<NotificationResponse> getNotificationsByVendor(String vendorPhone) {
        List<Notification> notifications = notificationRepository.findByVendorPhoneOrderByCreatedAtDesc(vendorPhone);
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get unread notifications for a vendor
     * @param vendorPhone the vendor's phone number
     * @return list of unread notifications
     */
    public List<NotificationResponse> getUnreadNotificationsByVendor(String vendorPhone) {
        List<Notification> notifications = notificationRepository.findByVendorPhoneAndStatusOrderByCreatedAtDesc(
                vendorPhone, Notification.NotificationStatus.UNREAD);
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Mark notification as read
     * @param notificationId the notification ID
     * @return true if successful, false otherwise
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean markAsRead(Long notificationId) {
        try {
            // Use JDBC for direct database update to bypass JPA caching issues
            String sql = "UPDATE notifications SET status = 'READ', read_at = ? WHERE notification_id = ?";
            int updated = jdbcTemplate.update(sql, LocalDateTime.now(), notificationId);
            
            if (updated > 0) {
                // Clear entity manager cache to ensure fresh data on next fetch
                entityManager.clear();
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Mark all notifications as read for a vendor
     * @param vendorPhone the vendor's phone number
     * @return number of notifications marked as read
     */
    public int markAllAsRead(String vendorPhone) {
        List<Notification> unreadNotifications = notificationRepository.findByVendorPhoneAndStatusOrderByCreatedAtDesc(
                vendorPhone, Notification.NotificationStatus.UNREAD);
        
        for (Notification notification : unreadNotifications) {
            notification.setStatus(Notification.NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
        }
        
        notificationRepository.saveAll(unreadNotifications);
        return unreadNotifications.size();
    }
    
    /**
     * Get notification count for a vendor
     * @param vendorPhone the vendor's phone number
     * @return count of unread notifications
     */
    public long getUnreadNotificationCount(String vendorPhone) {
        return notificationRepository.countByVendorPhoneAndStatus(
                vendorPhone, Notification.NotificationStatus.UNREAD);
    }
    
    /**
     * Delete notification
     * @param notificationId the notification ID
     * @return true if successful, false otherwise
     */
    public boolean deleteNotification(Long notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
            return true;
        }
        return false;
    }
    
    /**
     * Get notifications by business ID
     * @param businessId the business ID
     * @return list of notifications
     */
    public List<NotificationResponse> getNotificationsByBusiness(String businessId) {
        List<Notification> notifications = notificationRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get recent notifications for a vendor (last 30 days)
     * @param vendorPhone the vendor's phone number
     * @return list of recent notifications
     */
    public List<NotificationResponse> getRecentNotifications(String vendorPhone) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Notification> notifications = notificationRepository.findRecentNotificationsByVendor(vendorPhone, thirtyDaysAgo);
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Create order update message based on notification type
     * @param order the order
     * @param notificationType the notification type
     * @return formatted message
     */
    private String createOrderUpdateMessage(Order order, Notification.NotificationType notificationType) {
        switch (notificationType) {
            case ORDER_UPDATED:
                return String.format(
                    "%s status updated to %s for %s",
                    getOrderItemSummary(order),
                    order.getStatus().toString(),
                    order.getCustomerName()
                );
            case ORDER_CANCELLED:
                return String.format(
                    "%s order has been cancelled by %s",
                    getOrderItemSummary(order),
                    order.getCustomerName()
                );
            case ORDER_DELIVERED:
                return String.format(
                    "%s order has been delivered to %s",
                    getOrderItemSummary(order),
                    order.getCustomerName()
                );
            default:
                return String.format(
                    "%s order has been updated for %s",
                    getOrderItemSummary(order),
                    order.getCustomerName()
                );
        }
    }

    /**
     * Build a human-friendly summary of the primary items in an order.
     */
    private String getOrderItemSummary(Order order) {
        if (order == null || order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return "order";
        }

        List<com.example.RecordService.entity.OrderItem> items = order.getOrderItems().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            return "order";
        }

        String primaryName = Optional.ofNullable(items.get(0).getItemName())
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .orElse("order");

        long additionalCount = items.stream()
                .skip(1)
                .filter(Objects::nonNull)
                .count();

        if (additionalCount <= 0) {
            return primaryName;
        }

        return String.format(
                "%s + %d more item%s",
                primaryName,
                additionalCount,
                additionalCount > 1 ? "s" : ""
        );
    }
}
