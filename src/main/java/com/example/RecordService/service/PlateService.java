package com.example.RecordService.service;

import com.example.RecordService.entity.Plate;
import com.example.RecordService.repository.PlateRepository;
import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlateService {
    @Autowired
    private PlateRepository plateRepository;

    public List<Plate> getAllPlates() {
        return plateRepository.findAll();
    }

    public Optional<Plate> getPlateById(String plateId) {
        return plateRepository.findById(plateId);
    }

    public List<Plate> getPlatesByBusinessId(String businessId) {
        return plateRepository.findByBusinessId(businessId);
    }

    public Plate createPlate(Plate plate) {
        // Ensure dishType is set, default to 'veg' if null or empty
        if (plate.getDishType() == null || plate.getDishType().trim().isEmpty()) {
            plate.setDishType("veg");
        }
        return plateRepository.save(plate);
    }

    public Plate updatePlate(String plateId, Plate plateDetails) {
        Optional<Plate> optionalPlate = plateRepository.findById(plateId);
        if (optionalPlate.isPresent()) {
            Plate plate = optionalPlate.get();
            plate.setDishName(plateDetails.getDishName());
            plate.setDishDescription(plateDetails.getDishDescription());
            plate.setPlateImage(plateDetails.getPlateImage());
            plate.setPrice(plateDetails.getPrice());
            
            // Ensure dishType is set, default to 'veg' if null or empty
            String dishType = plateDetails.getDishType();
            if (dishType == null || dishType.trim().isEmpty()) {
                dishType = "veg";
            }
            plate.setDishType(dishType);
            
            plate.setIsActive(plateDetails.getIsActive());
            return plateRepository.update(plate);
        }
        throw new RuntimeException("Plate not found with id: " + plateId);
    }

    public void deletePlate(String plateId) {
        plateRepository.deleteById(plateId);
    }

    public long getPlateCount() {
        return plateRepository.count();
    }

    public long getPlateCountByBusinessId(String businessId) {
        return plateRepository.countByBusinessId(businessId);
    }
}
