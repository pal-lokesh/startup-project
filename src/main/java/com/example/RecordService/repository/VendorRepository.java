package com.example.RecordService.repository;

import com.example.RecordService.model.Vendor;
import java.util.List;

public interface VendorRepository {
    Vendor save(Vendor vendor);
    Vendor findByPhoneNumber(String phoneNumber);
    Vendor findByVendorId(String vendorId);
    List<Vendor> findAll();
    List<Vendor> findByVerified(boolean verified);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByVendorId(String vendorId);
    Vendor update(Vendor vendor);
    boolean delete(String phoneNumber);
    long count();
}
