package com.example.RecordService.controller;

import com.example.RecordService.model.Vendor;
import com.example.RecordService.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(origins = "*")
public class VendorController {
    
    @Autowired
    private VendorService vendorService;
    
    /**
     * POST endpoint to create a new vendor
     * @param vendor the vendor details to be saved
     * @return ResponseEntity with the created vendor and HTTP status
     */
    @PostMapping
    public ResponseEntity<Vendor> createVendor(@RequestBody Vendor vendor) {
        try {
            // Validate required fields
            if (vendor.getPhoneNumber() == null || vendor.getPhoneNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (vendor.getBusinessName() == null || vendor.getBusinessName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (vendor.getBusinessEmail() == null || vendor.getBusinessEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Check if vendor already exists with this phone number
            if (vendorService.vendorExistsByPhoneNumber(vendor.getPhoneNumber())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            // Save the vendor
            Vendor savedVendor = vendorService.saveVendor(vendor);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVendor);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET endpoint to retrieve all vendors
     * @return ResponseEntity with list of all vendors
     */
    @GetMapping
    public ResponseEntity<List<Vendor>> getAllVendors() {
        List<Vendor> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(vendors);
    }
    
    /**
     * GET endpoint to retrieve a vendor by phone number
     * @param phoneNumber the phone number
     * @return ResponseEntity with the vendor if found
     */
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<Vendor> getVendorByPhoneNumber(@PathVariable String phoneNumber) {
        Vendor vendor = vendorService.getVendorByPhoneNumber(phoneNumber);
        if (vendor != null) {
            return ResponseEntity.ok(vendor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET endpoint to retrieve a vendor by vendor ID
     * @param vendorId the vendor ID
     * @return ResponseEntity with the vendor if found
     */
    @GetMapping("/{vendorId}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable String vendorId) {
        Vendor vendor = vendorService.getVendorById(vendorId);
        if (vendor != null) {
            return ResponseEntity.ok(vendor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET endpoint to retrieve verified vendors
     * @param verified verification status
     * @return ResponseEntity with list of vendors with specified verification status
     */
    @GetMapping("/verified/{verified}")
    public ResponseEntity<List<Vendor>> getVendorsByVerified(@PathVariable boolean verified) {
        List<Vendor> vendors = vendorService.getVendorsByVerified(verified);
        return ResponseEntity.ok(vendors);
    }
    
    /**
     * PUT endpoint to update an existing vendor
     * @param phoneNumber the phone number of the vendor to update
     * @param vendor the updated vendor data
     * @return ResponseEntity with the updated vendor
     */
    @PutMapping("/phone/{phoneNumber}")
    public ResponseEntity<Vendor> updateVendor(@PathVariable String phoneNumber, @RequestBody Vendor vendor) {
        try {
            vendor.setPhoneNumber(phoneNumber); // Ensure phone number consistency
            Vendor updatedVendor = vendorService.updateVendor(vendor);
            if (updatedVendor != null) {
                return ResponseEntity.ok(updatedVendor);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * DELETE endpoint to delete a vendor by phone number
     * @param phoneNumber the phone number
     * @return ResponseEntity with success status
     */
    @DeleteMapping("/phone/{phoneNumber}")
    public ResponseEntity<Void> deleteVendor(@PathVariable String phoneNumber) {
        boolean deleted = vendorService.deleteVendor(phoneNumber);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET endpoint to get the total count of vendors
     * @return ResponseEntity with vendor count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getVendorCount() {
        long count = vendorService.getVendorCount();
        return ResponseEntity.ok(count);
    }
}
