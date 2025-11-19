package com.example.RecordService.service;

import com.example.RecordService.entity.ClientNotification;
import com.example.RecordService.entity.Order;
import com.example.RecordService.model.dto.ClientNotificationResponse;
import com.example.RecordService.model.dto.ClientNotificationWithCountResponse;
import com.example.RecordService.repository.ClientNotificationRepository;
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
public class ClientNotificationService {

    @Autowired
    private ClientNotificationRepository clientNotificationRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Create a new order notification for the client
     * @param order the created order
     */
    public void createOrderNotification(Order order) {
        try {
            // Create notification message
            String itemSummary = getOrderItemSummary(order);
            String message = String.format(
                "Your %s order has been placed successfully for ₹%.2f. Delivery scheduled for %s",
                itemSummary,
                order.getTotalAmount(),
                order.getDeliveryDate()
            );

            // Create notification
            ClientNotification notification = new ClientNotification(
                order.getUserId(),
                order.getOrderItems().get(0).getBusinessId(), // Use first business ID
                order.getOrderItems().get(0).getBusinessName(), // Use first business name
                order.getOrderId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                order.getTotalAmount(),
                order.getDeliveryDate(),
                order.getDeliveryAddress(),
                ClientNotification.NotificationType.ORDER_CONFIRMED,
                message
            );

            clientNotificationRepository.save(notification);
        } catch (Exception e) {
            // Log error but don't fail order creation
            System.err.println("Error creating client order notification: " + e.getMessage());
        }
    }

