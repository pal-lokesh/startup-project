package com.example.RecordService.controller;

import com.example.RecordService.entity.Plate;
import com.example.RecordService.service.PlateService;
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
    public ResponseEntity<Plate> createPlate(@RequestBody Plate plate) {
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
    public ResponseEntity<Plate> createPlateWithImage(@RequestBody Plate plate) {
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
    public ResponseEntity<Plate> updatePlate(@PathVariable String plateId, @RequestBody Plate plateDetails) {
        try {
            Plate updatedPlate = plateService.updatePlate(plateId, plateDetails);
            return ResponseEntity.ok(updatedPlate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{plateId}")
    public ResponseEntity<Void> deletePlate(@PathVariable String plateId) {
        try {
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
