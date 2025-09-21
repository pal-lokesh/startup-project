package com.example.RecordService.repository;

import com.example.RecordService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    List<User> findByUserType(User.UserType userType);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}
