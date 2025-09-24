package com.example.RecordService.repository.impl;

import com.example.RecordService.entity.InventoryImage;
import com.example.RecordService.repository.InventoryImageRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InventoryImageRepositoryImpl implements InventoryImageRepository {
    private final Map<String, InventoryImage> inventoryImages = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public InventoryImage save(InventoryImage inventoryImage) {
        if (inventoryImage.getImageId() == null) {
            inventoryImage.setImageId("INVENTORY_IMAGE_" + idCounter.getAndIncrement());
        }
        inventoryImages.put(inventoryImage.getImageId(), inventoryImage);
        return inventoryImage;
    }

    @Override
    public Optional<InventoryImage> findById(String imageId) {
        return Optional.ofNullable(inventoryImages.get(imageId));
    }

    @Override
    public List<InventoryImage> findAll() {
        return new ArrayList<>(inventoryImages.values());
    }

    @Override
    public List<InventoryImage> findByInventoryId(String inventoryId) {
        return inventoryImages.values().stream()
                .filter(image -> image.getInventoryId().equals(inventoryId))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public InventoryImage update(InventoryImage inventoryImage) {
        inventoryImages.put(inventoryImage.getImageId(), inventoryImage);
        return inventoryImage;
    }

    @Override
    public boolean delete(String imageId) {
        InventoryImage inventoryImage = inventoryImages.remove(imageId);
        return inventoryImage != null;
    }

    @Override
    public long count() {
        return inventoryImages.size();
    }

    @Override
    public void setPrimaryImage(String inventoryId, String imageId) {
        // First, set all images for this inventory to non-primary
        inventoryImages.values().stream()
                .filter(image -> image.getInventoryId().equals(inventoryId))
                .forEach(image -> image.setPrimary(false));
        
        // Then set the specified image as primary
        Optional<InventoryImage> targetImage = inventoryImages.values().stream()
                .filter(image -> image.getImageId().equals(imageId))
                .findFirst();
        
        targetImage.ifPresent(image -> image.setPrimary(true));
    }
}
