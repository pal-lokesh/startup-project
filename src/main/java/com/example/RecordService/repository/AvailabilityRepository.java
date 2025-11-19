package com.example.RecordService.repository;

import com.example.RecordService.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    
    // Find availability for a specific item and date
    Optional<Availability> findByItemIdAndItemTypeAndAvailabilityDate(
        String itemId, String itemType, LocalDate availabilityDate);
    
    // Find all availabilities for an item
    List<Availability> findByItemIdAndItemTypeOrderByAvailabilityDateAsc(
        String itemId, String itemType);
    
    // Find availabilities for an item within a date range
    @Query("SELECT a FROM Availability a WHERE a.itemId = :itemId AND a.itemType = :itemType " +
           "AND a.availabilityDate BETWEEN :startDate AND :endDate ORDER BY a.availabilityDate ASC")
    List<Availability> findByItemIdAndItemTypeAndDateRange(
        @Param("itemId") String itemId,
        @Param("itemType") String itemType,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
    
    // Find all availabilities for a business
    List<Availability> findByBusinessIdOrderByAvailabilityDateAsc(String businessId);
    
    // Find available items for a specific date
    @Query("SELECT a FROM Availability a WHERE a.businessId = :businessId " +
           "AND a.availabilityDate = :date AND a.isAvailable = true AND a.availableQuantity > 0")
    List<Availability> findAvailableItemsByBusinessAndDate(
        @Param("businessId") String businessId,
        @Param("date") LocalDate date);
    
    // Check if item is available on a specific date with sufficient quantity
    @Query("SELECT a FROM Availability a WHERE a.itemId = :itemId AND a.itemType = :itemType " +
           "AND a.availabilityDate = :date AND a.isAvailable = true AND a.availableQuantity >= :quantity")
    Optional<Availability> checkAvailability(
        @Param("itemId") String itemId,
        @Param("itemType") String itemType,
        @Param("date") LocalDate date,
        @Param("quantity") Integer quantity);
    
    // Delete all availabilities for an item
    void deleteByItemIdAndItemType(String itemId, String itemType);
}

