package com.example.RecordService.controller;

import com.example.RecordService.model.dto.NotificationResponse;
import com.example.RecordService.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Get all notifications for a vendor
     * @param vendorPhone the vendor's phone number
     * @return ResponseEntity with list of notifications
     */
    @GetMapping("/vendor/{vendorPhone}")
    public ResponseEntity<?> getNotificationsByVendor(@PathVariable String vendorPhone) {
        try {
            List<NotificationResponse> notifications = notificationService.getNotificationsByVendor(vendorPhone);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get notifications: " + e.getMessage());
        }
    }
    
    /**
     * Get unread notifications for a vendor
     * @param vendorPhone the vendor's phone number
     * @return ResponseEntity with list of unread notifications
     */
    @GetMapping("/vendor/{vendorPhone}/unread")
    public ResponseEntity<?> getUnreadNotificationsByVendor(@PathVariable String vendorPhone) {
        try {
            List<NotificationResponse> notifications = notificationService.getUnreadNotificationsByVendor(vendorPhone);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get unread notifications: " + e.getMessage());
        }
    }
    
    /**
     * Get notification count for a vendor
     * @param vendorPhone the vendor's phone number
     * @return ResponseEntity with notification count
     */
    @GetMapping("/vendor/{vendorPhone}/count")
    public ResponseEntity<?> getNotificationCount(@PathVariable String vendorPhone) {
        try {
            long count = notificationService.getUnreadNotificationCount(vendorPhone);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get notification count: " + e.getMessage());
        }
    }
    
    /**
     * Mark notification as read
     * @param notificationId the notification ID
     * @return ResponseEntity with result
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        try {
            boolean success = notificationService.markAsRead(notificationId);
            if (success) {
                return ResponseEntity.ok().body("Notification marked as read");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark notification as read: " + e.getMessage());
        }
    }
    
    /**
     * Mark all notifications as read for a vendor
     * @param vendorPhone the vendor's phone number
     * @return ResponseEntity with result
     */
    @PutMapping("/vendor/{vendorPhone}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable String vendorPhone) {
        try {
            int count = notificationService.markAllAsRead(vendorPhone);
            return ResponseEntity.ok().body("Marked " + count + " notifications as read");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark all notifications as read: " + e.getMessage());
        }
    }
    
    /**
     * Delete notification
     * @param notificationId the notification ID
     * @return ResponseEntity with result
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        try {
            boolean success = notificationService.deleteNotification(notificationId);
            if (success) {
                return ResponseEntity.ok().body("Notification deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete notification: " + e.getMessage());
        }
    }
    
    /**
     * Get notifications by business ID
     * @param businessId the business ID
     * @return ResponseEntity with list of notifications
     */
    @GetMapping("/business/{businessId}")
    public ResponseEntity<?> getNotificationsByBusiness(@PathVariable String businessId) {
        try {
            List<NotificationResponse> notifications = notificationService.getNotificationsByBusiness(businessId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get notifications: " + e.getMessage());
        }
    }
    
    /**
     * Get recent notifications for a vendor (last 30 days)
     * @param vendorPhone the vendor's phone number
     * @return ResponseEntity with list of recent notifications
     */
    @GetMapping("/vendor/{vendorPhone}/recent")
    public ResponseEntity<?> getRecentNotifications(@PathVariable String vendorPhone) {
        try {
            List<NotificationResponse> notifications = notificationService.getRecentNotifications(vendorPhone);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get recent notifications: " + e.getMessage());
        }
    }
}
