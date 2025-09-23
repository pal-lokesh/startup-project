package com.example.RecordService.repository;

import com.example.RecordService.model.Business;
import java.util.List;

public interface BusinessRepository {
    Business save(Business business);
    Business findByBusinessId(String businessId);
    Business findByPhoneNumber(String phoneNumber);
    List<Business> findAll();
    List<Business> findByCategory(String category);
    List<Business> findByActive(boolean active);
    List<Business> findByVendorPhoneNumber(String phoneNumber);
    boolean existsByBusinessId(String businessId);
    boolean existsByPhoneNumber(String phoneNumber);
    Business update(Business business);
    boolean delete(String businessId);
    long count();
}
