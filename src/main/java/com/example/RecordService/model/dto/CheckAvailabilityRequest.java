package com.example.RecordService.model.dto;

import java.time.LocalDate;

public class CheckAvailabilityRequest {
    private String itemId;
    private String itemType; // "theme", "inventory", "plate"
    private LocalDate date;
    private Integer quantity; // Quantity to check availability for

    // Constructors
    public CheckAvailabilityRequest() {}

    public CheckAvailabilityRequest(String itemId, String itemType, LocalDate date, Integer quantity) {
        this.itemId = itemId;
        this.itemType = itemType;
        this.date = date;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

