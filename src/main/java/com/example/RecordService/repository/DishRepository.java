package com.example.RecordService.repository;

import com.example.RecordService.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, String> {
    List<Dish> findByBusinessId(String businessId);
    long countByBusinessId(String businessId);
}

