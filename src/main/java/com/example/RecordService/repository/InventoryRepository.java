package com.example.RecordService.repository;

import com.example.RecordService.entity.Inventory;
import java.util.List;

public interface InventoryRepository {
    Inventory save(Inventory inventory);
    Inventory findByInventoryId(String inventoryId);
    List<Inventory> findAll();
    List<Inventory> findByBusinessId(String businessId);
    List<Inventory> findByCategory(String category);
    boolean existsByInventoryId(String inventoryId);
    Inventory update(Inventory inventory);
    boolean delete(String inventoryId);
    long count();
}