    /**
     * Create notification for order status updates
     * @param order the updated order
     * @param notificationType the type of notification
     */
    public void createOrderUpdateNotification(Order order, ClientNotification.NotificationType notificationType) {
        try {
            String message = createOrderUpdateMessage(order, notificationType);

            ClientNotification notification = new ClientNotification(
                order.getUserId(),
                order.getOrderItems().get(0).getBusinessId(),
                order.getOrderItems().get(0).getBusinessName(),
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

            clientNotificationRepository.save(notification);
        } catch (Exception e) {
            System.err.println("Error creating client order update notification: " + e.getMessage());
        }
    }

    /**
     * Get all notifications for a client
     * @param clientPhone the client's phone number
     * @return list of notification responses
     */
    public List<ClientNotificationResponse> getNotificationsByClientPhone(String clientPhone) {
        System.out.println("=== FETCHING NOTIFICATIONS for client: " + clientPhone + " ===");
        List<ClientNotification> notifications = clientNotificationRepository.findByClientPhoneOrderByCreatedAtDesc(clientPhone);
        
        // Log the actual isRead values from database
        for (ClientNotification notif : notifications) {
            System.out.println("Notification ID: " + notif.getNotificationId() + ", isRead: " + notif.isRead());
        }
        
        List<ClientNotificationResponse> responses = notifications.stream()
                .map(ClientNotificationResponse::new)
                .collect(Collectors.toList());
        
        // Log the DTO values
        for (ClientNotificationResponse resp : responses) {
            System.out.println("Response Notification ID: " + resp.getNotificationId() + ", isRead: " + resp.isRead());
        }
        
        System.out.println("=== FETCHED " + responses.size() + " notifications ===");
        return responses;
    }

    /**
     * Get unread notification count for a client
     * @param clientPhone the client's phone number
     * @return unread notification count
     */
    public long getUnreadNotificationCount(String clientPhone) {
        return clientNotificationRepository.countByClientPhoneAndIsReadFalse(clientPhone);
    }

    /**
     * Mark a notification as read
     * @param notificationId the notification ID
     * @return true if marked as read, false otherwise
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean markNotificationAsRead(Long notificationId) {
        try {
            System.out.println("=== MARK AS READ START for notification " + notificationId + " ===");
            
            // First check current state with JDBC (bypass JPA cache)
            String checkSql = "SELECT is_read FROM client_notifications WHERE notification_id = ?";
            Boolean currentIsRead = null;
            try {
                currentIsRead = jdbcTemplate.queryForObject(checkSql, Boolean.class, notificationId);
                System.out.println("Current database state (from JDBC): isRead = " + currentIsRead);
            } catch (Exception e) {
                System.err.println("ERROR checking notification state: " + e.getMessage());
                return false;
            }
            
            if (currentIsRead == null) {
                System.err.println("Notification " + notificationId + " not found in database");
                return false;
            }
            
            // Always update, even if already read (idempotent operation)
            // This ensures the database is definitely updated
            String sql = "UPDATE client_notifications SET is_read = true WHERE notification_id = ?";
            int updated = jdbcTemplate.update(sql, notificationId);
            System.out.println("JDBC UPDATE executed. Rows affected: " + updated);
            
            if (updated > 0) {
                // Force commit by clearing entity manager
                entityManager.clear();
                
                // Immediately verify with a fresh JDBC query (bypass all caches)
                // Use a new connection to ensure we see committed data
                String verifySql = "SELECT is_read FROM client_notifications WHERE notification_id = ?";
                Boolean verifiedIsRead = jdbcTemplate.queryForObject(verifySql, Boolean.class, notificationId);
                System.out.println("Immediate verification (JDBC): isRead = " + verifiedIsRead);
                
                // Also check with a second query to ensure consistency
                try {
                    Thread.sleep(100);
                    Boolean secondCheck = jdbcTemplate.queryForObject(verifySql, Boolean.class, notificationId);
                    System.out.println("Second verification (JDBC after 100ms): isRead = " + secondCheck);
                    
                    if (!Boolean.TRUE.equals(secondCheck)) {
                        System.err.println("⚠️ WARNING: Second check shows notification is NOT read! Possible transaction rollback.");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                if (Boolean.TRUE.equals(verifiedIsRead)) {
                    System.out.println("✅ SUCCESS: Notification " + notificationId + " confirmed as read in database");
                    System.out.println("=== MARK AS READ END (SUCCESS) ===");
                    return true;
                } else {
                    System.err.println("❌ ERROR: Verification failed - notification " + notificationId + " is still unread after update!");
                    System.out.println("=== MARK AS READ END (VERIFICATION FAILED) ===");
                    return false;
                }
            } else {
                System.err.println("❌ ERROR: JDBC UPDATE returned 0 rows affected for notification " + notificationId);
                System.out.println("=== MARK AS READ END (UPDATE FAILED) ===");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ EXCEPTION while marking notification " + notificationId + " as read: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== MARK AS READ END (EXCEPTION) ===");
            return false;
        }
    }

    /**
     * Mark all notifications for a client as read
     * @param clientPhone the client's phone number
     */
    @Transactional
    public void markAllNotificationsAsRead(String clientPhone) {
        clientNotificationRepository.markAllAsReadByClientPhone(clientPhone);
        // Explicitly flush to ensure the changes are persisted immediately
        entityManager.flush();
    }

    /**
     * Delete a notification
     * @param notificationId the notification ID
     * @return true if deleted, false otherwise
     */
    public boolean deleteNotification(Long notificationId) {
        if (clientNotificationRepository.existsById(notificationId)) {
            clientNotificationRepository.deleteById(notificationId);
            return true;
        }
        return false;
    }

    /**
     * Get recent notifications for a client (last 30 days)
     * @param clientPhone the client's phone number
     * @return list of recent notifications
     */
    public List<ClientNotificationResponse> getRecentNotifications(String clientPhone) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<ClientNotification> notifications = clientNotificationRepository.findRecentNotificationsByClient(clientPhone, thirtyDaysAgo);
        return notifications.stream()
                .map(ClientNotificationResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Create order update message based on notification type
     * @param order the order
     * @param notificationType the notification type
     * @return the notification message
     */
    private String createOrderUpdateMessage(Order order, ClientNotification.NotificationType notificationType) {
        String orderSummary = getOrderItemSummary(order);
        switch (notificationType) {
            case ORDER_CONFIRMED:
                return String.format(
                    "Your %s order has been confirmed by %s. We're preparing it!",
                    orderSummary,
                    order.getOrderItems().get(0).getBusinessName()
                );
            case ORDER_PREPARING:
                return String.format(
                    "Your %s order is now being prepared by %s. It will be ready soon!",
                    orderSummary,
                    order.getOrderItems().get(0).getBusinessName()
                );
            case ORDER_READY:
                return String.format(
                    "Great news! Your %s order is ready for pickup/delivery from %s.",
                    orderSummary,
                    order.getOrderItems().get(0).getBusinessName()
                );
            case ORDER_SHIPPED:
                return String.format(
                    "Your %s order has been shipped and is on its way to you!",
                    orderSummary
                );
            case ORDER_DELIVERED:
                return String.format(
                    "Your %s order has been delivered successfully. Thank you for your business!",
                    orderSummary
                );
            case ORDER_CANCELLED:
                return String.format(
                    "Your %s order has been cancelled. If you have any questions, please contact us.",
                    orderSummary
                );
            default:
                return String.format(
                    "Your %s order has been updated.",
                    orderSummary
                );
        }
    }
    
    /**
     * Save a notification directly (for stock notifications and other non-order notifications)
     * @param notification the notification to save
     */
    public void saveNotification(ClientNotification notification) {
        clientNotificationRepository.save(notification);
    }

    /**
     * Get notifications with unread count in a single call (optimized)
     * @param clientPhone the client's phone number
     * @return notifications and unread count
     */
    public ClientNotificationWithCountResponse getNotificationsWithCount(String clientPhone) {
        List<ClientNotificationResponse> notifications = getNotificationsByClientPhone(clientPhone);
        long unreadCount = getUnreadNotificationCount(clientPhone);
        return new ClientNotificationWithCountResponse(notifications, unreadCount);
    }

    /**
     * Build a concise, human readable summary of the items within an order so clients see product names.
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
