package com.example.RecordService.controller;

import com.example.RecordService.entity.Order;
import com.example.RecordService.model.dto.OrderRequest;
import com.example.RecordService.model.dto.OrderResponse;
import com.example.RecordService.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * Create a new order
     * @param orderRequest the order request
     * @return ResponseEntity with the created order
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            System.out.println("=== ORDER CREATION REQUEST ===");
            System.out.println("User ID: " + orderRequest.getUserId());
            System.out.println("Items count: " + (orderRequest.getItems() != null ? orderRequest.getItems().size() : 0));
            
            // Validate required fields
            if (orderRequest.getUserId() == null || orderRequest.getUserId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User ID is required", "message", "User ID is required"));
            }
            if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Order items are required", "message", "Order items are required"));
            }
            if (orderRequest.getCustomerName() == null || orderRequest.getCustomerName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Customer name is required", "message", "Customer name is required"));
            }
            if (orderRequest.getCustomerEmail() == null || orderRequest.getCustomerEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Customer email is required", "message", "Customer email is required"));
            }
            if (orderRequest.getCustomerPhone() == null || orderRequest.getCustomerPhone().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Customer phone is required", "message", "Customer phone is required"));
            }
            if (orderRequest.getDeliveryAddress() == null || orderRequest.getDeliveryAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Delivery address is required", "message", "Delivery address is required"));
            }
            if (orderRequest.getDeliveryDate() == null || orderRequest.getDeliveryDate().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Delivery date is required", "message", "Delivery date is required"));
            }
            
            // Log booking dates for debugging
            if (orderRequest.getItems() != null) {
                for (int i = 0; i < orderRequest.getItems().size(); i++) {
                    var item = orderRequest.getItems().get(i);
                    System.out.println("Item " + i + ": " + item.getItemName() + 
                        ", BookingDate: " + item.getBookingDate() + 
                        ", BookingDate type: " + (item.getBookingDate() != null ? item.getBookingDate().getClass().getName() : "null"));
                }
            }
            
            OrderResponse orderResponse = orderService.createOrder(orderRequest);
            System.out.println("=== ORDER CREATED SUCCESSFULLY ===");
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
            
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            e.printStackTrace();
            // Return error as JSON object for better frontend handling
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage(), "message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            // Return error as JSON object for better frontend handling
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create order: " + e.getMessage(), 
                                 "message", "Failed to create order: " + e.getMessage()));
        }
    }
    
    /**
     * Get order by ID
     * @param orderId the order ID
     * @return ResponseEntity with the order
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        try {
            Optional<OrderResponse> order = orderService.getOrderById(orderId);
            return order.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get order: " + e.getMessage());
        }
    }
    
    /**
     * Get all orders for a user
     * @param userId the user ID
     * @return ResponseEntity with list of orders
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable String userId) {
        try {
            List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get orders: " + e.getMessage());
        }
    }
    
    /**
     * Get all orders for authenticated vendor (only their business orders)
     * @return ResponseEntity with list of vendor's orders
     */
    @GetMapping
    public ResponseEntity<?> getVendorOrders() {
        try {
            // This endpoint should be secured and only return orders for the authenticated vendor's businesses
            // For now, return empty list to prevent security issue
            return ResponseEntity.ok("This endpoint needs proper authentication and authorization");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get orders: " + e.getMessage());
        }
    }
    
    /**
     * Get orders by status
     * @param status the order status
     * @return ResponseEntity with list of orders
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            List<OrderResponse> orders = orderService.getOrdersByStatus(orderStatus);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get orders: " + e.getMessage());
        }
    }
    
    /**
     * Get orders by business ID
     * @param businessId the business ID
     * @return ResponseEntity with list of orders
     */
    @GetMapping("/business/{businessId}")
    public ResponseEntity<?> getOrdersByBusinessId(@PathVariable String businessId) {
        try {
            List<OrderResponse> orders = orderService.getOrdersByBusinessId(businessId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get orders: " + e.getMessage());
        }
    }
    
    /**
     * Update order status
     * @param orderId the order ID
     * @param status the new status
     * @return ResponseEntity with the updated order
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, 
                                               @RequestParam String status) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            Optional<OrderResponse> updatedOrder = orderService.updateOrderStatus(orderId, orderStatus);
            return updatedOrder.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid status error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status: " + status, "message", e.getMessage()));
        } catch (RuntimeException e) {
            // Handle business logic errors (e.g., insufficient stock, availability issues)
            System.err.println("Order update error: " + e.getMessage());
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Cannot confirm order")) {
                // Extract the actual error message from the RuntimeException
                Throwable cause = e.getCause();
                if (cause != null && cause.getMessage() != null) {
                    errorMessage = cause.getMessage();
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", errorMessage != null ? errorMessage : "Failed to update order", 
                                 "message", errorMessage != null ? errorMessage : "Failed to update order"));
        } catch (Exception e) {
            System.err.println("Unexpected error updating order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update order: " + e.getMessage(), 
                                 "message", "Failed to update order: " + e.getMessage()));
        }
    }
    
    /**
     * Delete order
     * @param orderId the order ID
     * @return ResponseEntity with deletion result
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        try {
            boolean deleted = orderService.deleteOrder(orderId);
            if (deleted) {
                return ResponseEntity.ok().body("Order deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete order: " + e.getMessage());
        }
    }
    
    /**
     * Get order statistics
     * @return ResponseEntity with order statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getOrderStatistics() {
        try {
            OrderService.OrderStatistics statistics = orderService.getOrderStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get statistics: " + e.getMessage());
        }
    }
}
