package com.example.RecordService.repository.impl;

import com.example.RecordService.entity.Plate;
import com.example.RecordService.repository.PlateRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PlateRepositoryImpl implements PlateRepository {
    private final Map<String, Plate> plates = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

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
                .filter(plate -> businessId.equals(plate.getBusinessId()))
                .collect(Collectors.toList());
    }

    @Override
    public Plate save(Plate plate) {
        if (plate.getPlateId() == null || plate.getPlateId().isEmpty()) {
            plate.setPlateId("PLATE_" + idCounter.getAndIncrement());
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (plate.getCreatedAt() == null) {
            plate.setCreatedAt(now);
        }
        plate.setUpdatedAt(now);
        
        if (plate.getIsActive() == null) {
            plate.setIsActive(true);
        }
        
        plates.put(plate.getPlateId(), plate);
        return plate;
    }

    @Override
    public Plate update(Plate plate) {
        if (plates.containsKey(plate.getPlateId())) {
            plate.setUpdatedAt(LocalDateTime.now());
            plates.put(plate.getPlateId(), plate);
            return plate;
        }
        throw new RuntimeException("Plate not found with id: " + plate.getPlateId());
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
                .filter(plate -> businessId.equals(plate.getBusinessId()))
                .count();
    }
}
