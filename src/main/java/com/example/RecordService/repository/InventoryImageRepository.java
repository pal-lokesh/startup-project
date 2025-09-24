package com.example.RecordService.repository;

import com.example.RecordService.entity.InventoryImage;
import java.util.List;
import java.util.Optional;

public interface InventoryImageRepository {
    InventoryImage save(InventoryImage inventoryImage);
    Optional<InventoryImage> findById(String imageId);
    List<InventoryImage> findAll();
    List<InventoryImage> findByInventoryId(String inventoryId);
    InventoryImage update(InventoryImage inventoryImage);
    boolean delete(String imageId);
    long count();
    void setPrimaryImage(String inventoryId, String imageId);
}
