package com.example.RecordService.controller;

import com.example.RecordService.entity.InventoryImage;
import com.example.RecordService.entity.Inventory;
import com.example.RecordService.model.User;
import com.example.RecordService.service.InventoryImageService;
import com.example.RecordService.service.InventoryService;
import com.example.RecordService.service.BusinessService;
import com.example.RecordService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory/images")
@CrossOrigin(origins = "*")
public class InventoryImageController {

    @Autowired
    private InventoryImageService inventoryImageService;
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private BusinessService businessService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private com.example.RecordService.service.AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<?> createInventoryImage(
            @RequestBody InventoryImage inventoryImage,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
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

            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can upload images for any inventory
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Only vendors or super admins can upload images."));
                    }
                    
                    // Get inventory to verify ownership
                    Optional<Inventory> inventoryOpt = inventoryService.getInventoryById(inventoryImage.getInventoryId());
                    if (!inventoryOpt.isPresent()) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "Inventory not found."));
                    }
                    Inventory inventory = inventoryOpt.get();
                    
                    // Verify vendor owns the business
                    com.example.RecordService.model.Business business = businessService.getBusinessById(inventory.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        // Check if vendor owns the business through any of their businesses
                        List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(inventory.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Map.of("error", "You can only upload images for your own products."));
                        }
                    }
                }
            } else {
                // If no vendor phone provided but trying to create, deny access
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only vendors or super admins can upload images."));
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
    public ResponseEntity<?> updateInventoryImage(
            @PathVariable String imageId, 
            @RequestBody InventoryImage inventoryImage,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Get existing image to get inventoryId
            Optional<InventoryImage> existingImageOpt = inventoryImageService.getInventoryImageById(imageId);
            if (!existingImageOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            InventoryImage existingImage = existingImageOpt.get();
            
            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can update images for any inventory
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Only vendors or super admins can update images."));
                    }
                    
                    // Get inventory to verify ownership
                Optional<Inventory> inventoryOpt = inventoryService.getInventoryById(existingImage.getInventoryId());
                if (!inventoryOpt.isPresent()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Inventory not found."));
                }
                Inventory inventory = inventoryOpt.get();
                
                // Verify vendor owns the business
                com.example.RecordService.model.Business business = businessService.getBusinessById(inventory.getBusinessId());
                if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                    // Check if vendor owns the business through any of their businesses
                    List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                    boolean ownsBusiness = vendorBusinesses.stream()
                            .anyMatch(b -> b.getBusinessId().equals(inventory.getBusinessId()));
                    if (!ownsBusiness) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "You can only update images for your own products."));
                    }
                }
                }
            } else {
                // If no vendor phone provided but trying to update, deny access
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Only vendors or super admins can update images."));
            }
            
            inventoryImage.setImageId(imageId);
            InventoryImage updatedImage = inventoryImageService.updateInventoryImage(inventoryImage);
            return ResponseEntity.ok(updatedImage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteInventoryImage(
            @PathVariable String imageId,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Get existing image to get inventoryId
            Optional<InventoryImage> existingImageOpt = inventoryImageService.getInventoryImageById(imageId);
            if (!existingImageOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            InventoryImage existingImage = existingImageOpt.get();
            
            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // First, verify user is a VENDOR, not a CLIENT
                User user = userService.getUserByPhoneNumber(vendorPhone);
                if (user == null || user.getUserType() != User.UserType.VENDOR) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Only vendors or super admins can delete images."));
                }
                
                // Get inventory to verify ownership
                Optional<Inventory> inventoryOpt = inventoryService.getInventoryById(existingImage.getInventoryId());
                if (!inventoryOpt.isPresent()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Inventory not found."));
                }
                Inventory inventory = inventoryOpt.get();
                
                // Verify vendor owns the business
                com.example.RecordService.model.Business business = businessService.getBusinessById(inventory.getBusinessId());
                if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                    // Check if vendor owns the business through any of their businesses
                    List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                    boolean ownsBusiness = vendorBusinesses.stream()
                            .anyMatch(b -> b.getBusinessId().equals(inventory.getBusinessId()));
                    if (!ownsBusiness) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "You can only delete images for your own products."));
                    }
                }
            } else {
                // If no vendor phone provided but trying to delete, deny access
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only vendors can delete images."));
            }
            
            boolean deleted = inventoryImageService.deleteInventoryImage(imageId);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getInventoryImageCount() {
        long count = inventoryImageService.getInventoryImageCount();
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{imageId}/primary")
    public ResponseEntity<?> setPrimaryInventoryImage(
            @PathVariable String imageId,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            Optional<InventoryImage> imageOpt = inventoryImageService.getInventoryImageById(imageId);
            if (!imageOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            InventoryImage existingImage = imageOpt.get();
            
            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // First, verify user is a VENDOR, not a CLIENT
                User user = userService.getUserByPhoneNumber(vendorPhone);
                if (user == null || user.getUserType() != User.UserType.VENDOR) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Only vendors or super admins can set primary images."));
                }
                
                // Get inventory to verify ownership
                Optional<Inventory> inventoryOpt = inventoryService.getInventoryById(existingImage.getInventoryId());
                if (!inventoryOpt.isPresent()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Inventory not found."));
                }
                Inventory inventory = inventoryOpt.get();
                
                // Verify vendor owns the business
                com.example.RecordService.model.Business business = businessService.getBusinessById(inventory.getBusinessId());
                if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                    // Check if vendor owns the business through any of their businesses
                    List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                    boolean ownsBusiness = vendorBusinesses.stream()
                            .anyMatch(b -> b.getBusinessId().equals(inventory.getBusinessId()));
                    if (!ownsBusiness) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "You can only set primary images for your own products."));
                    }
                }
            } else {
                // If no vendor phone provided but trying to set primary, deny access
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only vendors can set primary images."));
            }
            
            inventoryImageService.setPrimaryInventoryImage(existingImage.getInventoryId(), imageId);
            return ResponseEntity.ok(existingImage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
