package com.example.RecordService.controller;

import com.example.RecordService.model.Business;
import com.example.RecordService.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businesses")
@CrossOrigin(origins = "*")
public class BusinessController {
    
    @Autowired
    private BusinessService businessService;
    
    /**
     * POST endpoint to create a new business
     * @param business the business details to be saved
     * @return ResponseEntity with the created business and HTTP status
     */
    @PostMapping
    public ResponseEntity<Business> createBusiness(@RequestBody Business business) {
        try {
            // Validate required fields
            if (business.getPhoneNumber() == null || business.getPhoneNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (business.getBusinessName() == null || business.getBusinessName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (business.getBusinessEmail() == null || business.getBusinessEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Save the business (multiple businesses per vendor are now allowed)
            Business savedBusiness = businessService.saveBusiness(business);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBusiness);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET endpoint to retrieve all businesses
     * @return ResponseEntity with list of all businesses
     */
    @GetMapping
    public ResponseEntity<List<Business>> getAllBusinesses() {
        List<Business> businesses = businessService.getAllBusinesses();
        return ResponseEntity.ok(businesses);
    }
    
    /**
     * GET endpoint to retrieve a business by business ID
     * @param businessId the business ID
     * @return ResponseEntity with the business if found
     */
    @GetMapping("/{businessId}")
    public ResponseEntity<Business> getBusinessById(@PathVariable String businessId) {
        Business business = businessService.getBusinessById(businessId);
        if (business != null) {
            return ResponseEntity.ok(business);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET endpoint to retrieve a business by phone number
     * @param phoneNumber the phone number
     * @return ResponseEntity with the business if found
     */
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<Business> getBusinessByPhoneNumber(@PathVariable String phoneNumber) {
        Business business = businessService.getBusinessByPhoneNumber(phoneNumber);
        if (business != null) {
            return ResponseEntity.ok(business);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET endpoint to retrieve businesses by category
     * @param category the business category
     * @return ResponseEntity with list of businesses with specified category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Business>> getBusinessesByCategory(@PathVariable String category) {
        List<Business> businesses = businessService.getBusinessesByCategory(category);
        return ResponseEntity.ok(businesses);
    }
    
    /**
     * GET endpoint to retrieve active businesses
     * @param active active status
     * @return ResponseEntity with list of businesses with specified active status
     */
    @GetMapping("/active/{active}")
    public ResponseEntity<List<Business>> getBusinessesByActive(@PathVariable boolean active) {
        List<Business> businesses = businessService.getBusinessesByActive(active);
        return ResponseEntity.ok(businesses);
    }
    
    /**
     * PUT endpoint to update an existing business
     * @param businessId the business ID of the business to update
     * @param business the updated business data
     * @return ResponseEntity with the updated business
     */
    @PutMapping("/{businessId}")
    public ResponseEntity<Business> updateBusiness(@PathVariable String businessId, @RequestBody Business business) {
        try {
            business.setBusinessId(businessId); // Ensure business ID consistency
            Business updatedBusiness = businessService.updateBusiness(business);
            if (updatedBusiness != null) {
                return ResponseEntity.ok(updatedBusiness);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * DELETE endpoint to delete a business by business ID
     * @param businessId the business ID
     * @return ResponseEntity with success status
     */
    @DeleteMapping("/{businessId}")
    public ResponseEntity<Void> deleteBusiness(@PathVariable String businessId) {
        boolean deleted = businessService.deleteBusiness(businessId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET endpoint to get the total count of businesses
     * @return ResponseEntity with business count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getBusinessCount() {
        long count = businessService.getBusinessCount();
        return ResponseEntity.ok(count);
    }
    
    /**
     * GET endpoint to retrieve all businesses for a vendor by phone number
     * @param phoneNumber the vendor's phone number
     * @return ResponseEntity with list of businesses for the vendor
     */
    @GetMapping("/vendor/{phoneNumber}")
    public ResponseEntity<List<Business>> getBusinessesByVendorPhoneNumber(@PathVariable String phoneNumber) {
        List<Business> businesses = businessService.getBusinessesByVendorPhoneNumber(phoneNumber);
        return ResponseEntity.ok(businesses);
    }
}
