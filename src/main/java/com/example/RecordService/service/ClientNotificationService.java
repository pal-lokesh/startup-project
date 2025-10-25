package com.example.RecordService.service;

import com.example.RecordService.entity.ClientNotification;
import com.example.RecordService.entity.Order;
import com.example.RecordService.model.dto.ClientNotificationResponse;
import com.example.RecordService.repository.ClientNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientNotificationService {

    @Autowired
    private ClientNotificationRepository clientNotificationRepository;

    /**
     * Create a new order notification for the client
     * @param order the created order
     */
    public void createOrderNotification(Order order) {
        try {
            // Create notification message
            String message = String.format(
                "Your order #%d has been placed successfully for ₹%.2f. Delivery scheduled for %s",
                order.getOrderId(),
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
        List<ClientNotification> notifications = clientNotificationRepository.findByClientPhoneOrderByCreatedAtDesc(clientPhone);
        return notifications.stream()
                .map(ClientNotificationResponse::new)
                .collect(Collectors.toList());
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
    public boolean markNotificationAsRead(Long notificationId) {
        Optional<ClientNotification> notificationOpt = clientNotificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            ClientNotification notification = notificationOpt.get();
            notification.setRead(true);
            clientNotificationRepository.save(notification);
            return true;
        }
        return false;
    }

    /**
     * Mark all notifications for a client as read
     * @param clientPhone the client's phone number
     */
    public void markAllNotificationsAsRead(String clientPhone) {
        clientNotificationRepository.markAllAsReadByClientPhone(clientPhone);
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
        switch (notificationType) {
            case ORDER_CONFIRMED:
                return String.format(
                    "Your order #%d has been confirmed by %s. We're preparing your order!",
                    order.getOrderId(),
                    order.getOrderItems().get(0).getBusinessName()
                );
            case ORDER_PREPARING:
                return String.format(
                    "Your order #%d is now being prepared by %s. It will be ready soon!",
                    order.getOrderId(),
                    order.getOrderItems().get(0).getBusinessName()
                );
            case ORDER_READY:
                return String.format(
                    "Great news! Your order #%d is ready for pickup/delivery from %s.",
                    order.getOrderId(),
                    order.getOrderItems().get(0).getBusinessName()
                );
            case ORDER_SHIPPED:
                return String.format(
                    "Your order #%d has been shipped and is on its way to you!",
                    order.getOrderId()
                );
            case ORDER_DELIVERED:
                return String.format(
                    "Your order #%d has been delivered successfully. Thank you for your business!",
                    order.getOrderId()
                );
            case ORDER_CANCELLED:
                return String.format(
                    "Your order #%d has been cancelled. If you have any questions, please contact us.",
                    order.getOrderId()
                );
            default:
                return String.format(
                    "Your order #%d has been updated.",
                    order.getOrderId()
                );
        }
    }
}
