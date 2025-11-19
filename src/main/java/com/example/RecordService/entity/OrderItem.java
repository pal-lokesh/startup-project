package com.example.RecordService.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(name = "item_id", nullable = false)
    private String itemId;
    
    @Column(name = "item_name", nullable = false)
    private String itemName;
    
    @Column(name = "item_price", nullable = false)
    private Double itemPrice;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "item_type", nullable = false)
    private String itemType; // "theme", "inventory", "plate"
    
    @Column(name = "business_id", nullable = false)
    private String businessId;
    
    @Column(name = "business_name", nullable = false)
    private String businessName;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "booking_date")
    private java.time.LocalDate bookingDate; // Date for which the item is booked
    
    @Column(name = "selected_dishes", columnDefinition = "TEXT")
    private String selectedDishes; // JSON string storing selected dishes for plates
    
    // Constructors
    public OrderItem() {}
    
    public OrderItem(Order order, String itemId, String itemName, Double itemPrice, 
                    Integer quantity, String itemType, String businessId, String businessName) {
        this.order = order;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        this.itemType = itemType;
        this.businessId = businessId;
        this.businessName = businessName;
    }
    
    public OrderItem(Order order, String itemId, String itemName, Double itemPrice, 
                    Integer quantity, String itemType, String businessId, String businessName,
                    java.time.LocalDate bookingDate) {
        this(order, itemId, itemName, itemPrice, quantity, itemType, businessId, businessName);
        this.bookingDate = bookingDate;
    }
    
    // Getters and Setters
    public Long getOrderItemId() {
        return orderItemId;
    }
    
    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public Double getItemPrice() {
        return itemPrice;
    }
    
    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getItemType() {
        return itemType;
    }
    
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    
    public String getBusinessId() {
        return businessId;
    }
    
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
    
    public String getBusinessName() {
        return businessName;
    }
    
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public java.time.LocalDate getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(java.time.LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    public String getSelectedDishes() {
        return selectedDishes;
    }
    
    public void setSelectedDishes(String selectedDishes) {
        this.selectedDishes = selectedDishes;
    }
    
    // Helper method to calculate total price for this item
    public Double getTotalPrice() {
        return itemPrice * quantity;
    }
}
