package com.example.RecordService.service;

import com.example.RecordService.model.Business;
import com.example.RecordService.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessService {
    
    @Autowired
    private BusinessRepository businessRepository;
    
    /**
     * Save a new business
     * @param business the business to be saved
     * @return the saved business
     */
    public Business saveBusiness(Business business) {
        return businessRepository.save(business);
    }
    
    /**
     * Get all businesses
     * @return list of all businesses
     */
    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }
    
    /**
     * Get business by business ID
     * @param businessId the business ID
     * @return the business if found, null otherwise
     */
    public Business getBusinessById(String businessId) {
        return businessRepository.findByBusinessId(businessId);
    }
    
    /**
     * Get business by phone number
     * @param phoneNumber the phone number
     * @return the business if found, null otherwise
     */
    public Business getBusinessByPhoneNumber(String phoneNumber) {
        return businessRepository.findByPhoneNumber(phoneNumber);
    }
    
    /**
     * Get businesses by category
     * @param category the business category
     * @return list of businesses with specified category
     */
    public List<Business> getBusinessesByCategory(String category) {
        return businessRepository.findByCategory(category);
    }
    
    /**
     * Get active businesses
     * @param active active status
     * @return list of businesses with specified active status
     */
    public List<Business> getBusinessesByActive(boolean active) {
        return businessRepository.findByActive(active);
    }
    
    /**
     * Check if business exists by business ID
     * @param businessId the business ID
     * @return true if business exists, false otherwise
     */
    public boolean businessExistsById(String businessId) {
        return businessRepository.existsByBusinessId(businessId);
    }
    
    /**
     * Check if business exists by phone number
     * @param phoneNumber the phone number
     * @return true if business exists, false otherwise
     */
    public boolean businessExistsByPhoneNumber(String phoneNumber) {
        return businessRepository.existsByPhoneNumber(phoneNumber);
    }
    
    /**
     * Update an existing business
     * @param business the updated business data
     * @return the updated business
     */
    public Business updateBusiness(Business business) {
        return businessRepository.update(business);
    }
    
    /**
     * Delete a business by business ID
     * @param businessId the business ID
     * @return true if business was deleted, false if not found
     */
    public boolean deleteBusiness(String businessId) {
        return businessRepository.delete(businessId);
    }
    
    /**
     * Get total number of businesses
     * @return count of businesses
     */
    public long getBusinessCount() {
        return businessRepository.count();
    }
}
