package com.example.RecordService.repository.impl;

import com.example.RecordService.model.User;
import com.example.RecordService.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepositoryImpl implements UserRepository {
    
    private static final Map<String, User> users = new ConcurrentHashMap<>();
    
    @Override
    public User save(User user) {
        user.setUpdatedAt(java.time.LocalDateTime.now());
        users.put(user.getPhoneNumber(), user);
        return user;
    }
    
    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return users.get(phoneNumber);
    }
    
    @Override
    public User findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public List<User> findByUserType(User.UserType userType) {
        return users.values().stream()
                .filter(user -> user.getUserType() == userType)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return users.containsKey(phoneNumber);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
    
    @Override
    public User update(User user) {
        user.setUpdatedAt(java.time.LocalDateTime.now());
        users.put(user.getPhoneNumber(), user);
        return user;
    }
    
    @Override
    public boolean delete(String phoneNumber) {
        return users.remove(phoneNumber) != null;
    }
    
    @Override
    public long count() {
        return users.size();
    }
}
