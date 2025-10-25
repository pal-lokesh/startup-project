package com.example.RecordService.service;

import com.example.RecordService.entity.Order;
import com.example.RecordService.entity.OrderItem;
import com.example.RecordService.entity.Notification;
import com.example.RecordService.entity.ClientNotification;
import com.example.RecordService.model.dto.OrderRequest;
import com.example.RecordService.model.dto.OrderResponse;
import com.example.RecordService.model.dto.OrderItemResponse;
import com.example.RecordService.repository.OrderRepository;
import com.example.RecordService.service.NotificationService;
import com.example.RecordService.service.ClientNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ClientNotificationService clientNotificationService;
    
    /**
     * Create a new order
     * @param orderRequest the order request
     * @return the created order response
     */
    public OrderResponse createOrder(OrderRequest orderRequest) {
        try {
            // Calculate total amount
            Double totalAmount = orderRequest.getItems().stream()
                    .mapToDouble(item -> item.getItemPrice() * item.getQuantity())
                    .sum();
            
            // Create order entity
            Order order = new Order(
                orderRequest.getUserId(),
                orderRequest.getCustomerName(),
                orderRequest.getCustomerEmail(),
                orderRequest.getCustomerPhone(),
                orderRequest.getDeliveryAddress(),
                orderRequest.getDeliveryDate(),
                totalAmount
            );
            
            order.setSpecialNotes(orderRequest.getSpecialNotes());
            
            // Save order first to get the ID
            Order savedOrder = orderRepository.save(order);
            
            // Create order items
            List<OrderItem> orderItems = orderRequest.getItems().stream()
                    .map(itemRequest -> {
                        OrderItem orderItem = new OrderItem(
                            savedOrder,
                            itemRequest.getItemId(),
                            itemRequest.getItemName(),
                            itemRequest.getItemPrice(),
                            itemRequest.getQuantity(),
                            itemRequest.getItemType(),
                            itemRequest.getBusinessId(),
                            itemRequest.getBusinessName()
                        );
                        orderItem.setImageUrl(itemRequest.getImageUrl());
                        return orderItem;
                    })
                    .collect(Collectors.toList());
            
            // Set order items and save again
            savedOrder.setOrderItems(orderItems);
            Order finalOrder = orderRepository.save(savedOrder);
            
            // Create notification for vendors
            notificationService.createOrderNotification(finalOrder);
            
            // Create notification for client
            clientNotificationService.createOrderNotification(finalOrder);
            
            return convertToOrderResponse(finalOrder);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get order by ID
     * @param orderId the order ID
     * @return the order response
     */
    public Optional<OrderResponse> getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToOrderResponse);
    }
    
    /**
     * Get all orders for a user
     * @param userId the user ID
     * @return list of order responses
     */
    public List<OrderResponse> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all orders
     * @return list of all order responses
     */
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get orders by status
     * @param status the order status
     * @return list of order responses
     */
    public List<OrderResponse> getOrdersByStatus(Order.OrderStatus status) {
        List<Order> orders = orderRepository.findByStatusOrderByOrderDateDesc(status);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get orders by business ID
     * @param businessId the business ID
     * @return list of order responses
     */
    public List<OrderResponse> getOrdersByBusinessId(String businessId) {
        List<Order> orders = orderRepository.findByBusinessId(businessId);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Update order status
     * @param orderId the order ID
     * @param status the new status
     * @return the updated order response
     */
    public Optional<OrderResponse> updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            Order.OrderStatus oldStatus = order.getStatus();
            order.setStatus(status);
            Order updatedOrder = orderRepository.save(order);
            
            // Create notification for status change
            if (oldStatus != status) {
                Notification.NotificationType notificationType = getNotificationTypeForStatus(status);
                notificationService.createOrderUpdateNotification(updatedOrder, notificationType);
                
                // Create client notification for status change
                ClientNotification.NotificationType clientNotificationType = getClientNotificationTypeForStatus(status);
                clientNotificationService.createOrderUpdateNotification(updatedOrder, clientNotificationType);
            }
            
            return Optional.of(convertToOrderResponse(updatedOrder));
        }
        return Optional.empty();
    }
    
    /**
     * Get notification type based on order status
     * @param status the order status
     * @return notification type
     */
    private Notification.NotificationType getNotificationTypeForStatus(Order.OrderStatus status) {
        switch (status) {
            case CANCELLED:
                return Notification.NotificationType.ORDER_CANCELLED;
            case DELIVERED:
                return Notification.NotificationType.ORDER_DELIVERED;
            default:
                return Notification.NotificationType.ORDER_UPDATED;
        }
    }
    
    /**
     * Get client notification type based on order status
     * @param status the order status
     * @return the client notification type
     */
    private ClientNotification.NotificationType getClientNotificationTypeForStatus(Order.OrderStatus status) {
        switch (status) {
            case CONFIRMED:
                return ClientNotification.NotificationType.ORDER_CONFIRMED;
            case PREPARING:
                return ClientNotification.NotificationType.ORDER_PREPARING;
            case READY:
                return ClientNotification.NotificationType.ORDER_READY;
            case SHIPPED:
                return ClientNotification.NotificationType.ORDER_SHIPPED;
            case DELIVERED:
                return ClientNotification.NotificationType.ORDER_DELIVERED;
            case CANCELLED:
                return ClientNotification.NotificationType.ORDER_CANCELLED;
            default:
                return ClientNotification.NotificationType.ORDER_CONFIRMED;
        }
    }
    
    /**
     * Delete order
     * @param orderId the order ID
     * @return true if deleted, false if not found
     */
    public boolean deleteOrder(Long orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            return true;
        }
        return false;
    }
    
    /**
     * Get order statistics
     * @return order statistics
     */
    public OrderStatistics getOrderStatistics() {
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        long confirmedOrders = orderRepository.countByStatus(Order.OrderStatus.CONFIRMED);
        long shippedOrders = orderRepository.countByStatus(Order.OrderStatus.SHIPPED);
        long deliveredOrders = orderRepository.countByStatus(Order.OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(Order.OrderStatus.CANCELLED);
        
        return new OrderStatistics(totalOrders, pendingOrders, confirmedOrders, 
                                 shippedOrders, deliveredOrders, cancelledOrders);
    }
    
    /**
     * Convert Order entity to OrderResponse
     * @param order the order entity
     * @return the order response
     */
    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse(order);
        
        if (order.getOrderItems() != null) {
            List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                    .map(OrderItemResponse::new)
                    .collect(Collectors.toList());
            response.setOrderItems(orderItemResponses);
        }
        
        return response;
    }
    
    /**
     * Order statistics class
     */
    public static class OrderStatistics {
        private final long totalOrders;
        private final long pendingOrders;
        private final long confirmedOrders;
        private final long shippedOrders;
        private final long deliveredOrders;
        private final long cancelledOrders;
        
        public OrderStatistics(long totalOrders, long pendingOrders, long confirmedOrders, 
                              long shippedOrders, long deliveredOrders, long cancelledOrders) {
            this.totalOrders = totalOrders;
            this.pendingOrders = pendingOrders;
            this.confirmedOrders = confirmedOrders;
            this.shippedOrders = shippedOrders;
            this.deliveredOrders = deliveredOrders;
            this.cancelledOrders = cancelledOrders;
        }
        
        // Getters
        public long getTotalOrders() { return totalOrders; }
        public long getPendingOrders() { return pendingOrders; }
        public long getConfirmedOrders() { return confirmedOrders; }
        public long getShippedOrders() { return shippedOrders; }
        public long getDeliveredOrders() { return deliveredOrders; }
        public long getCancelledOrders() { return cancelledOrders; }
    }
}
