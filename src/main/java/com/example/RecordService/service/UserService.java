package com.example.RecordService.service;

import com.example.RecordService.model.User;
import com.example.RecordService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Save a new user
     * @param user the user to be saved
     * @return the saved user
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Get all users
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get user by phone number
     * @param phoneNumber the phone number
     * @return the user if found, null otherwise
     */
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
    
    /**
     * Get user by email
     * @param email the email address
     * @return the user if found, null otherwise
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Get users by type (VENDOR or CLIENT)
     * @param userType the user type
     * @return list of users with the specified type
     */
    public List<User> getUsersByType(User.UserType userType) {
        return userRepository.findByUserType(userType);
    }
    
    /**
     * Check if user exists by phone number
     * @param phoneNumber the phone number
     * @return true if user exists, false otherwise
     */
    public boolean userExistsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    
    /**
     * Check if user exists by email
     * @param email the email address
     * @return true if user exists, false otherwise
     */
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Update an existing user
     * @param user the updated user data
     * @return the updated user
     */
    public User updateUser(User user) {
        return userRepository.update(user);
    }
    
    /**
     * Delete a user by phone number
     * @param phoneNumber the phone number
     * @return true if user was deleted, false if not found
     */
    public boolean deleteUser(String phoneNumber) {
        return userRepository.delete(phoneNumber);
    }
    
    /**
     * Get total number of users
     * @return count of users
     */
    public long getUserCount() {
        return userRepository.count();
    }
}
