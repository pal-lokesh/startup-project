package com.example.RecordService.controller;

import com.example.RecordService.model.User;
import com.example.RecordService.model.dto.EmailVerificationRequest;
import com.example.RecordService.model.dto.VerificationRequest;
import com.example.RecordService.repository.UserRepository;
import com.example.RecordService.service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VerificationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationService verificationService;

    /**
     * Send phone verification code
     */
    @PostMapping("/phone/send")
    public ResponseEntity<?> sendPhoneVerificationCode(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Phone number is required");
                return ResponseEntity.badRequest().body(error);
            }

            boolean sent = verificationService.sendPhoneVerificationCode(phoneNumber);
            if (sent) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Verification code sent successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Failed to send verification code. User may not exist.");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while sending verification code");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Verify phone number with code
     */
    @PostMapping("/phone/verify")
    public ResponseEntity<?> verifyPhoneNumber(@Valid @RequestBody VerificationRequest request) {
        try {
            boolean verified = verificationService.verifyPhoneNumber(request.getPhoneNumber(), request.getCode());
            if (verified) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Phone number verified successfully");
                response.put("verified", "true");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid or expired verification code");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while verifying phone number");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Send email verification code
     */
    @PostMapping("/email/send")
    public ResponseEntity<?> sendEmailVerificationCode(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email is required");
                return ResponseEntity.badRequest().body(error);
            }

            boolean sent = verificationService.sendEmailVerificationCode(email);
            if (sent) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Verification code sent successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Failed to send verification code. User may not exist.");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while sending verification code");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Verify email with code
     */
    @PostMapping("/email/verify")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            boolean verified = verificationService.verifyEmail(request.getEmail(), request.getCode());
            if (verified) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Email verified successfully");
                response.put("verified", "true");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid or expired verification code");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while verifying email");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Check if phone number is verified
     */
    @GetMapping("/phone/{phoneNumber}/status")
    public ResponseEntity<?> checkPhoneVerificationStatus(@PathVariable String phoneNumber) {
        try {
            boolean verified = verificationService.isPhoneVerified(phoneNumber);
            Map<String, Object> response = new HashMap<>();
            response.put("phoneNumber", phoneNumber);
            response.put("verified", verified);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while checking verification status");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Check if email is verified
     */
    @GetMapping("/email/{email}/status")
    public ResponseEntity<?> checkEmailVerificationStatus(@PathVariable String email) {
        try {
            boolean verified = verificationService.isEmailVerified(email);
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("verified", verified);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while checking verification status");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Get verification status for a user (both phone and email)
     * Works for both CLIENT and VENDOR users
     */
    @GetMapping("/user/{phoneNumber}/status")
    public ResponseEntity<?> getUserVerificationStatus(@PathVariable String phoneNumber) {
        try {
            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
            if (userOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("phoneNumber", user.getPhoneNumber());
            response.put("email", user.getEmail());
            response.put("phoneVerified", user.isPhoneVerified());
            response.put("emailVerified", user.isEmailVerified());
            response.put("userType", user.getUserType());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while checking verification status");
            return ResponseEntity.status(500).body(error);
        }
    }
}

