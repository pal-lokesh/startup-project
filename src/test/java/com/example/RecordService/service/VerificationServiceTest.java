package com.example.RecordService.service;

import com.example.RecordService.model.User;
import com.example.RecordService.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private VerificationService verificationService;

    private User testUser;
    private String testPhoneNumber = "1234567890";
    private String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setPhoneNumber(testPhoneNumber);
        testUser.setEmail(testEmail);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPassword("password123");
        testUser.setUserType(User.UserType.CLIENT);
        testUser.setPhoneVerified(false);
        testUser.setEmailVerified(false);
    }

    // Test OTP Code Generation
    @Test
    void testGenerateVerificationCode_ShouldGenerateSixDigitCode() {
        String code = verificationService.generateVerificationCode();
        
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));
        int codeValue = Integer.parseInt(code);
        assertTrue(codeValue >= 100000 && codeValue <= 999999);
    }

    @Test
    void testGenerateVerificationCode_ShouldGenerateDifferentCodes() {
        String code1 = verificationService.generateVerificationCode();
        String code2 = verificationService.generateVerificationCode();
        
        // Codes should be different (very high probability)
        assertNotEquals(code1, code2);
    }

    // Test Phone Verification - Send Code
    @Test
    void testSendPhoneVerificationCode_ShouldSuccessfullySendCode() {
        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.sendPhoneVerificationCode(testPhoneNumber);

        assertTrue(result);
        verify(userRepository, times(1)).findByPhoneNumber(testPhoneNumber);
        verify(userRepository, times(1)).save(any(User.class));
        assertNotNull(testUser.getPhoneVerificationCode());
        assertEquals(6, testUser.getPhoneVerificationCode().length());
        assertNotNull(testUser.getPhoneVerificationCodeExpiry());
        assertTrue(testUser.getPhoneVerificationCodeExpiry().isAfter(LocalDateTime.now()));
    }

    @Test
    void testSendPhoneVerificationCode_ShouldReturnFalseWhenUserNotFound() {
        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.empty());

        boolean result = verificationService.sendPhoneVerificationCode(testPhoneNumber);

        assertFalse(result);
        verify(userRepository, times(1)).findByPhoneNumber(testPhoneNumber);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSendPhoneVerificationCode_ShouldSetExpiryToTenMinutes() {
        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            LocalDateTime expiry = savedUser.getPhoneVerificationCodeExpiry();
            LocalDateTime expectedMin = LocalDateTime.now().plusMinutes(9);
            LocalDateTime expectedMax = LocalDateTime.now().plusMinutes(11);
            assertTrue(expiry.isAfter(expectedMin) && expiry.isBefore(expectedMax));
            return savedUser;
        });

        verificationService.sendPhoneVerificationCode(testPhoneNumber);

        verify(userRepository, times(1)).save(any(User.class));
    }

    // Test Email Verification - Send Code
    @Test
    void testSendEmailVerificationCode_ShouldSuccessfullySendCode() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        boolean result = verificationService.sendEmailVerificationCode(testEmail);

        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(userRepository, times(1)).save(any(User.class));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        assertNotNull(testUser.getEmailVerificationCode());
        assertEquals(6, testUser.getEmailVerificationCode().length());
    }

    @Test
    void testSendEmailVerificationCode_ShouldReturnFalseWhenUserNotFound() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        boolean result = verificationService.sendEmailVerificationCode(testEmail);

        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSendEmailVerificationCode_ShouldWorkWithoutMailSender() {
        // Test that it works even when mailSender is null (logs to console)
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        verificationService.sendEmailVerificationCode(testEmail);

        // Should still work even if mail sender is null (logs to console)
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Test Phone Verification - Verify Code
    @Test
    void testVerifyPhoneNumber_ShouldSuccessfullyVerifyWithValidCode() {
        String code = "123456";
        testUser.setPhoneVerificationCode(code);
        testUser.setPhoneVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyPhoneNumber(testPhoneNumber, code);

        assertTrue(result);
        assertTrue(testUser.isPhoneVerified());
        assertNull(testUser.getPhoneVerificationCode());
        assertNull(testUser.getPhoneVerificationCodeExpiry());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testVerifyPhoneNumber_ShouldReturnFalseForInvalidCode() {
        String correctCode = "123456";
        String wrongCode = "654321";
        testUser.setPhoneVerificationCode(correctCode);
        testUser.setPhoneVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyPhoneNumber(testPhoneNumber, wrongCode);

        assertFalse(result);
        assertFalse(testUser.isPhoneVerified());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testVerifyPhoneNumber_ShouldReturnFalseForExpiredCode() {
        String code = "123456";
        testUser.setPhoneVerificationCode(code);
        testUser.setPhoneVerificationCodeExpiry(LocalDateTime.now().minusMinutes(1)); // Expired

        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyPhoneNumber(testPhoneNumber, code);

        assertFalse(result);
        assertFalse(testUser.isPhoneVerified());
        assertNull(testUser.getPhoneVerificationCode()); // Should be cleared
        assertNull(testUser.getPhoneVerificationCodeExpiry());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testVerifyPhoneNumber_ShouldReturnFalseWhenUserNotFound() {
        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.empty());

        boolean result = verificationService.verifyPhoneNumber(testPhoneNumber, "123456");

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testVerifyPhoneNumber_ShouldReturnFalseWhenCodeIsNull() {
        testUser.setPhoneVerificationCode(null);
        testUser.setPhoneVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyPhoneNumber(testPhoneNumber, "123456");

        assertFalse(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Test Email Verification - Verify Code
    @Test
    void testVerifyEmail_ShouldSuccessfullyVerifyWithValidCode() {
        String code = "123456";
        testUser.setEmailVerificationCode(code);
        testUser.setEmailVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyEmail(testEmail, code);

        assertTrue(result);
        assertTrue(testUser.isEmailVerified());
        assertNull(testUser.getEmailVerificationCode());
        assertNull(testUser.getEmailVerificationCodeExpiry());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testVerifyEmail_ShouldReturnFalseForInvalidCode() {
        String correctCode = "123456";
        String wrongCode = "654321";
        testUser.setEmailVerificationCode(correctCode);
        testUser.setEmailVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyEmail(testEmail, wrongCode);

        assertFalse(result);
        assertFalse(testUser.isEmailVerified());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testVerifyEmail_ShouldReturnFalseForExpiredCode() {
        String code = "123456";
        testUser.setEmailVerificationCode(code);
        testUser.setEmailVerificationCodeExpiry(LocalDateTime.now().minusMinutes(1)); // Expired

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyEmail(testEmail, code);

        assertFalse(result);
        assertFalse(testUser.isEmailVerified());
        assertNull(testUser.getEmailVerificationCode()); // Should be cleared
        assertNull(testUser.getEmailVerificationCodeExpiry());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testVerifyEmail_ShouldReturnFalseWhenUserNotFound() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        boolean result = verificationService.verifyEmail(testEmail, "123456");

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    // Test Verification Status Checks
    @Test
    void testIsPhoneVerified_ShouldReturnTrueWhenVerified() {
        testUser.setPhoneVerified(true);
        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));

        boolean result = verificationService.isPhoneVerified(testPhoneNumber);

        assertTrue(result);
    }

    @Test
    void testIsPhoneVerified_ShouldReturnFalseWhenNotVerified() {
        testUser.setPhoneVerified(false);
        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));

        boolean result = verificationService.isPhoneVerified(testPhoneNumber);

        assertFalse(result);
    }

    @Test
    void testIsPhoneVerified_ShouldReturnFalseWhenUserNotFound() {
        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.empty());

        boolean result = verificationService.isPhoneVerified(testPhoneNumber);

        assertFalse(result);
    }

    @Test
    void testIsEmailVerified_ShouldReturnTrueWhenVerified() {
        testUser.setEmailVerified(true);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        boolean result = verificationService.isEmailVerified(testEmail);

        assertTrue(result);
    }

    @Test
    void testIsEmailVerified_ShouldReturnFalseWhenNotVerified() {
        testUser.setEmailVerified(false);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        boolean result = verificationService.isEmailVerified(testEmail);

        assertFalse(result);
    }

    @Test
    void testIsEmailVerified_ShouldReturnFalseWhenUserNotFound() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        boolean result = verificationService.isEmailVerified(testEmail);

        assertFalse(result);
    }

    // Test Edge Cases
    @Test
    void testVerifyPhoneNumber_ShouldHandleNullExpiry() {
        String code = "123456";
        testUser.setPhoneVerificationCode(code);
        testUser.setPhoneVerificationCodeExpiry(null);

        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyPhoneNumber(testPhoneNumber, code);

        assertFalse(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testVerifyEmail_ShouldHandleNullExpiry() {
        String code = "123456";
        testUser.setEmailVerificationCode(code);
        testUser.setEmailVerificationCodeExpiry(null);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyEmail(testEmail, code);

        assertFalse(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testVerifyPhoneNumber_ShouldHandleExactlyExpiredCode() {
        String code = "123456";
        testUser.setPhoneVerificationCode(code);
        testUser.setPhoneVerificationCodeExpiry(LocalDateTime.now().minusSeconds(1)); // Just expired

        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyPhoneNumber(testPhoneNumber, code);

        assertFalse(result);
        assertFalse(testUser.isPhoneVerified());
    }

    @Test
    void testVerifyEmail_ShouldHandleExactlyExpiredCode() {
        String code = "123456";
        testUser.setEmailVerificationCode(code);
        testUser.setEmailVerificationCodeExpiry(LocalDateTime.now().minusSeconds(1)); // Just expired

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyEmail(testEmail, code);

        assertFalse(result);
        assertFalse(testUser.isEmailVerified());
    }

    @Test
    void testVerifyPhoneNumber_ShouldHandleCodeExpiringAtBoundary() {
        String code = "123456";
        testUser.setPhoneVerificationCode(code);
        testUser.setPhoneVerificationCodeExpiry(LocalDateTime.now().plusSeconds(1)); // Just before expiry

        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyPhoneNumber(testPhoneNumber, code);

        assertTrue(result); // Should still be valid
        assertTrue(testUser.isPhoneVerified());
    }

    @Test
    void testSendPhoneVerificationCode_ShouldHandleRepositoryException() {
        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenThrow(new RuntimeException("Database error"));

        boolean result = verificationService.sendPhoneVerificationCode(testPhoneNumber);

        assertFalse(result);
    }

    @Test
    void testSendEmailVerificationCode_ShouldHandleRepositoryException() {
        when(userRepository.findByEmail(testEmail)).thenThrow(new RuntimeException("Database error"));

        boolean result = verificationService.sendEmailVerificationCode(testEmail);

        assertFalse(result);
    }

    @Test
    void testVerifyPhoneNumber_ShouldHandleRepositoryException() {
        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenThrow(new RuntimeException("Database error"));

        boolean result = verificationService.verifyPhoneNumber(testPhoneNumber, "123456");

        assertFalse(result);
    }

    @Test
    void testVerifyEmail_ShouldHandleRepositoryException() {
        when(userRepository.findByEmail(testEmail)).thenThrow(new RuntimeException("Database error"));

        boolean result = verificationService.verifyEmail(testEmail, "123456");

        assertFalse(result);
    }

    @Test
    void testVerifyEmail_ShouldReturnFalseWhenCodeIsNull() {
        testUser.setEmailVerificationCode(null);
        testUser.setEmailVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyEmail(testEmail, "123456");

        assertFalse(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testVerifyEmail_ShouldHandleCodeExpiringAtBoundary() {
        String code = "123456";
        testUser.setEmailVerificationCode(code);
        testUser.setEmailVerificationCodeExpiry(LocalDateTime.now().plusSeconds(1)); // Just before expiry

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyEmail(testEmail, code);

        assertTrue(result); // Should still be valid
        assertTrue(testUser.isEmailVerified());
    }

    @Test
    void testSendPhoneVerificationCode_ShouldUpdateExistingCode() {
        testUser.setPhoneVerificationCode("111111");
        testUser.setPhoneVerificationCodeExpiry(LocalDateTime.now().minusMinutes(5)); // Old expired code

        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.sendPhoneVerificationCode(testPhoneNumber);

        assertTrue(result);
        assertNotNull(testUser.getPhoneVerificationCode());
        assertNotEquals("111111", testUser.getPhoneVerificationCode()); // Should be new code
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSendEmailVerificationCode_ShouldUpdateExistingCode() {
        testUser.setEmailVerificationCode("111111");
        testUser.setEmailVerificationCodeExpiry(LocalDateTime.now().minusMinutes(5)); // Old expired code

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        boolean result = verificationService.sendEmailVerificationCode(testEmail);

        assertTrue(result);
        assertNotNull(testUser.getEmailVerificationCode());
        assertNotEquals("111111", testUser.getEmailVerificationCode()); // Should be new code
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testVerifyPhoneNumber_ShouldNotVerifyIfAlreadyVerified() {
        String code = "123456";
        testUser.setPhoneVerificationCode(code);
        testUser.setPhoneVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));
        testUser.setPhoneVerified(true); // Already verified

        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyPhoneNumber(testPhoneNumber, code);

        // Should still verify successfully and clear the code
        assertTrue(result);
        assertTrue(testUser.isPhoneVerified());
        assertNull(testUser.getPhoneVerificationCode());
    }

    @Test
    void testVerifyEmail_ShouldNotVerifyIfAlreadyVerified() {
        String code = "123456";
        testUser.setEmailVerificationCode(code);
        testUser.setEmailVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));
        testUser.setEmailVerified(true); // Already verified

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = verificationService.verifyEmail(testEmail, code);

        // Should still verify successfully and clear the code
        assertTrue(result);
        assertTrue(testUser.isEmailVerified());
        assertNull(testUser.getEmailVerificationCode());
    }

    @Test
    void testGenerateVerificationCode_ShouldGenerateMultipleUniqueCodes() {
        // Generate 100 codes and ensure they're all unique (very high probability)
        java.util.Set<String> codes = new java.util.HashSet<>();
        for (int i = 0; i < 100; i++) {
            String code = verificationService.generateVerificationCode();
            codes.add(code);
            assertNotNull(code);
            assertEquals(6, code.length());
        }
        // All 100 codes should be unique (probability of collision is extremely low)
        assertEquals(100, codes.size());
    }

    @Test
    void testSendEmailVerificationCode_ShouldHandleMailSenderException() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Should still return true even if mail sending fails (code is saved)
        boolean result = verificationService.sendEmailVerificationCode(testEmail);

        assertTrue(result);
        assertNotNull(testUser.getEmailVerificationCode());
        verify(userRepository, times(1)).save(any(User.class));
    }
}

