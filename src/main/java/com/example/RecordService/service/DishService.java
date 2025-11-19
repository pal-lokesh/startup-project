package com.example.RecordService.service;

import com.example.RecordService.entity.Dish;
import com.example.RecordService.repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DishService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private StockNotificationService stockNotificationService;

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public Optional<Dish> getDishById(String dishId) {
        return dishRepository.findById(dishId);
    }

    public List<Dish> getDishesByBusinessId(String businessId) {
        System.out.println("🔍 DishService.getDishesByBusinessId called with businessId: " + businessId);
        List<Dish> dishes = dishRepository.findByBusinessId(businessId);
        System.out.println("✅ Found " + (dishes != null ? dishes.size() : 0) + " dishes for business: " + businessId);
        if (dishes != null && !dishes.isEmpty()) {
            dishes.forEach(d -> System.out.println("   - Dish: " + d.getDishId() + " - " + d.getDishName()));
        }
        return dishes != null ? dishes : new java.util.ArrayList<>();
    }

    public Dish createDish(Dish dish) {
        if (dish.getIsAvailable() == null) {
            dish.setIsAvailable(true);
        }
        if (dish.getQuantity() == null) {
            dish.setQuantity(0);
        }
        if (dish.getAvailabilityDates() == null) {
            dish.setAvailabilityDates(new java.util.ArrayList<>());
        }
        return dishRepository.save(dish);
    }

    public Dish updateDish(String dishId, Dish dishDetails) {
        Optional<Dish> optionalDish = dishRepository.findById(dishId);
        Dish dish = optionalDish.orElseThrow(() -> new RuntimeException("Dish not found with id: " + dishId));
        int previousQuantity = dish.getQuantity() == null ? 0 : dish.getQuantity();

        dish.setDishName(dishDetails.getDishName());
        dish.setDishDescription(dishDetails.getDishDescription());
        dish.setDishImage(dishDetails.getDishImage());
        dish.setPrice(dishDetails.getPrice());
        dish.setQuantity(dishDetails.getQuantity());
        dish.setIsAvailable(dishDetails.getIsAvailable());
        dish.setAvailabilityDates(
                dishDetails.getAvailabilityDates() != null
                        ? dishDetails.getAvailabilityDates()
                        : new java.util.ArrayList<>()
        );

        Dish updatedDish = dishRepository.save(dish);

        if (previousQuantity == 0 && updatedDish.getQuantity() != null && updatedDish.getQuantity() > 0) {
            try {
                stockNotificationService.notifySubscribers(
                        updatedDish.getDishId(),
                        "DISH",
                        updatedDish.getDishName()
                );
            } catch (Exception e) {
                System.err.println("Failed to notify subscribers for dish " + updatedDish.getDishId() + ": " + e.getMessage());
            }
        }

        return updatedDish;
    }

    public void deleteDish(String dishId) {
        dishRepository.deleteById(dishId);
    }

    public long getDishCount() {
        return dishRepository.count();
    }
}

