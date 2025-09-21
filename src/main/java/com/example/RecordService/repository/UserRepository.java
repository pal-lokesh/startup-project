package com.example.RecordService.repository;

import com.example.RecordService.model.User;
import java.util.List;

public interface UserRepository {
    User save(User user);
    User findByPhoneNumber(String phoneNumber);
    User findByEmail(String email);
    List<User> findAll();
    List<User> findByUserType(User.UserType userType);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    User update(User user);
    boolean delete(String phoneNumber);
    long count();
}
