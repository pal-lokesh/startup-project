package com.example.RecordService.controller;

import com.example.RecordService.entity.Inventory;
import com.example.RecordService.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory) {
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

            // Save the inventory
            Inventory savedInventory = inventoryService.saveInventory(inventory);
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
    public ResponseEntity<Inventory> updateInventory(@PathVariable String inventoryId, @RequestBody Inventory inventory) {
        try {
            inventory.setInventoryId(inventoryId);
            Inventory updatedInventory = inventoryService.updateInventory(inventory);
            return ResponseEntity.ok(updatedInventory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable String inventoryId) {
        boolean deleted = inventoryService.deleteInventory(inventoryId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getInventoryCount() {
        long count = inventoryService.getInventoryCount();
        return ResponseEntity.ok(count);
    }
}
