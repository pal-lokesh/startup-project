package com.example.RecordService.repository.impl;

import com.example.RecordService.entity.Plate;
import com.example.RecordService.repository.PlateRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PlateRepositoryImpl implements PlateRepository {
    
    private static final Map<String, Plate> plates = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);
    
    @Override
    public List<Plate> findAll() {
        return new ArrayList<>(plates.values());
    }
    
    @Override
    public Optional<Plate> findById(String plateId) {
        return Optional.ofNullable(plates.get(plateId));
    }
    
    @Override
    public List<Plate> findByBusinessId(String businessId) {
        return plates.values().stream()
                .filter(plate -> plate.getBusinessId().equals(businessId))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public Plate save(Plate plate) {
        if (plate.getPlateId() == null) {
            plate.setPlateId("PLATE_" + idCounter.getAndIncrement());
        }
        plates.put(plate.getPlateId(), plate);
        return plate;
    }
    
    @Override
    public Plate update(Plate plate) {
        plates.put(plate.getPlateId(), plate);
        return plate;
    }
    
    @Override
    public void deleteById(String plateId) {
        plates.remove(plateId);
    }
    
    @Override
    public long count() {
        return plates.size();
    }
    
    @Override
    public long countByBusinessId(String businessId) {
        return plates.values().stream()
                .filter(plate -> plate.getBusinessId().equals(businessId))
                .count();
    }
}