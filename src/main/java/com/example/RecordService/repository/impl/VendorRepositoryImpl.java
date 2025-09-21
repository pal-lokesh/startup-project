package com.example.RecordService.repository.impl;

import com.example.RecordService.model.Vendor;
import com.example.RecordService.repository.VendorRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class VendorRepositoryImpl implements VendorRepository {
    
    private static final Map<String, Vendor> vendors = new ConcurrentHashMap<>();
    private static final Map<String, Vendor> vendorsById = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);
    
    @Override
    public Vendor save(Vendor vendor) {
        if (vendor.getVendorId() == null) {
            vendor.setVendorId("VENDOR_" + idCounter.getAndIncrement());
        }
        vendor.setUpdatedAt(java.time.LocalDateTime.now());
        vendors.put(vendor.getPhoneNumber(), vendor);
        vendorsById.put(vendor.getVendorId(), vendor);
        return vendor;
    }
    
    @Override
    public Vendor findByPhoneNumber(String phoneNumber) {
        return vendors.get(phoneNumber);
    }
    
    @Override
    public Vendor findByVendorId(String vendorId) {
        return vendorsById.get(vendorId);
    }
    
    @Override
    public List<Vendor> findAll() {
        return new ArrayList<>(vendors.values());
    }
    
    @Override
    public List<Vendor> findByVerified(boolean verified) {
        return vendors.values().stream()
                .filter(vendor -> vendor.isVerified() == verified)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return vendors.containsKey(phoneNumber);
    }
    
    @Override
    public boolean existsByVendorId(String vendorId) {
        return vendorsById.containsKey(vendorId);
    }
    
    @Override
    public Vendor update(Vendor vendor) {
        vendor.setUpdatedAt(java.time.LocalDateTime.now());
        vendors.put(vendor.getPhoneNumber(), vendor);
        vendorsById.put(vendor.getVendorId(), vendor);
        return vendor;
    }
    
    @Override
    public boolean delete(String phoneNumber) {
        Vendor vendor = vendors.remove(phoneNumber);
        if (vendor != null) {
            vendorsById.remove(vendor.getVendorId());
            return true;
        }
        return false;
    }
    
    @Override
    public long count() {
        return vendors.size();
    }
}
