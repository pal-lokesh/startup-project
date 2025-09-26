package com.example.RecordService.repository;

import com.example.RecordService.entity.Plate;
import java.util.List;
import java.util.Optional;

public interface PlateRepository {
    List<Plate> findAll();
    Optional<Plate> findById(String plateId);
    List<Plate> findByBusinessId(String businessId);
    Plate save(Plate plate);
    Plate update(Plate plate);
    void deleteById(String plateId);
    long count();
    long countByBusinessId(String businessId);
}
