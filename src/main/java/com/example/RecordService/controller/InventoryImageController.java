package com.example.RecordService.controller;

import com.example.RecordService.entity.InventoryImage;
import com.example.RecordService.service.InventoryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory/images")
@CrossOrigin(origins = "*")
public class InventoryImageController {

    @Autowired
    private InventoryImageService inventoryImageService;

    @PostMapping
    public ResponseEntity<InventoryImage> createInventoryImage(@RequestBody InventoryImage inventoryImage) {
        try {
            System.out.println("Received inventory image request: " + inventoryImage);
            
            // Validate required fields
            if (inventoryImage.getInventoryId() == null || inventoryImage.getInventoryId().trim().isEmpty()) {
                System.out.println("Validation failed: inventoryId is null or empty");
                return ResponseEntity.badRequest().build();
            }
            if (inventoryImage.getImageName() == null || inventoryImage.getImageName().trim().isEmpty()) {
                System.out.println("Validation failed: imageName is null or empty");
                return ResponseEntity.badRequest().build();
            }
            if (inventoryImage.getImageUrl() == null || inventoryImage.getImageUrl().trim().isEmpty()) {
                System.out.println("Validation failed: imageUrl is null or empty");
                return ResponseEntity.badRequest().build();
            }

            // Save the inventory image
            InventoryImage savedImage = inventoryImageService.saveInventoryImage(inventoryImage);
            System.out.println("Successfully saved inventory image: " + savedImage.getImageId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedImage);
        } catch (Exception e) {
            System.out.println("Error saving inventory image: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<InventoryImage> getInventoryImageById(@PathVariable String imageId) {
        Optional<InventoryImage> image = inventoryImageService.getInventoryImageById(imageId);
        return image.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<InventoryImage>> getAllInventoryImages() {
        List<InventoryImage> images = inventoryImageService.getAllInventoryImages();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/inventory/{inventoryId}")
    public ResponseEntity<List<InventoryImage>> getInventoryImagesByInventoryId(@PathVariable String inventoryId) {
        List<InventoryImage> images = inventoryImageService.getInventoryImagesByInventoryId(inventoryId);
        return ResponseEntity.ok(images);
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<InventoryImage> updateInventoryImage(@PathVariable String imageId, @RequestBody InventoryImage inventoryImage) {
        try {
            inventoryImage.setImageId(imageId);
            InventoryImage updatedImage = inventoryImageService.updateInventoryImage(inventoryImage);
            return ResponseEntity.ok(updatedImage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteInventoryImage(@PathVariable String imageId) {
        boolean deleted = inventoryImageService.deleteInventoryImage(imageId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getInventoryImageCount() {
        long count = inventoryImageService.getInventoryImageCount();
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{imageId}/primary")
    public ResponseEntity<InventoryImage> setPrimaryInventoryImage(@PathVariable String imageId) {
        try {
            Optional<InventoryImage> imageOpt = inventoryImageService.getInventoryImageById(imageId);
            if (imageOpt.isPresent()) {
                InventoryImage image = imageOpt.get();
                inventoryImageService.setPrimaryInventoryImage(image.getInventoryId(), imageId);
                return ResponseEntity.ok(image);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
