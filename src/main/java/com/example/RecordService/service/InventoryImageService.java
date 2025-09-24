package com.example.RecordService.service;

import com.example.RecordService.entity.InventoryImage;
import com.example.RecordService.repository.InventoryImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryImageService {
    
    @Autowired
    private InventoryImageRepository inventoryImageRepository;

    public InventoryImage saveInventoryImage(InventoryImage inventoryImage) {
        return inventoryImageRepository.save(inventoryImage);
    }

    public Optional<InventoryImage> getInventoryImageById(String imageId) {
        return inventoryImageRepository.findById(imageId);
    }

    public List<InventoryImage> getAllInventoryImages() {
        return inventoryImageRepository.findAll();
    }

    public List<InventoryImage> getInventoryImagesByInventoryId(String inventoryId) {
        return inventoryImageRepository.findByInventoryId(inventoryId);
    }

    public InventoryImage updateInventoryImage(InventoryImage inventoryImage) {
        return inventoryImageRepository.update(inventoryImage);
    }

    public boolean deleteInventoryImage(String imageId) {
        return inventoryImageRepository.delete(imageId);
    }

    public long getInventoryImageCount() {
        return inventoryImageRepository.count();
    }

    public void setPrimaryInventoryImage(String inventoryId, String imageId) {
        inventoryImageRepository.setPrimaryImage(inventoryId, imageId);
    }
}
