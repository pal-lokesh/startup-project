package com.example.RecordService.repository;

import com.example.RecordService.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find all orders by user ID
     * @param userId the user ID
     * @return list of orders for the user
     */
    List<Order> findByUserIdOrderByOrderDateDesc(String userId);
    
    /**
     * Find all orders by status
     * @param status the order status
     * @return list of orders with the specified status
     */
    List<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status);
    
    /**
     * Find orders by business ID (through order items)
     * @param businessId the business ID
     * @return list of orders containing items from the business
     */
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.businessId = :businessId ORDER BY o.orderDate DESC")
    List<Order> findByBusinessId(@Param("businessId") String businessId);
    
    /**
     * Find orders by date range
     * @param startDate start date
     * @param endDate end date
     * @return list of orders within the date range
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    List<Order> findByOrderDateBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                     @Param("endDate") java.time.LocalDateTime endDate);
    
    /**
     * Count orders by status
     * @param status the order status
     * @return count of orders with the specified status
     */
    long countByStatus(Order.OrderStatus status);
    
    /**
     * Find orders by customer email
     * @param customerEmail the customer email
     * @return list of orders for the customer
     */
    List<Order> findByCustomerEmailOrderByOrderDateDesc(String customerEmail);
    
    /**
     * Find order by order ID
     * @param orderId the order ID
     * @return the order if found
     */
    Optional<Order> findByOrderId(Long orderId);
    
    /**
     * Count total booked quantity for an item on a specific date from confirmed orders
     * @param itemId the item ID
     * @param itemType the item type
     * @param bookingDate the booking date
     * @return total booked quantity
     */
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM Order o JOIN o.orderItems oi " +
           "WHERE oi.itemId = :itemId AND oi.itemType = :itemType AND oi.bookingDate = :bookingDate " +
           "AND o.status IN ('CONFIRMED', 'PREPARING', 'READY', 'SHIPPED')")
    Integer countBookedQuantityForItemAndDate(
        @Param("itemId") String itemId,
        @Param("itemType") String itemType,
        @Param("bookingDate") java.time.LocalDate bookingDate);
}
