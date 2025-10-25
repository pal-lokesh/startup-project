package com.example.RecordService.controller;

import com.example.RecordService.entity.Order;
import com.example.RecordService.entity.OrderItem;
import com.example.RecordService.model.dto.OrderResponse;
import com.example.RecordService.service.OrderService;
import com.example.RecordService.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Get all orders (for testing)
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<OrderResponse> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Get orders by business ID (for testing)
     */
    @GetMapping("/orders/business/{businessId}")
    public ResponseEntity<?> getOrdersByBusinessId(@PathVariable String businessId) {
        try {
            List<OrderResponse> orders = orderService.getOrdersByBusinessId(businessId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Get notifications by vendor (for testing)
     */
    @GetMapping("/notifications/vendor/{vendorPhone}")
    public ResponseEntity<?> getNotificationsByVendor(@PathVariable String vendorPhone) {
        try {
            return ResponseEntity.ok(notificationService.getNotificationsByVendor(vendorPhone));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Create a test order (for testing)
     */
    @PostMapping("/orders")
    public ResponseEntity<?> createTestOrder() {
        try {
            // This is a simplified test - in reality you'd need proper order creation
            return ResponseEntity.ok("Test order creation endpoint - use frontend for actual order creation");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
