package com.example.RecordService.controller;

import com.example.RecordService.entity.Inventory;
import com.example.RecordService.service.InventoryService;
import com.example.RecordService.service.BusinessService;
import com.example.RecordService.service.UserService;
import com.example.RecordService.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private BusinessService businessService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private com.example.RecordService.service.AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<?> createInventory(
            @RequestBody Inventory inventory,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Validate required fields
            if (inventory.getBusinessId() == null || inventory.getBusinessId().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (inventory.getInventoryName() == null || inventory.getInventoryName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (inventory.getInventoryDescription() == null || inventory.getInventoryDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (inventory.getInventoryCategory() == null || inventory.getInventoryCategory().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (inventory.getPrice() < 0) {
                return ResponseEntity.badRequest().build();
            }
            if (inventory.getQuantity() < 0) {
                return ResponseEntity.badRequest().build();
            }

            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can create inventory for any business
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Only vendors or super admins can create inventory."));
                    }
                    
                    // Verify vendor owns the business
                    com.example.RecordService.model.Business business = businessService.getBusinessById(inventory.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(inventory.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Map.of("error", "You can only create inventory for your own businesses."));
                        }
                    }
                }
            }

            // Save the inventory
            Inventory savedInventory = inventoryService.saveInventory(inventory);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedInventory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * POST endpoint to create a new inventory with image validation
     * This endpoint requires that at least one image is uploaded for the inventory
     * @param inventory the inventory details to be saved
     * @return ResponseEntity with the created inventory and HTTP status
     */
    @PostMapping("/with-images")
    public ResponseEntity<?> createInventoryWithImages(
            @RequestBody Inventory inventory,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Validate required fields
            if (inventory.getBusinessId() == null || inventory.getBusinessId().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (inventory.getInventoryName() == null || inventory.getInventoryName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (inventory.getInventoryDescription() == null || inventory.getInventoryDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (inventory.getInventoryCategory() == null || inventory.getInventoryCategory().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (inventory.getPrice() < 0) {
                return ResponseEntity.badRequest().build();
            }
            
            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can create inventory for any business
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Only vendors or super admins can create inventory."));
                    }
                    
                    // Verify vendor owns the business
                    com.example.RecordService.model.Business business = businessService.getBusinessById(inventory.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(inventory.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Map.of("error", "You can only create inventory for your own businesses."));
                        }
                    }
                }
            }
            if (inventory.getQuantity() < 0) {
                return ResponseEntity.badRequest().build();
            }

            // Save the inventory first
            Inventory savedInventory = inventoryService.saveInventory(inventory);
            
            // Check if inventory has images - this will be validated by the frontend
            // The frontend should ensure images are uploaded before calling this endpoint
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedInventory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable String inventoryId) {
        Optional<Inventory> inventory = inventoryService.getInventoryById(inventoryId);
        return inventory.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventories() {
        List<Inventory> inventories = inventoryService.getAllInventories();
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<Inventory>> getInventoriesByBusinessId(@PathVariable String businessId) {
        List<Inventory> inventories = inventoryService.getInventoriesByBusinessId(businessId);
        return ResponseEntity.ok(inventories);
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<?> updateInventory(
            @PathVariable String inventoryId,
            @RequestBody Inventory inventory,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Get existing inventory to verify ownership
            Optional<Inventory> existingInventoryOpt = inventoryService.getInventoryById(inventoryId);
            if (!existingInventoryOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            Inventory existingInventory = existingInventoryOpt.get();
            
            // Validate vendor ownership if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can update any inventory
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Only vendors or super admins can update product details."));
                    }
                    
                    // Then verify ownership
                    com.example.RecordService.model.Business business = businessService.getBusinessById(existingInventory.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        // Check if vendor owns the business through any of their businesses
                        List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(existingInventory.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Map.of("error", "You can only update your own products."));
                        }
                    }
                }
            } else {
                // If no vendor phone provided but trying to update, deny access
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only vendors or super admins can update product details."));
            }
            
            inventory.setInventoryId(inventoryId);
            Inventory updatedInventory = inventoryService.updateInventory(inventory);
            return ResponseEntity.ok(updatedInventory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(
            @PathVariable String inventoryId,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        // Get existing inventory to verify ownership
        Optional<Inventory> existingInventoryOpt = inventoryService.getInventoryById(inventoryId);
        if (!existingInventoryOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Inventory existingInventory = existingInventoryOpt.get();
        
        // Validate vendor ownership if vendor phone is provided
        if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
            // Super admin can delete any inventory
            if (!authorizationService.isSuperAdmin(vendorPhone)) {
                if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                
                // Then verify ownership
                com.example.RecordService.model.Business business = businessService.getBusinessById(existingInventory.getBusinessId());
                if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                    // Check if vendor owns the business through any of their businesses
                    List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                    boolean ownsBusiness = vendorBusinesses.stream()
                            .anyMatch(b -> b.getBusinessId().equals(existingInventory.getBusinessId()));
                    if (!ownsBusiness) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                }
            }
        }
        
        boolean deleted = inventoryService.deleteInventory(inventoryId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getInventoryCount() {
        long count = inventoryService.getInventoryCount();
        return ResponseEntity.ok(count);
    }
}
