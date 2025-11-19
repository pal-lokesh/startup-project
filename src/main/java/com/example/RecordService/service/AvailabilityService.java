package com.example.RecordService.service;

import com.example.RecordService.entity.Availability;
import com.example.RecordService.model.dto.AvailabilityRequest;
import com.example.RecordService.model.dto.AvailabilityResponse;
import com.example.RecordService.model.dto.CheckAvailabilityRequest;
import com.example.RecordService.repository.AvailabilityRepository;
import com.example.RecordService.repository.OrderRepository;
import com.example.RecordService.service.StockNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvailabilityService {
    
    @Autowired
    private AvailabilityRepository availabilityRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private StockNotificationService stockNotificationService;
    
    @Autowired
    private com.example.RecordService.service.PlateService plateService;
    
    @Autowired
    private com.example.RecordService.service.ThemeService themeService;
    
    @Autowired
    private com.example.RecordService.service.InventoryService inventoryService;
    
    /**
     * Create or update availability for an item on a specific date
     */
    public AvailabilityResponse createOrUpdateAvailability(AvailabilityRequest request) {
        // Check if availability already exists for this item and date
        Optional<Availability> existing = availabilityRepository.findByItemIdAndItemTypeAndAvailabilityDate(
            request.getItemId(), request.getItemType(), request.getAvailabilityDate());
        
        Availability availability;
        if (existing.isPresent()) {
            // Update existing availability
            availability = existing.get();
            availability.setAvailableQuantity(request.getAvailableQuantity());
            availability.setIsAvailable(request.getIsAvailable());
            if (request.getPriceOverride() != null) {
                availability.setPriceOverride(request.getPriceOverride());
            }
        } else {
            // Create new availability
            availability = new Availability();
            availability.setItemId(request.getItemId());
            availability.setItemType(request.getItemType());
            availability.setBusinessId(request.getBusinessId());
            availability.setAvailabilityDate(request.getAvailabilityDate());
            availability.setAvailableQuantity(request.getAvailableQuantity());
            availability.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
            availability.setPriceOverride(request.getPriceOverride());
        }
        
        Availability saved = availabilityRepository.save(availability);
        
        // Notify subscribers when availability is added for a date that was previously unavailable
        // This handles the case where vendors add date-wise availability for plates/themes/inventory
        if (saved.getIsAvailable() && saved.getAvailableQuantity() > 0) {
            // Check if this date was previously unavailable
            boolean wasUnavailableForDate = false;
            if (existing.isPresent()) {
                // If updating, check if previous quantity was 0 or not available
                Availability prev = existing.get();
                wasUnavailableForDate = !prev.getIsAvailable() || prev.getAvailableQuantity() == 0;
                System.out.println("Updating availability for " + request.getItemId() + " (" + request.getItemType() + ") on " + 
                    request.getAvailabilityDate() + ": Previous quantity=" + prev.getAvailableQuantity() + 
                    ", Previous isAvailable=" + prev.getIsAvailable() + ", wasUnavailable=" + wasUnavailableForDate);
            } else {
                // If creating new availability, this date was previously unavailable (no availability record)
                wasUnavailableForDate = true;
                System.out.println("Creating new availability for " + request.getItemId() + " (" + request.getItemType() + ") on " + 
                    request.getAvailabilityDate() + ": wasUnavailable=true (new record)");
            }
            
            // Notify subscribers if this date was previously unavailable and now has availability
            // We notify regardless of general stock because clients subscribed when the date was unavailable
            if (wasUnavailableForDate) {
                try {
                    String itemName = getItemName(request.getItemId(), request.getItemType());
                    System.out.println("Attempting to notify subscribers for " + request.getItemId() + " (" + request.getItemType() + 
                        "): itemName=" + itemName);
                    if (itemName != null) {
                        // Convert LocalDate to String for notification
                        String dateString = request.getAvailabilityDate() != null ? request.getAvailabilityDate().toString() : null;
                        stockNotificationService.notifySubscribers(
                            request.getItemId(),
                            request.getItemType().toUpperCase(),
                            itemName,
                            dateString // Pass the availability date as string
                        );
                        System.out.println("Successfully triggered notification for " + request.getItemId() + " (" + request.getItemType() + ") for date " + dateString);
                    } else {
                        System.out.println("Cannot notify: itemName is null for " + request.getItemId() + " (" + request.getItemType() + ")");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to notify subscribers for item " + request.getItemId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Not notifying: date was not previously unavailable for " + request.getItemId() + " (" + request.getItemType() + ")");
            }
        } else {
            System.out.println("Not notifying: availability not available or quantity is 0 for " + request.getItemId() + 
                " (" + request.getItemType() + "): isAvailable=" + saved.getIsAvailable() + ", quantity=" + saved.getAvailableQuantity());
        }
        
        return convertToResponse(saved);
    }
    
    /**
     * Check if item has stock (for general stock-based notifications)
     */
    private boolean checkItemHasStock(String itemId, String itemType) {
        try {
            String type = itemType.toLowerCase();
            if ("plate".equals(type)) {
                Optional<com.example.RecordService.entity.Plate> plate = plateService.getPlateById(itemId);
                return plate.isPresent() && plate.get().getQuantity() > 0;
            } else if ("theme".equals(type)) {
                com.example.RecordService.model.Theme theme = themeService.getThemeById(itemId);
                return theme != null && theme.getQuantity() > 0;
            } else if ("inventory".equals(type)) {
                Optional<com.example.RecordService.entity.Inventory> inventory = inventoryService.getInventoryById(itemId);
                return inventory.isPresent() && inventory.get().getQuantity() > 0;
            }
        } catch (Exception e) {
            System.err.println("Error checking item stock for " + itemId + ": " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get item name for notification purposes
     */
    private String getItemName(String itemId, String itemType) {
        try {
            String type = itemType.toLowerCase();
            if ("plate".equals(type)) {
                Optional<com.example.RecordService.entity.Plate> plate = plateService.getPlateById(itemId);
                return plate.map(com.example.RecordService.entity.Plate::getDishName).orElse(null);
            } else if ("theme".equals(type)) {
                com.example.RecordService.model.Theme theme = themeService.getThemeById(itemId);
                return theme != null ? theme.getThemeName() : null;
            } else if ("inventory".equals(type)) {
                Optional<com.example.RecordService.entity.Inventory> inventory = inventoryService.getInventoryById(itemId);
                return inventory.map(com.example.RecordService.entity.Inventory::getInventoryName).orElse(null);
            }
        } catch (Exception e) {
            System.err.println("Error getting item name for " + itemId + ": " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get availability for an item on a specific date
     */
    public Optional<AvailabilityResponse> getAvailability(String itemId, String itemType, LocalDate date) {
        return availabilityRepository.findByItemIdAndItemTypeAndAvailabilityDate(itemId, itemType, date)
            .map(this::convertToResponse);
    }
    
    /**
     * Get all availabilities for an item
     */
    public List<AvailabilityResponse> getAvailabilitiesForItem(String itemId, String itemType) {
        return availabilityRepository.findByItemIdAndItemTypeOrderByAvailabilityDateAsc(itemId, itemType)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get availabilities for an item within a date range
     */
    public List<AvailabilityResponse> getAvailabilitiesForItemInRange(
            String itemId, String itemType, LocalDate startDate, LocalDate endDate) {
        return availabilityRepository.findByItemIdAndItemTypeAndDateRange(itemId, itemType, startDate, endDate)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all availabilities for a business
     */
    public List<AvailabilityResponse> getAvailabilitiesForBusiness(String businessId) {
        return availabilityRepository.findByBusinessIdOrderByAvailabilityDateAsc(businessId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Check if an item is available on a specific date with sufficient quantity
     */
    public boolean checkAvailability(CheckAvailabilityRequest request) {
        Optional<Availability> availability = availabilityRepository.checkAvailability(
            request.getItemId(),
            request.getItemType(),
            request.getDate(),
            request.getQuantity() != null ? request.getQuantity() : 1
        );
        return availability.isPresent();
    }
    
    /**
     * Get available quantity for an item on a specific date
     * This calculates: (Availability Quantity) - (Already Booked Quantity from Confirmed Orders)
     */
    public Integer getAvailableQuantity(String itemId, String itemType, LocalDate date) {
        Optional<Availability> availability = availabilityRepository.findByItemIdAndItemTypeAndAvailabilityDate(
            itemId, itemType, date);
        
        if (!availability.isPresent() || !availability.get().getIsAvailable()) {
            return 0;
        }
        
        // Get total booked quantity from confirmed orders
        Integer bookedQuantity = orderRepository.countBookedQuantityForItemAndDate(
            itemId, itemType.toLowerCase(), date);
        
        if (bookedQuantity == null) {
            bookedQuantity = 0;
        }
        
        // Calculate available quantity: availability - booked
        int availableQuantity = availability.get().getAvailableQuantity() - bookedQuantity;
        
        // Return 0 if negative (shouldn't happen, but safety check)
        return Math.max(0, availableQuantity);
    }
    
    /**
     * Delete availability for an item on a specific date
     */
    public void deleteAvailability(String itemId, String itemType, LocalDate date) {
        Optional<Availability> availability = availabilityRepository.findByItemIdAndItemTypeAndAvailabilityDate(
            itemId, itemType, date);
        availability.ifPresent(availabilityRepository::delete);
    }
    
    /**
     * Delete all availabilities for an item
     */
    public void deleteAllAvailabilitiesForItem(String itemId, String itemType) {
        availabilityRepository.deleteByItemIdAndItemType(itemId, itemType);
    }
    
    /**
     * Decrement available quantity when an item is booked
     */
    public void decrementAvailability(String itemId, String itemType, LocalDate date, Integer quantity) {
        Optional<Availability> availabilityOpt = availabilityRepository.findByItemIdAndItemTypeAndAvailabilityDate(
            itemId, itemType, date);
        
        if (availabilityOpt.isPresent()) {
            Availability availability = availabilityOpt.get();
            int newQuantity = availability.getAvailableQuantity() - quantity;
            if (newQuantity < 0) {
                newQuantity = 0;
            }
            availability.setAvailableQuantity(newQuantity);
            if (newQuantity == 0) {
                availability.setIsAvailable(false);
            }
            availabilityRepository.save(availability);
        }
    }
    
    /**
     * Increment available quantity when an order is cancelled
     */
    public void incrementAvailability(String itemId, String itemType, LocalDate date, Integer quantity) {
        Optional<Availability> availabilityOpt = availabilityRepository.findByItemIdAndItemTypeAndAvailabilityDate(
            itemId, itemType, date);
        
        if (availabilityOpt.isPresent()) {
            Availability availability = availabilityOpt.get();
            int previousQuantity = availability.getAvailableQuantity();
            boolean wasUnavailable = !availability.getIsAvailable() || previousQuantity == 0;
            
            int newQuantity = availability.getAvailableQuantity() + quantity;
            availability.setAvailableQuantity(newQuantity);
            if (newQuantity > 0) {
                availability.setIsAvailable(true);
            }
            availabilityRepository.save(availability);
            
            // Notify subscribers if item was unavailable and now has availability
            if (wasUnavailable && newQuantity > 0 && availability.getIsAvailable()) {
                try {
                    String itemName = getItemName(itemId, itemType);
                    if (itemName != null) {
                        stockNotificationService.notifySubscribers(
                            itemId,
                            itemType.toUpperCase(),
                            itemName
                        );
                    }
                } catch (Exception e) {
                    System.err.println("Failed to notify subscribers for item " + itemId + " when incrementing availability: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Convert Availability entity to AvailabilityResponse DTO
     */
    private AvailabilityResponse convertToResponse(Availability availability) {
        AvailabilityResponse response = new AvailabilityResponse();
        response.setAvailabilityId(availability.getAvailabilityId());
        response.setItemId(availability.getItemId());
        response.setItemType(availability.getItemType());
        response.setBusinessId(availability.getBusinessId());
        response.setAvailabilityDate(availability.getAvailabilityDate());
        response.setAvailableQuantity(availability.getAvailableQuantity());
        response.setIsAvailable(availability.getIsAvailable());
        response.setPriceOverride(availability.getPriceOverride());
        return response;
    }
}

