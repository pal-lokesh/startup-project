package com.example.RecordService.repository.impl;

import com.example.RecordService.model.Business;
import com.example.RecordService.repository.BusinessRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class BusinessRepositoryImpl implements BusinessRepository {
    
    private static final Map<String, Business> businesses = new ConcurrentHashMap<>();
    private static final Map<String, Business> businessesByPhone = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);
    
    @Override
    public Business save(Business business) {
        if (business.getBusinessId() == null) {
            business.setBusinessId("BUSINESS_" + idCounter.getAndIncrement());
        }
        business.setUpdatedAt(java.time.LocalDateTime.now());
        businesses.put(business.getBusinessId(), business);
        businessesByPhone.put(business.getPhoneNumber(), business);
        return business;
    }
    
    @Override
    public Business findByBusinessId(String businessId) {
        return businesses.get(businessId);
    }
    
    @Override
    public Business findByPhoneNumber(String phoneNumber) {
        return businessesByPhone.get(phoneNumber);
    }
    
    @Override
    public List<Business> findAll() {
        return new ArrayList<>(businesses.values());
    }
    
    @Override
    public List<Business> findByCategory(String category) {
        return businesses.values().stream()
                .filter(business -> business.getBusinessCategory().equals(category))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<Business> findByActive(boolean active) {
        return businesses.values().stream()
                .filter(business -> business.isActive() == active)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public boolean existsByBusinessId(String businessId) {
        return businesses.containsKey(businessId);
    }
    
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return businessesByPhone.containsKey(phoneNumber);
    }
    
    @Override
    public Business update(Business business) {
        business.setUpdatedAt(java.time.LocalDateTime.now());
        businesses.put(business.getBusinessId(), business);
        businessesByPhone.put(business.getPhoneNumber(), business);
        return business;
    }
    
    @Override
    public boolean delete(String businessId) {
        Business business = businesses.remove(businessId);
        if (business != null) {
            businessesByPhone.remove(business.getPhoneNumber());
            return true;
        }
        return false;
    }
    
    @Override
    public long count() {
        return businesses.size();
    }
}
