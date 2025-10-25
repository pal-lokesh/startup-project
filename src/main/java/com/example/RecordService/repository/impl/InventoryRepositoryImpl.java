package com.example.RecordService.repository.impl;

import com.example.RecordService.entity.Inventory;
import com.example.RecordService.repository.InventoryRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InventoryRepositoryImpl implements InventoryRepository {
    
    private static final Map<String, Inventory> inventories = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);
    
    @Override
    public Inventory save(Inventory inventory) {
        if (inventory.getInventoryId() == null) {
            inventory.setInventoryId("INV_" + idCounter.getAndIncrement());
        }
        inventories.put(inventory.getInventoryId(), inventory);
        return inventory;
    }
    
    @Override
    public Inventory findByInventoryId(String inventoryId) {
        return inventories.get(inventoryId);
    }
    
    @Override
    public List<Inventory> findAll() {
        return new ArrayList<>(inventories.values());
    }
    
    @Override
    public List<Inventory> findByBusinessId(String businessId) {
        return inventories.values().stream()
                .filter(inventory -> inventory.getBusinessId().equals(businessId))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<Inventory> findByCategory(String category) {
        return inventories.values().stream()
                .filter(inventory -> inventory.getInventoryCategory().equals(category))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public boolean existsByInventoryId(String inventoryId) {
        return inventories.containsKey(inventoryId);
    }
    
    @Override
    public Inventory update(Inventory inventory) {
        inventories.put(inventory.getInventoryId(), inventory);
        return inventory;
    }
    
    @Override
    public boolean delete(String inventoryId) {
        Inventory removed = inventories.remove(inventoryId);
        return removed != null;
    }
    
    @Override
    public long count() {
        return inventories.size();
    }
}