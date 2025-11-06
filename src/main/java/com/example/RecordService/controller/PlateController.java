package com.example.RecordService.controller;

import com.example.RecordService.entity.Plate;
import com.example.RecordService.service.PlateService;
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
@RequestMapping("/api/plates")
@CrossOrigin(origins = "http://localhost:3000")
public class PlateController {
    @Autowired
    private PlateService plateService;
    
    @Autowired
    private BusinessService businessService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private com.example.RecordService.service.AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<List<Plate>> getAllPlates() {
        try {
            List<Plate> plates = plateService.getAllPlates();
            return ResponseEntity.ok(plates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{plateId}")
    public ResponseEntity<Plate> getPlateById(@PathVariable String plateId) {
        try {
            Optional<Plate> plate = plateService.getPlateById(plateId);
            return plate.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<Plate>> getPlatesByBusinessId(@PathVariable String businessId) {
        try {
            List<Plate> plates = plateService.getPlatesByBusinessId(businessId);
            return ResponseEntity.ok(plates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createPlate(
            @RequestBody Plate plate,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Validate required fields
            if (plate.getBusinessId() == null || plate.getBusinessId().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (plate.getDishName() == null || plate.getDishName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (plate.getDishDescription() == null || plate.getDishDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (plate.getPrice() == null || plate.getPrice() <= 0) {
                return ResponseEntity.badRequest().build();
            }
            
            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can create plates for any business
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Only vendors or super admins can create plates."));
                    }
                    
                    // Verify vendor owns the business
                    com.example.RecordService.model.Business business = businessService.getBusinessById(plate.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(plate.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Map.of("error", "You can only create plates for your own businesses."));
                        }
                    }
                }
            }
            
            Plate createdPlate = plateService.createPlate(plate);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * POST endpoint to create a new plate with image validation
     * This endpoint requires that an image is uploaded for the plate
     * @param plate the plate details to be saved
     * @return ResponseEntity with the created plate and HTTP status
     */
    @PostMapping("/with-image")
    public ResponseEntity<?> createPlateWithImage(
            @RequestBody Plate plate,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Validate required fields
            if (plate.getBusinessId() == null || plate.getBusinessId().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (plate.getDishName() == null || plate.getDishName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (plate.getDishDescription() == null || plate.getDishDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (plate.getPrice() == null || plate.getPrice() <= 0) {
                return ResponseEntity.badRequest().build();
            }
            
            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can create plates for any business
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                    
                    // Verify vendor owns the business
                    com.example.RecordService.model.Business business = businessService.getBusinessById(plate.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(plate.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                        }
                    }
                }
            }
            
            // Validate that plate has an image
            if (plate.getPlateImage() == null || plate.getPlateImage().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Plate createdPlate = plateService.createPlate(plate);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{plateId}")
    public ResponseEntity<?> updatePlate(
            @PathVariable String plateId,
            @RequestBody Plate plateDetails,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Get existing plate to verify ownership
            Optional<Plate> existingPlateOpt = plateService.getPlateById(plateId);
            if (!existingPlateOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            Plate existingPlate = existingPlateOpt.get();
            
            // Validate vendor ownership if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can update any plate
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Only vendors or super admins can update product details."));
                    }
                    
                    // Then verify ownership
                    com.example.RecordService.model.Business business = businessService.getBusinessById(existingPlate.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        // Check if vendor owns the business through any of their businesses
                        List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(existingPlate.getBusinessId()));
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
            
            Plate updatedPlate = plateService.updatePlate(plateId, plateDetails);
            return ResponseEntity.ok(updatedPlate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{plateId}")
    public ResponseEntity<Void> deletePlate(
            @PathVariable String plateId,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Get existing plate to verify ownership
            Optional<Plate> existingPlateOpt = plateService.getPlateById(plateId);
            if (!existingPlateOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            Plate existingPlate = existingPlateOpt.get();
            
            // Validate vendor ownership if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can delete any plate
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                    
                    // Then verify ownership
                    com.example.RecordService.model.Business business = businessService.getBusinessById(existingPlate.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        // Check if vendor owns the business through any of their businesses
                        List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(existingPlate.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                        }
                    }
                }
            }
            
            plateService.deletePlate(plateId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getPlateCount() {
        try {
            long count = plateService.getPlateCount();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/business/{businessId}/count")
    public ResponseEntity<Map<String, Long>> getPlateCountByBusinessId(@PathVariable String businessId) {
        try {
            long count = plateService.getPlateCountByBusinessId(businessId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
