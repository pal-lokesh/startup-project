package com.example.RecordService.controller;

import com.example.RecordService.model.User;
import com.example.RecordService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow CORS for testing
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * POST endpoint to create a new user
     * @param user the user details to be saved
     * @return ResponseEntity with the created user and HTTP status
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            // Validate required fields
            if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (user.getUserType() == null) {
                return ResponseEntity.badRequest().build();
            }
            
            // Check if user already exists with this phone number
            if (userService.userExistsByPhoneNumber(user.getPhoneNumber())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            // Check if user already exists with this email
            if (userService.userExistsByEmail(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            // Save the user
            User savedUser = userService.saveUser(user);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET endpoint to retrieve all users
     * @return ResponseEntity with list of all users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * GET endpoint to retrieve a user by phone number
     * @param phoneNumber the phone number
     * @return ResponseEntity with the user if found
     */
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<User> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        User user = userService.getUserByPhoneNumber(phoneNumber);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET endpoint to retrieve a user by email
     * @param email the email address
     * @return ResponseEntity with the user if found
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * PUT endpoint to update an existing user
     * @param phoneNumber the phone number of the user to update
     * @param user the updated user data
     * @return ResponseEntity with the updated user
     */
    @PutMapping("/phone/{phoneNumber}")
    public ResponseEntity<User> updateUser(@PathVariable String phoneNumber, @RequestBody User user) {
        try {
            user.setPhoneNumber(phoneNumber); // Ensure phone number consistency
            User updatedUser = userService.updateUser(user);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET endpoint to retrieve users by type
     * @param userType the user type (VENDOR or CLIENT)
     * @return ResponseEntity with list of users of specified type
     */
    @GetMapping("/type/{userType}")
    public ResponseEntity<List<User>> getUsersByType(@PathVariable String userType) {
        try {
            User.UserType type = User.UserType.valueOf(userType.toUpperCase());
            List<User> users = userService.getUsersByType(type);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * DELETE endpoint to delete a user by phone number
     * @param phoneNumber the phone number
     * @return ResponseEntity with success status
     */
    @DeleteMapping("/phone/{phoneNumber}")
    public ResponseEntity<Void> deleteUser(@PathVariable String phoneNumber) {
        boolean deleted = userService.deleteUser(phoneNumber);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET endpoint to get the total count of users
     * @return ResponseEntity with user count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        long count = userService.getUserCount();
        return ResponseEntity.ok(count);
    }
}
