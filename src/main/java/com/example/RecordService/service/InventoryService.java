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
        return inventoryRepository.update(inventory);
    }

    public boolean deleteInventory(String inventoryId) {
        return inventoryRepository.delete(inventoryId);
    }

    public long getInventoryCount() {
        return inventoryRepository.count();
    }
}
