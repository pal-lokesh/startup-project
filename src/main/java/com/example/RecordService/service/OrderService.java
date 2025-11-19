package com.example.RecordService.service;

import com.example.RecordService.entity.Order;
import com.example.RecordService.entity.OrderItem;
import com.example.RecordService.entity.Notification;
import com.example.RecordService.entity.ClientNotification;
import com.example.RecordService.model.Theme;
import com.example.RecordService.entity.Inventory;
import com.example.RecordService.entity.Plate;
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
    
    @Autowired
    private ThemeService themeService;
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private PlateService plateService;
    
    @Autowired
    private AvailabilityService availabilityService;
    
    
    /**
     * Create a new order
     * @param orderRequest the order request
     * @return the created order response
     */
    public OrderResponse createOrder(OrderRequest orderRequest) {
        try {
            System.out.println("OrderService.createOrder - Starting order creation");
            System.out.println("Items to process: " + orderRequest.getItems().size());
            
            // Validate stock availability and date availability before creating order
            System.out.println("Validating stock availability...");
            validateStockAvailability(orderRequest.getItems());
            System.out.println("Stock validation passed");
            
            System.out.println("Validating date availability...");
            validateDateAvailability(orderRequest.getItems());
            System.out.println("Date validation passed");
            
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
            
            // Set delivery geolocation if provided
            if (orderRequest.getDeliveryLatitude() != null && orderRequest.getDeliveryLongitude() != null) {
                order.setDeliveryLatitude(orderRequest.getDeliveryLatitude());
                order.setDeliveryLongitude(orderRequest.getDeliveryLongitude());
            }
            
            // Save order first to get the ID
            Order savedOrder = orderRepository.save(order);
            
            // Create order items
            List<OrderItem> orderItems = orderRequest.getItems().stream()
                    .map(itemRequest -> {
                        OrderItem orderItem;
                        if (itemRequest.getBookingDate() != null) {
                            // Use constructor with booking date
                            orderItem = new OrderItem(
                                savedOrder,
                                itemRequest.getItemId(),
                                itemRequest.getItemName(),
                                itemRequest.getItemPrice(),
                                itemRequest.getQuantity(),
                                itemRequest.getItemType(),
                                itemRequest.getBusinessId(),
                                itemRequest.getBusinessName(),
                                itemRequest.getBookingDate()
                            );
                        } else {
                            // Use constructor without booking date
                            orderItem = new OrderItem(
                                savedOrder,
                                itemRequest.getItemId(),
                                itemRequest.getItemName(),
                                itemRequest.getItemPrice(),
                                itemRequest.getQuantity(),
                                itemRequest.getItemType(),
                                itemRequest.getBusinessId(),
                                itemRequest.getBusinessName()
                            );
                        }
                        orderItem.setImageUrl(itemRequest.getImageUrl());
                        // Set selected dishes if present (for plates)
                        if (itemRequest.getSelectedDishes() != null && !itemRequest.getSelectedDishes().trim().isEmpty()) {
                            orderItem.setSelectedDishes(itemRequest.getSelectedDishes());
                        }
                        return orderItem;
                    })
                    .collect(Collectors.toList());
            
            // Set order items and save again
            savedOrder.setOrderItems(orderItems);
            Order finalOrder = orderRepository.save(savedOrder);
            
            // Decrement stock immediately when order is created
            // This reserves the stock for the order
            try {
                System.out.println("Decrementing stock for order " + finalOrder.getOrderId());
                decrementStockForOrderItems(finalOrder.getOrderItems());
                System.out.println("Stock decremented successfully for order " + finalOrder.getOrderId());
            } catch (Exception e) {
                System.err.println("Failed to decrement stock for order " + finalOrder.getOrderId() + ": " + e.getMessage());
                e.printStackTrace();
                // Don't fail the order creation if stock decrement fails - log it for investigation
                // Stock will be validated again when order is confirmed
            }
            
            // Create notification for vendors
            notificationService.createOrderNotification(finalOrder);
            
            // Create notification for client
            clientNotificationService.createOrderNotification(finalOrder);
            
            return convertToOrderResponse(finalOrder);
            
        } catch (IllegalArgumentException e) {
            // Re-throw validation errors
            throw e;
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
     * Check if a client has purchased and received a specific item (only DELIVERED orders)
     * @param userId the user ID (client's phone number)
     * @param itemId the item ID (themeId, inventoryId, or plateId)
     * @param itemType the item type (THEME, INVENTORY, or PLATE)
     * @return true if client has a DELIVERED order containing the item, false otherwise
     */
    public boolean hasClientPurchasedItem(String userId, String itemId, String itemType) {
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        
        // Only check DELIVERED orders - clients can only rate after delivery
        return orders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.DELIVERED)
                .anyMatch(order -> {
                    if (order.getOrderItems() == null) {
                        return false;
                    }
                    return order.getOrderItems().stream()
                            .anyMatch(item -> item.getItemId().equals(itemId) && 
                                           item.getItemType().equalsIgnoreCase(itemType));
                });
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
                
                // If order is confirmed, no need to re-validate - validation was already done at client side
                // Stock/availability was already decremented when order was created, so no need to decrement again
                if (status == Order.OrderStatus.CONFIRMED && oldStatus == Order.OrderStatus.PENDING) {
                    System.out.println("Order " + updatedOrder.getOrderId() + " confirmed - stock/availability was already validated and decremented on creation");
                }
                
                // If order is cancelled, restore stock (stock was decremented on order creation, so restore it)
                if (status == Order.OrderStatus.CANCELLED) {
                    try {
                        System.out.println("Restoring stock for cancelled order " + updatedOrder.getOrderId());
                        restoreStockForOrderItems(updatedOrder.getOrderItems());
                        System.out.println("Stock restored successfully for cancelled order " + updatedOrder.getOrderId());
                    } catch (Exception e) {
                        System.err.println("Failed to restore stock for cancelled order " + updatedOrder.getOrderId() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                // If order is delivered, create rating requests for each item
                if (status == Order.OrderStatus.DELIVERED) {
                    try {
                        createRatingRequestsForDeliveredOrder(updatedOrder);
                    } catch (Exception e) {
                        System.err.println("Failed to create rating requests for order " + updatedOrder.getOrderId() + ": " + e.getMessage());
                    }
                }
                
            }
            
            return Optional.of(convertToOrderResponse(updatedOrder));
        }
        return Optional.empty();
    }
    
    /**
     * Create rating requests for all items in a delivered order
     * @param order the delivered order
     */
    private void createRatingRequestsForDeliveredOrder(Order order) {
        // This method will be called when an order is delivered
        // The actual rating creation will be handled by the frontend when the client submits ratings
        // We just need to ensure the order is marked as delivered and can be rated
        System.out.println("Order " + order.getOrderId() + " has been delivered. Client can now rate the items.");
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
     * Validate date availability for order items before creating order
     * @param items the order items to validate
     * @throws IllegalArgumentException if item is not available on the requested date
     */
    private void validateDateAvailability(List<com.example.RecordService.model.dto.OrderItemRequest> items) {
        for (com.example.RecordService.model.dto.OrderItemRequest item : items) {
            if (item.getBookingDate() != null) {
                try {
                    System.out.println("Validating date availability for item: " + item.getItemName() + 
                        ", Date: " + item.getBookingDate() + 
                        ", Quantity: " + item.getQuantity());
                    
                    // Check if item is available on the requested date
                    Integer availableQuantity = availabilityService.getAvailableQuantity(
                        item.getItemId(), 
                        item.getItemType().toLowerCase(), 
                        item.getBookingDate()
                    );
                    
                    System.out.println("Available quantity for " + item.getItemName() + " on " + 
                        item.getBookingDate() + ": " + availableQuantity);
                    
                    if (availableQuantity == null || availableQuantity < item.getQuantity()) {
                        throw new IllegalArgumentException(
                            "Item '" + item.getItemName() + "' is not available on " + item.getBookingDate() + ". " +
                            "Available quantity: " + (availableQuantity != null ? availableQuantity : 0) + 
                            ", Requested: " + item.getQuantity()
                        );
                    }
                } catch (IllegalArgumentException e) {
                    // Re-throw validation errors
                    throw e;
                } catch (Exception e) {
                    System.err.println("Error validating date availability for item " + item.getItemName() + ": " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("Failed to validate date availability: " + e.getMessage(), e);
                }
            }
        }
    }
    
    /**
     * Validate date availability for existing order items (used when confirming order)
     * @param orderItems the order items to validate
     * @param orderId the order ID (used to exclude this order from booked quantity calculation)
     * @throws IllegalArgumentException if item is not available on the requested date
     */
    private void validateDateAvailabilityForOrderItems(List<OrderItem> orderItems, Long orderId) {
        if (orderItems == null) {
            return;
        }
        
        for (OrderItem item : orderItems) {
            if (item == null) {
                continue;
            }
            
            if (item.getBookingDate() != null) {
                try {
                    System.out.println("Validating date availability for order item: " + item.getItemName() + 
                        ", Date: " + item.getBookingDate() + 
                        ", Quantity: " + item.getQuantity() +
                        ", Order ID: " + orderId);
                    
                    // Check if item is available on the requested date
                    // Note: Availability was already decremented when order was created (PENDING),
                    // so we need to add back this order's quantity to check if it was valid
                    Integer availableQuantity = availabilityService.getAvailableQuantity(
                        item.getItemId(), 
                        item.getItemType().toLowerCase(), 
                        item.getBookingDate()
                    );
                    
                    // Add back this order's quantity since it was already reserved when order was created
                    // This allows the vendor to confirm the "last available" order
                    int adjustedAvailableQuantity = availableQuantity + item.getQuantity();
                    
                    System.out.println("Available quantity for " + item.getItemName() + " on " + 
                        item.getBookingDate() + ": " + availableQuantity + 
                        " (adjusted: " + adjustedAvailableQuantity + " including this order's reserved quantity)");
                    
                    if (adjustedAvailableQuantity < item.getQuantity()) {
                        throw new IllegalArgumentException(
                            "Item '" + item.getItemName() + "' is not available on " + item.getBookingDate() + ". " +
                            "Available quantity: " + availableQuantity + 
                            ", Requested: " + item.getQuantity()
                        );
                    }
                } catch (IllegalArgumentException e) {
                    // Re-throw validation errors
                    throw e;
                } catch (Exception e) {
                    System.err.println("Error validating date availability for item " + item.getItemName() + ": " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("Failed to validate date availability: " + e.getMessage(), e);
                }
            }
        }
    }
    
    /**
     * Validate stock availability for order items before creating order
     * Note: If an item has a bookingDate, skip general stock validation as date availability is checked separately
     * @param items the order items to validate
     * @throws IllegalArgumentException if stock is insufficient
     */
    private void validateStockAvailability(List<com.example.RecordService.model.dto.OrderItemRequest> items) {
        for (com.example.RecordService.model.dto.OrderItemRequest item : items) {
            // Skip general stock validation if item has a booking date
            // Date availability will be validated separately in validateDateAvailability()
            if (item.getBookingDate() != null) {
                System.out.println("Skipping general stock validation for item " + item.getItemName() + 
                    " - has booking date: " + item.getBookingDate());
                continue;
            }
            
            String itemType = item.getItemType().toUpperCase();
            String itemId = item.getItemId();
            int requestedQuantity = item.getQuantity();
            
            System.out.println("Validating general stock for item: " + item.getItemName() + 
                ", Type: " + itemType + ", Quantity: " + requestedQuantity);
            
            if ("THEME".equals(itemType)) {
                Theme theme = themeService.getThemeById(itemId);
                if (theme == null) {
                    throw new IllegalArgumentException("Theme with ID " + itemId + " not found");
                }
                System.out.println("Theme general stock: " + theme.getQuantity());
                if (theme.getQuantity() < requestedQuantity) {
                    throw new IllegalArgumentException(
                        "Insufficient stock for theme '" + item.getItemName() + "'. " +
                        "Available: " + theme.getQuantity() + ", Requested: " + requestedQuantity
                    );
                }
            } else if ("INVENTORY".equals(itemType)) {
                Optional<Inventory> inventoryOpt = inventoryService.getInventoryById(itemId);
                if (inventoryOpt.isEmpty()) {
                    throw new IllegalArgumentException("Inventory with ID " + itemId + " not found");
                }
                Inventory inventory = inventoryOpt.get();
                System.out.println("Inventory general stock: " + inventory.getQuantity());
                if (inventory.getQuantity() < requestedQuantity) {
                    throw new IllegalArgumentException(
                        "Insufficient stock for inventory '" + item.getItemName() + "'. " +
                        "Available: " + inventory.getQuantity() + ", Requested: " + requestedQuantity
                    );
                }
            } else if ("PLATE".equals(itemType)) {
                Optional<Plate> plateOpt = plateService.getPlateById(itemId);
                if (plateOpt.isEmpty()) {
                    throw new IllegalArgumentException("Plate with ID " + itemId + " not found");
                }
                Plate plate = plateOpt.get();
                System.out.println("Plate general stock: " + plate.getQuantity());
                if (plate.getQuantity() < requestedQuantity) {
                    throw new IllegalArgumentException(
                        "Insufficient stock for plate '" + item.getItemName() + "'. " +
                        "Available: " + plate.getQuantity() + ", Requested: " + requestedQuantity
                    );
                }
            }
        }
    }
    
    // Stock validation during confirmation removed: stock is enforced at client checkout time
    
    /**
     * Decrement stock for all items in an order
     * Note: For items with booking dates, only date availability is decremented, not general stock
     * @param orderItems the order items
     */
    private void decrementStockForOrderItems(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            try {
                String itemType = item.getItemType().toUpperCase();
                String itemId = item.getItemId();
                int quantity = item.getQuantity();
                
                // Decrement date availability if booking date is set
                if (item.getBookingDate() != null) {
                    System.out.println("Decrementing date availability for item " + item.getItemName() + 
                        " on date " + item.getBookingDate() + " by quantity " + quantity);
                    availabilityService.decrementAvailability(
                        itemId, 
                        item.getItemType().toLowerCase(), 
                        item.getBookingDate(), 
                        quantity
                    );
                    // Skip general stock decrement for items with booking dates
                    continue;
                }
                
                // Decrement general stock only for items without booking dates
                System.out.println("Decrementing general stock for item " + item.getItemName() + " by quantity " + quantity);
                
                if ("THEME".equals(itemType)) {
                    Theme theme = themeService.getThemeById(itemId);
                    if (theme != null) {
                        int currentQuantity = theme.getQuantity();
                        int newQuantity = Math.max(0, currentQuantity - quantity);
                        System.out.println("Theme " + item.getItemName() + " stock: " + currentQuantity + " -> " + newQuantity);
                        theme.setQuantity(newQuantity);
                        theme.setUpdatedAt(LocalDateTime.now());
                        themeService.saveTheme(theme);
                    }
                } else if ("INVENTORY".equals(itemType)) {
                    Optional<Inventory> inventoryOpt = inventoryService.getInventoryById(itemId);
                    if (inventoryOpt.isPresent()) {
                        Inventory inventory = inventoryOpt.get();
                        int currentQuantity = inventory.getQuantity();
                        int newQuantity = Math.max(0, currentQuantity - quantity);
                        System.out.println("Inventory " + item.getItemName() + " stock: " + currentQuantity + " -> " + newQuantity);
                        inventory.setQuantity(newQuantity);
                        inventory.setUpdatedAt(LocalDateTime.now());
                        inventoryService.updateInventory(inventory);
                    }
                } else if ("PLATE".equals(itemType)) {
                    Optional<Plate> plateOpt = plateService.getPlateById(itemId);
                    if (plateOpt.isPresent()) {
                        Plate plate = plateOpt.get();
                        int currentQuantity = plate.getQuantity();
                        int newQuantity = Math.max(0, currentQuantity - quantity);
                        System.out.println("Plate " + item.getItemName() + " stock: " + currentQuantity + " -> " + newQuantity);
                        plate.setQuantity(newQuantity);
                        plate.setUpdatedAt(LocalDateTime.now());
                        plateService.updatePlate(itemId, plate);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error decrementing stock for item " + item.getItemId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Restore stock for all items in a cancelled order
     * Note: For items with booking dates, only date availability is restored, not general stock
     * @param orderItems the order items
     */
    private void restoreStockForOrderItems(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            try {
                String itemType = item.getItemType().toUpperCase();
                String itemId = item.getItemId();
                int quantity = item.getQuantity();
                
                // Restore date availability if booking date is set
                if (item.getBookingDate() != null) {
                    System.out.println("Restoring date availability for item " + item.getItemName() + 
                        " on date " + item.getBookingDate() + " by quantity " + quantity);
                    availabilityService.incrementAvailability(
                        itemId, 
                        item.getItemType().toLowerCase(), 
                        item.getBookingDate(), 
                        quantity
                    );
                    // Skip general stock restoration for items with booking dates
                    continue;
                }
                
                // Restore general stock only for items without booking dates
                System.out.println("Restoring general stock for item " + item.getItemName() + " by quantity " + quantity);
                
                if ("THEME".equals(itemType)) {
                    Theme theme = themeService.getThemeById(itemId);
                    if (theme != null) {
                        int currentQuantity = theme.getQuantity();
                        int newQuantity = currentQuantity + quantity;
                        System.out.println("Theme " + item.getItemName() + " stock: " + currentQuantity + " -> " + newQuantity);
                        theme.setQuantity(newQuantity);
                        theme.setUpdatedAt(LocalDateTime.now());
                        // Use updateTheme to trigger notifications if stock changes from 0 to >0
                        themeService.updateTheme(theme);
                    }
                } else if ("INVENTORY".equals(itemType)) {
                    Optional<Inventory> inventoryOpt = inventoryService.getInventoryById(itemId);
                    if (inventoryOpt.isPresent()) {
                        Inventory inventory = inventoryOpt.get();
                        int currentQuantity = inventory.getQuantity();
                        int newQuantity = currentQuantity + quantity;
                        System.out.println("Inventory " + item.getItemName() + " stock: " + currentQuantity + " -> " + newQuantity);
                        inventory.setQuantity(newQuantity);
                        inventory.setUpdatedAt(LocalDateTime.now());
                        // updateInventory will automatically notify subscribers if stock changes from 0 to >0
                        inventoryService.updateInventory(inventory);
                    }
                } else if ("PLATE".equals(itemType)) {
                    Optional<Plate> plateOpt = plateService.getPlateById(itemId);
                    if (plateOpt.isPresent()) {
                        Plate plate = plateOpt.get();
                        int currentQuantity = plate.getQuantity();
                        int newQuantity = currentQuantity + quantity;
                        System.out.println("Plate " + item.getItemName() + " stock: " + currentQuantity + " -> " + newQuantity);
                        plate.setQuantity(newQuantity);
                        plate.setUpdatedAt(LocalDateTime.now());
                        // updatePlate will automatically notify subscribers if stock changes from 0 to >0
                        plateService.updatePlate(itemId, plate);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error restoring stock for item " + item.getItemId() + ": " + e.getMessage());
            }
        }
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
