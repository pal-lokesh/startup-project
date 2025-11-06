package com.example.RecordService.repository.impl;

import com.example.RecordService.entity.Rating;
import com.example.RecordService.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class RatingRepositoryImpl implements RatingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Rating> ratingRowMapper = new RowMapper<Rating>() {
        @Override
        public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
            Rating rating = new Rating();
            rating.setRatingId(rs.getString("rating_id"));
            rating.setClientPhone(rs.getString("client_phone"));
            rating.setItemId(rs.getString("item_id"));
            rating.setItemType(rs.getString("item_type"));
            rating.setBusinessId(rs.getString("business_id"));
            rating.setRating(rs.getInt("rating"));
            rating.setComment(rs.getString("comment"));
            rating.setOrderId(rs.getString("order_id"));
            rating.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            rating.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            rating.setIsActive(rs.getBoolean("is_active"));
            return rating;
        }
    };

    @Override
    public List<Rating> findAll() {
        String sql = "SELECT * FROM ratings WHERE is_active = true ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, ratingRowMapper);
    }

    @Override
    public Optional<Rating> findById(String ratingId) {
        String sql = "SELECT * FROM ratings WHERE rating_id = ? AND is_active = true";
        List<Rating> ratings = jdbcTemplate.query(sql, ratingRowMapper, ratingId);
        return ratings.isEmpty() ? Optional.empty() : Optional.of(ratings.get(0));
    }

    @Override
    public List<Rating> findByItemIdAndItemType(String itemId, String itemType) {
        String sql = "SELECT * FROM ratings WHERE item_id = ? AND item_type = ? AND is_active = true ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, ratingRowMapper, itemId, itemType);
    }

    @Override
    public List<Rating> findByBusinessId(String businessId) {
        String sql = "SELECT * FROM ratings WHERE business_id = ? AND is_active = true ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, ratingRowMapper, businessId);
    }

    @Override
    public List<Rating> findByClientPhone(String clientPhone) {
        String sql = "SELECT * FROM ratings WHERE client_phone = ? AND is_active = true ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, ratingRowMapper, clientPhone);
    }

    @Override
    public List<Rating> findByOrderId(String orderId) {
        String sql = "SELECT * FROM ratings WHERE order_id = ? AND is_active = true ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, ratingRowMapper, orderId);
    }

    @Override
    public Optional<Rating> findByClientPhoneAndItemIdAndItemType(String clientPhone, String itemId, String itemType) {
        String sql = "SELECT * FROM ratings WHERE client_phone = ? AND item_id = ? AND item_type = ? AND is_active = true";
        List<Rating> ratings = jdbcTemplate.query(sql, ratingRowMapper, clientPhone, itemId, itemType);
        return ratings.isEmpty() ? Optional.empty() : Optional.of(ratings.get(0));
    }

    @Override
    public Rating save(Rating rating) {
        String sql = "INSERT INTO ratings (rating_id, client_phone, item_id, item_type, business_id, rating, comment, order_id, created_at, updated_at, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, 
            rating.getRatingId(),
            rating.getClientPhone(),
            rating.getItemId(),
            rating.getItemType(),
            rating.getBusinessId(),
            rating.getRating(),
            rating.getComment(),
            rating.getOrderId(),
            rating.getCreatedAt(),
            rating.getUpdatedAt(),
            rating.getIsActive()
        );
        return rating;
    }

    @Override
    public Rating update(Rating rating) {
        String sql = "UPDATE ratings SET rating = ?, comment = ?, updated_at = ? WHERE rating_id = ?";
        rating.setUpdatedAt(LocalDateTime.now());
        jdbcTemplate.update(sql, 
            rating.getRating(),
            rating.getComment(),
            rating.getUpdatedAt(),
            rating.getRatingId()
        );
        return rating;
    }

    @Override
    public void deleteById(String ratingId) {
        String sql = "UPDATE ratings SET is_active = false, updated_at = ? WHERE rating_id = ?";
        jdbcTemplate.update(sql, LocalDateTime.now(), ratingId);
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM ratings WHERE is_active = true";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    @Override
    public long countByItemIdAndItemType(String itemId, String itemType) {
        String sql = "SELECT COUNT(*) FROM ratings WHERE item_id = ? AND item_type = ? AND is_active = true";
        return jdbcTemplate.queryForObject(sql, Long.class, itemId, itemType);
    }

    @Override
    public long countByBusinessId(String businessId) {
        String sql = "SELECT COUNT(*) FROM ratings WHERE business_id = ? AND is_active = true";
        return jdbcTemplate.queryForObject(sql, Long.class, businessId);
    }

    @Override
    public double getAverageRatingByItemIdAndItemType(String itemId, String itemType) {
        String sql = "SELECT AVG(rating) FROM ratings WHERE item_id = ? AND item_type = ? AND is_active = true";
        Double avg = jdbcTemplate.queryForObject(sql, Double.class, itemId, itemType);
        return avg != null ? avg : 0.0;
    }

    @Override
    public double getAverageRatingByBusinessId(String businessId) {
        String sql = "SELECT AVG(rating) FROM ratings WHERE business_id = ? AND is_active = true";
        Double avg = jdbcTemplate.queryForObject(sql, Double.class, businessId);
        return avg != null ? avg : 0.0;
    }
}
