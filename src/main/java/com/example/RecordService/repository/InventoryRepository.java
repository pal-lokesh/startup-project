package com.example.RecordService.repository;

import com.example.RecordService.entity.Inventory;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    Inventory save(Inventory inventory);
    Optional<Inventory> findById(String inventoryId);
    List<Inventory> findAll();
    List<Inventory> findByBusinessId(String businessId);
    Inventory update(Inventory inventory);
    boolean delete(String inventoryId);
    long count();
}
