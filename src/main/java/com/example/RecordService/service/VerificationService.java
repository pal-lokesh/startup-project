package com.example.RecordService.service;

import com.example.RecordService.model.User;
import com.example.RecordService.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);
    private static final int CODE_EXPIRY_MINUTES = 10;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    private final SecureRandom random = new SecureRandom();

    /**
     * Generate a 6-digit verification code
     */
    public String generateVerificationCode() {
        int code = random.nextInt(900000) + 100000; // Generate 6-digit code (100000-999999)
        return String.valueOf(code);
    }

    /**
     * Send phone verification code
     * In production, integrate with SMS service like Twilio, AWS SNS, etc.
     */
    public boolean sendPhoneVerificationCode(String phoneNumber) {
        try {
            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
            if (userOpt.isEmpty()) {
                logger.error("User not found for phone number: {}", phoneNumber);
                return false;
            }

            User user = userOpt.get();
            String code = generateVerificationCode();
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES);

            user.setPhoneVerificationCode(code);
            user.setPhoneVerificationCodeExpiry(expiry);
            userRepository.save(user);

            // TODO: Integrate with SMS service (Twilio, AWS SNS, etc.)
            // For now, log the code (in production, send via SMS)
            logger.info("Phone verification code for {}: {}", phoneNumber, code);
            System.out.println("=== PHONE VERIFICATION CODE ===");
            System.out.println("Phone: " + phoneNumber);
            System.out.println("Code: " + code);
            System.out.println("Expires in: " + CODE_EXPIRY_MINUTES + " minutes");
            System.out.println("================================");

            return true;
        } catch (Exception e) {
            logger.error("Error sending phone verification code to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }

    /**
     * Send email verification code
     */
    public boolean sendEmailVerificationCode(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                logger.error("User not found for email: {}", email);
                return false;
            }

            User user = userOpt.get();
            String code = generateVerificationCode();
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES);

            user.setEmailVerificationCode(code);
            user.setEmailVerificationCodeExpiry(expiry);
            userRepository.save(user);

            // Send email if mail sender is configured
            if (mailSender != null) {
                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(email);
                    message.setSubject("Email Verification Code");
                    message.setText("Your verification code is: " + code + "\n\nThis code will expire in " + CODE_EXPIRY_MINUTES + " minutes.");
                    mailSender.send(message);
                    logger.info("Email verification code sent to: {}", email);
                } catch (Exception e) {
                    logger.error("Error sending email to {}: {}", email, e.getMessage());
                }
            } else {
                // Log the code if email is not configured
                logger.info("Email verification code for {}: {}", email, code);
                System.out.println("=== EMAIL VERIFICATION CODE ===");
                System.out.println("Email: " + email);
                System.out.println("Code: " + code);
                System.out.println("Expires in: " + CODE_EXPIRY_MINUTES + " minutes");
                System.out.println("================================");
            }

            return true;
        } catch (Exception e) {
            logger.error("Error sending email verification code to {}: {}", email, e.getMessage());
            return false;
        }
    }

    /**
     * Verify phone number with code
     */
    public boolean verifyPhoneNumber(String phoneNumber, String code) {
        try {
            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
            if (userOpt.isEmpty()) {
                logger.error("User not found for phone number: {}", phoneNumber);
                return false;
            }

            User user = userOpt.get();

            // Check if code matches
            if (user.getPhoneVerificationCode() == null || !user.getPhoneVerificationCode().equals(code)) {
                logger.warn("Invalid verification code for phone: {}", phoneNumber);
                return false;
            }

            // Check if code has expired
            if (user.getPhoneVerificationCodeExpiry() == null || 
                user.getPhoneVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
                logger.warn("Verification code expired for phone: {}", phoneNumber);
                // Clear expired code
                user.setPhoneVerificationCode(null);
                user.setPhoneVerificationCodeExpiry(null);
                userRepository.save(user);
                return false;
            }

            // Verify the phone number
            user.setPhoneVerified(true);
            user.setPhoneVerificationCode(null);
            user.setPhoneVerificationCodeExpiry(null);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            logger.info("Phone number verified successfully: {}", phoneNumber);
            return true;
        } catch (Exception e) {
            logger.error("Error verifying phone number {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }

    /**
     * Verify email with code
     */
    public boolean verifyEmail(String email, String code) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                logger.error("User not found for email: {}", email);
                return false;
            }

            User user = userOpt.get();

            // Check if code matches
            if (user.getEmailVerificationCode() == null || !user.getEmailVerificationCode().equals(code)) {
                logger.warn("Invalid verification code for email: {}", email);
                return false;
            }

            // Check if code has expired
            if (user.getEmailVerificationCodeExpiry() == null || 
                user.getEmailVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
                logger.warn("Verification code expired for email: {}", email);
                // Clear expired code
                user.setEmailVerificationCode(null);
                user.setEmailVerificationCodeExpiry(null);
                userRepository.save(user);
                return false;
            }

            // Verify the email
            user.setEmailVerified(true);
            user.setEmailVerificationCode(null);
            user.setEmailVerificationCodeExpiry(null);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            logger.info("Email verified successfully: {}", email);
            return true;
        } catch (Exception e) {
            logger.error("Error verifying email {}: {}", email, e.getMessage());
            return false;
        }
    }

    /**
     * Check if phone number is verified
     */
    public boolean isPhoneVerified(String phoneNumber) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        return userOpt.map(User::isPhoneVerified).orElse(false);
    }

    /**
     * Check if email is verified
     */
    public boolean isEmailVerified(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.map(User::isEmailVerified).orElse(false);
    }
}

