package com.example.RecordService.service;

import com.example.RecordService.entity.Inventory;
import com.example.RecordService.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private com.example.RecordService.service.StockNotificationService stockNotificationService;

    public Inventory saveInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Optional<Inventory> getInventoryById(String inventoryId) {
        Inventory inventory = inventoryRepository.findByInventoryId(inventoryId);
        return Optional.ofNullable(inventory);
    }

    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    public List<Inventory> getInventoriesByBusinessId(String businessId) {
        return inventoryRepository.findByBusinessId(businessId);
    }

    public Inventory updateInventory(Inventory inventory) {
        // Get existing inventory to check if stock changed from 0 to >0
        Inventory existingInventory = inventoryRepository.findByInventoryId(inventory.getInventoryId());
        int previousQuantity = existingInventory != null ? existingInventory.getQuantity() : 0;
        
        Inventory updatedInventory = inventoryRepository.update(inventory);
        
        // If item was out of stock and now has stock, notify subscribers
        if (previousQuantity == 0 && updatedInventory.getQuantity() > 0) {
            try {
                stockNotificationService.notifySubscribers(
                    updatedInventory.getInventoryId(), 
                    "INVENTORY", 
                    updatedInventory.getInventoryName()
                );
            } catch (Exception e) {
                System.err.println("Failed to notify subscribers for inventory " + updatedInventory.getInventoryId() + ": " + e.getMessage());
            }
        }
        
        return updatedInventory;
    }

    public boolean deleteInventory(String inventoryId) {
        return inventoryRepository.delete(inventoryId);
    }

    public long getInventoryCount() {
        return inventoryRepository.count();
    }
}
