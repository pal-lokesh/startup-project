package com.example.RecordService.controller;

import com.example.RecordService.entity.Dish;
import com.example.RecordService.service.AuthorizationService;
import com.example.RecordService.service.BusinessService;
import com.example.RecordService.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dishes")
@CrossOrigin(origins = "http://localhost:3000")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes() {
        try {
            return ResponseEntity.ok(dishService.getAllDishes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{dishId}")
    public ResponseEntity<Dish> getDishById(@PathVariable String dishId) {
        try {
            Optional<Dish> dish = dishService.getDishById(dishId);
            return dish.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<Dish>> getDishesByBusiness(@PathVariable String businessId) {
        try {
            List<Dish> dishes = dishService.getDishesByBusinessId(businessId);
            return ResponseEntity.ok(dishes != null ? dishes : new java.util.ArrayList<>());
        } catch (Exception e) {
            System.err.println("Error fetching dishes for business " + businessId + ": " + e.getMessage());
            e.printStackTrace();
            // Return empty list instead of error to prevent frontend crashes
            return ResponseEntity.ok(new java.util.ArrayList<>());
        }
    }

    @PostMapping
    public ResponseEntity<?> createDish(@RequestBody Dish dish, @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            if (dish.getBusinessId() == null || dish.getBusinessId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Business ID is required");
            }
            if (dish.getDishName() == null || dish.getDishName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Dish name is required");
            }
            if (dish.getDishDescription() == null || dish.getDishDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Dish description is required");
            }
            if (dish.getPrice() == null || dish.getPrice() <= 0) {
                return ResponseEntity.badRequest().body("Price must be greater than 0");
            }

            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }

                    var business = businessService.getBusinessById(dish.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        var vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(dish.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                        }
                    }
                }
            }

            // Note: Image is not required on creation - can be added via update
            // if (dish.getDishImage() == null || dish.getDishImage().trim().isEmpty()) {
            //     return ResponseEntity.badRequest().body("Dish image is required");
            // }

            Dish createdDish = dishService.createDish(dish);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDish);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{dishId}")
    public ResponseEntity<?> updateDish(@PathVariable String dishId,
                                        @RequestBody Dish dishDetails,
                                        @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }

                    var business = businessService.getBusinessById(dishDetails.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        var vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(dishDetails.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                        }
                    }
                }
            }

            Dish updatedDish = dishService.updateDish(dishId, dishDetails);
            return ResponseEntity.ok(updatedDish);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{dishId}")
    public ResponseEntity<Void> deleteDish(@PathVariable String dishId,
                                           @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                }
            }

            dishService.deleteDish(dishId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

