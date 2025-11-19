package com.example.RecordService.controller;

import com.example.RecordService.model.dto.ClientNotificationResponse;
import com.example.RecordService.service.ClientNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client-notifications")
@CrossOrigin(origins = "*")
public class ClientNotificationController {

    @Autowired
    private ClientNotificationService clientNotificationService;

    /**
     * Get all notifications for a specific client
     * @param clientPhone the phone number of the client
     * @return list of notifications
     */
    @GetMapping("/client/{clientPhone}")
    public ResponseEntity<?> getNotificationsByClient(@PathVariable String clientPhone) {
        try {
            List<ClientNotificationResponse> notifications = clientNotificationService.getNotificationsByClientPhone(clientPhone);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve notifications: " + e.getMessage());
        }
    }

    /**
     * Get unread notification count for a specific client
     * @param clientPhone the phone number of the client
     * @return unread notification count
     */
    @GetMapping("/client/{clientPhone}/unread-count")
    public ResponseEntity<?> getUnreadNotificationCount(@PathVariable String clientPhone) {
        try {
            long count = clientNotificationService.getUnreadNotificationCount(clientPhone);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve unread notification count: " + e.getMessage());
        }
    }

    /**
     * Mark a specific notification as read
     * @param notificationId the ID of the notification to mark as read
     * @return success or error response
     */
    @PutMapping("/{notificationId}/mark-read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            boolean success = clientNotificationService.markNotificationAsRead(notificationId);
            if (success) {
                return ResponseEntity.ok("Notification marked as read successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark notification as read: " + e.getMessage());
        }
    }

    /**
     * Mark all notifications for a client as read
     * @param clientPhone the client's phone number
     */
    @PutMapping("/client/{clientPhone}/mark-all-read")
    public ResponseEntity<?> markAllNotificationsAsRead(@PathVariable String clientPhone) {
        try {
            clientNotificationService.markAllNotificationsAsRead(clientPhone);
            return ResponseEntity.ok("All notifications marked as read for client: " + clientPhone);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark all notifications as read: " + e.getMessage());
        }
    }

    /**
     * Delete a specific notification
     * @param notificationId the ID of the notification to delete
     * @return success or error response
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        try {
            boolean success = clientNotificationService.deleteNotification(notificationId);
            if (success) {
                return ResponseEntity.ok("Notification deleted successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete notification: " + e.getMessage());
        }
    }

    /**
     * Get recent notifications for a specific client (last 30 days)
     * @param clientPhone the phone number of the client
     * @return list of recent notifications
     */
    @GetMapping("/client/{clientPhone}/recent")
    public ResponseEntity<?> getRecentNotifications(@PathVariable String clientPhone) {
        try {
            List<ClientNotificationResponse> notifications = clientNotificationService.getRecentNotifications(clientPhone);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve recent notifications: " + e.getMessage());
        }
    }

    /**
     * Get all notifications with unread count for a specific client (optimized single call)
     * @param clientPhone the phone number of the client
     * @return notifications and unread count
     */
    @GetMapping("/client/{clientPhone}/with-count")
    public ResponseEntity<?> getNotificationsWithCount(@PathVariable String clientPhone) {
        try {
            return ResponseEntity.ok(clientNotificationService.getNotificationsWithCount(clientPhone));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve notifications: " + e.getMessage());
        }
    }
}
