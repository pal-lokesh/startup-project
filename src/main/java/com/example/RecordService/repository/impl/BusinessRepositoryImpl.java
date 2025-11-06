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
    private static final AtomicLong idCounter = new AtomicLong(1);
    
    @Override
    public Business save(Business business) {
        if (business.getBusinessId() == null) {
            business.setBusinessId("BUSINESS_" + idCounter.getAndIncrement());
        }
        businesses.put(business.getBusinessId(), business);
        return business;
    }
    
    @Override
    public Business findByBusinessId(String businessId) {
        return businesses.get(businessId);
    }
    
    @Override
    public Business findByPhoneNumber(String phoneNumber) {
        return businesses.values().stream()
                .filter(business -> business.getPhoneNumber().equals(phoneNumber))
                .findFirst()
                .orElse(null);
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
    public List<Business> findByVendorPhoneNumber(String phoneNumber) {
        return businesses.values().stream()
                .filter(business -> business.getPhoneNumber().equals(phoneNumber))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public boolean existsByBusinessId(String businessId) {
        return businesses.containsKey(businessId);
    }
    
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return businesses.values().stream()
                .anyMatch(business -> business.getPhoneNumber().equals(phoneNumber));
    }
    
    @Override
    public Business update(Business business) {
        businesses.put(business.getBusinessId(), business);
        return business;
    }
    
    @Override
    public boolean delete(String businessId) {
        Business removed = businesses.remove(businessId);
        return removed != null;
    }
    
    @Override
    public long count() {
        return businesses.size();
    }

    @Override
    public List<Business> findNearby(double latitude, double longitude, double radiusKm) {
        // Simple distance calculation using Haversine formula
        return businesses.values().stream()
                .filter(business -> {
                    if (business.getLatitude() == null || business.getLongitude() == null) {
                        return false;
                    }
                    double distance = calculateDistance(
                        latitude, longitude,
                        business.getLatitude(), business.getLongitude()
                    );
                    return distance <= radiusKm;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}