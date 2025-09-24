package com.example.RecordService.repository.impl;

import com.example.RecordService.entity.Inventory;
import com.example.RecordService.repository.InventoryRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InventoryRepositoryImpl implements InventoryRepository {
    private final Map<String, Inventory> inventories = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Inventory save(Inventory inventory) {
        if (inventory.getInventoryId() == null) {
            inventory.setInventoryId("INVENTORY_" + idCounter.getAndIncrement());
        }
        inventory.setUpdatedAt(java.time.LocalDateTime.now());
        inventories.put(inventory.getInventoryId(), inventory);
        return inventory;
    }

    @Override
    public Optional<Inventory> findById(String inventoryId) {
        return Optional.ofNullable(inventories.get(inventoryId));
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
    public Inventory update(Inventory inventory) {
        inventory.setUpdatedAt(java.time.LocalDateTime.now());
        inventories.put(inventory.getInventoryId(), inventory);
        return inventory;
    }

    @Override
    public boolean delete(String inventoryId) {
        Inventory inventory = inventories.remove(inventoryId);
        return inventory != null;
    }

    @Override
    public long count() {
        return inventories.size();
    }
}
