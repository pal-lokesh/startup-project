package com.example.RecordService.controller;

import com.example.RecordService.model.User;
import com.example.RecordService.model.dto.JwtResponse;
import com.example.RecordService.model.dto.LoginRequest;
import com.example.RecordService.model.dto.SignupRequest;
import com.example.RecordService.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            User user = authService.registerUser(signupRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            Map<String, Object> userData = new HashMap<>();
            userData.put("phoneNumber", user.getPhoneNumber());
            userData.put("email", user.getEmail());
            userData.put("firstName", user.getFirstName());
            userData.put("lastName", user.getLastName());
            userData.put("userType", user.getUserType());
            userData.put("phoneVerified", user.isPhoneVerified());
            userData.put("emailVerified", user.isEmailVerified());
            response.put("user", userData);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/check-phone/{phoneNumber}")
    public ResponseEntity<?> checkPhoneNumber(@PathVariable String phoneNumber) {
        boolean exists = authService.existsByPhoneNumber(phoneNumber);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {
        boolean exists = authService.existsByEmail(email);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
