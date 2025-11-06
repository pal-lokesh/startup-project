package com.example.RecordService.repository;

import com.example.RecordService.entity.Rating;
import java.util.List;
import java.util.Optional;

public interface RatingRepository {
    List<Rating> findAll();
    Optional<Rating> findById(String ratingId);
    List<Rating> findByItemIdAndItemType(String itemId, String itemType);
    List<Rating> findByBusinessId(String businessId);
    List<Rating> findByClientPhone(String clientPhone);
    List<Rating> findByOrderId(String orderId);
    Optional<Rating> findByClientPhoneAndItemIdAndItemType(String clientPhone, String itemId, String itemType);
    Rating save(Rating rating);
    Rating update(Rating rating);
    void deleteById(String ratingId);
    long count();
    long countByItemIdAndItemType(String itemId, String itemType);
    long countByBusinessId(String businessId);
    double getAverageRatingByItemIdAndItemType(String itemId, String itemType);
    double getAverageRatingByBusinessId(String businessId);
}
