package com.example.RecordService.service;

import com.example.RecordService.model.Vendor;
import com.example.RecordService.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorService {
    
    @Autowired
    private VendorRepository vendorRepository;
    
    /**
     * Save a new vendor
     * @param vendor the vendor to be saved
     * @return the saved vendor
     */
    public Vendor saveVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }
    
    /**
     * Get all vendors
     * @return list of all vendors
     */
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }
    
    /**
     * Get vendor by phone number
     * @param phoneNumber the phone number
     * @return the vendor if found, null otherwise
     */
    public Vendor getVendorByPhoneNumber(String phoneNumber) {
        return vendorRepository.findByPhoneNumber(phoneNumber);
    }
    
    /**
     * Get vendor by vendor ID
     * @param vendorId the vendor ID
     * @return the vendor if found, null otherwise
     */
    public Vendor getVendorById(String vendorId) {
        return vendorRepository.findByVendorId(vendorId);
    }
    
    /**
     * Get verified vendors
     * @param verified verification status
     * @return list of vendors with specified verification status
     */
    public List<Vendor> getVendorsByVerified(boolean verified) {
        return vendorRepository.findByVerified(verified);
    }
    
    /**
     * Check if vendor exists by phone number
     * @param phoneNumber the phone number
     * @return true if vendor exists, false otherwise
     */
    public boolean vendorExistsByPhoneNumber(String phoneNumber) {
        return vendorRepository.existsByPhoneNumber(phoneNumber);
    }
    
    /**
     * Check if vendor exists by vendor ID
     * @param vendorId the vendor ID
     * @return true if vendor exists, false otherwise
     */
    public boolean vendorExistsById(String vendorId) {
        return vendorRepository.existsByVendorId(vendorId);
    }
    
    /**
     * Update an existing vendor
     * @param vendor the updated vendor data
     * @return the updated vendor
     */
    public Vendor updateVendor(Vendor vendor) {
        return vendorRepository.update(vendor);
    }
    
    /**
     * Delete a vendor by phone number
     * @param phoneNumber the phone number
     * @return true if vendor was deleted, false if not found
     */
    public boolean deleteVendor(String phoneNumber) {
        return vendorRepository.delete(phoneNumber);
    }
    
    /**
     * Get total number of vendors
     * @return count of vendors
     */
    public long getVendorCount() {
        return vendorRepository.count();
    }
}
