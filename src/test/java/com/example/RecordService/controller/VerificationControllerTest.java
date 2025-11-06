package com.example.RecordService.controller;

import com.example.RecordService.model.User;
import com.example.RecordService.model.dto.EmailVerificationRequest;
import com.example.RecordService.model.dto.VerificationRequest;
import com.example.RecordService.repository.UserRepository;
import com.example.RecordService.service.VerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VerificationController.class)
class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VerificationService verificationService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

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

    // Test Send Phone Verification Code
    @Test
    void testSendPhoneVerificationCode_ShouldReturnSuccess() throws Exception {
        when(verificationService.sendPhoneVerificationCode(testPhoneNumber)).thenReturn(true);

        Map<String, String> request = new HashMap<>();
        request.put("phoneNumber", testPhoneNumber);

        mockMvc.perform(post("/api/verification/phone/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verification code sent successfully"));

        verify(verificationService, times(1)).sendPhoneVerificationCode(testPhoneNumber);
    }

    @Test
    void testSendPhoneVerificationCode_ShouldReturnErrorWhenUserNotFound() throws Exception {
        when(verificationService.sendPhoneVerificationCode(testPhoneNumber)).thenReturn(false);

        Map<String, String> request = new HashMap<>();
        request.put("phoneNumber", testPhoneNumber);

        mockMvc.perform(post("/api/verification/phone/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Failed to send verification code. User may not exist."));

        verify(verificationService, times(1)).sendPhoneVerificationCode(testPhoneNumber);
    }

    @Test
    void testSendPhoneVerificationCode_ShouldReturnErrorWhenPhoneNumberMissing() throws Exception {
        Map<String, String> request = new HashMap<>();
        // phoneNumber is missing

        mockMvc.perform(post("/api/verification/phone/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Phone number is required"));

        verify(verificationService, never()).sendPhoneVerificationCode(anyString());
    }

    @Test
    void testSendPhoneVerificationCode_ShouldReturnErrorWhenPhoneNumberEmpty() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("phoneNumber", "");

        mockMvc.perform(post("/api/verification/phone/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Phone number is required"));

        verify(verificationService, never()).sendPhoneVerificationCode(anyString());
    }

    // Test Verify Phone Number
    @Test
    void testVerifyPhoneNumber_ShouldReturnSuccess() throws Exception {
        when(verificationService.verifyPhoneNumber(testPhoneNumber, "123456")).thenReturn(true);

        VerificationRequest request = new VerificationRequest(testPhoneNumber, "123456");

        mockMvc.perform(post("/api/verification/phone/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Phone number verified successfully"))
                .andExpect(jsonPath("$.verified").value("true"));

        verify(verificationService, times(1)).verifyPhoneNumber(testPhoneNumber, "123456");
    }

    @Test
    void testVerifyPhoneNumber_ShouldReturnErrorForInvalidCode() throws Exception {
        when(verificationService.verifyPhoneNumber(testPhoneNumber, "123456")).thenReturn(false);

        VerificationRequest request = new VerificationRequest(testPhoneNumber, "123456");

        mockMvc.perform(post("/api/verification/phone/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid or expired verification code"));

        verify(verificationService, times(1)).verifyPhoneNumber(testPhoneNumber, "123456");
    }

    @Test
    void testVerifyPhoneNumber_ShouldReturnErrorForInvalidRequest() throws Exception {
        VerificationRequest request = new VerificationRequest("", "123"); // Invalid phone and code

        mockMvc.perform(post("/api/verification/phone/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(verificationService, never()).verifyPhoneNumber(anyString(), anyString());
    }

    // Test Send Email Verification Code
    @Test
    void testSendEmailVerificationCode_ShouldReturnSuccess() throws Exception {
        when(verificationService.sendEmailVerificationCode(testEmail)).thenReturn(true);

        Map<String, String> request = new HashMap<>();
        request.put("email", testEmail);

        mockMvc.perform(post("/api/verification/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verification code sent successfully"));

        verify(verificationService, times(1)).sendEmailVerificationCode(testEmail);
    }

    @Test
    void testSendEmailVerificationCode_ShouldReturnErrorWhenUserNotFound() throws Exception {
        when(verificationService.sendEmailVerificationCode(testEmail)).thenReturn(false);

        Map<String, String> request = new HashMap<>();
        request.put("email", testEmail);

        mockMvc.perform(post("/api/verification/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Failed to send verification code. User may not exist."));

        verify(verificationService, times(1)).sendEmailVerificationCode(testEmail);
    }

    @Test
    void testSendEmailVerificationCode_ShouldReturnErrorWhenEmailMissing() throws Exception {
        Map<String, String> request = new HashMap<>();
        // email is missing

        mockMvc.perform(post("/api/verification/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is required"));

        verify(verificationService, never()).sendEmailVerificationCode(anyString());
    }

    // Test Verify Email
    @Test
    void testVerifyEmail_ShouldReturnSuccess() throws Exception {
        when(verificationService.verifyEmail(testEmail, "123456")).thenReturn(true);

        EmailVerificationRequest request = new EmailVerificationRequest(testEmail, "123456");

        mockMvc.perform(post("/api/verification/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email verified successfully"))
                .andExpect(jsonPath("$.verified").value("true"));

        verify(verificationService, times(1)).verifyEmail(testEmail, "123456");
    }

    @Test
    void testVerifyEmail_ShouldReturnErrorForInvalidCode() throws Exception {
        when(verificationService.verifyEmail(testEmail, "123456")).thenReturn(false);

        EmailVerificationRequest request = new EmailVerificationRequest(testEmail, "123456");

        mockMvc.perform(post("/api/verification/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid or expired verification code"));

        verify(verificationService, times(1)).verifyEmail(testEmail, "123456");
    }

    @Test
    void testVerifyEmail_ShouldReturnErrorForInvalidRequest() throws Exception {
        EmailVerificationRequest request = new EmailVerificationRequest("invalid-email", "123"); // Invalid email and code

        mockMvc.perform(post("/api/verification/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(verificationService, never()).verifyEmail(anyString(), anyString());
    }

    // Test Check Phone Verification Status
    @Test
    void testCheckPhoneVerificationStatus_ShouldReturnVerified() throws Exception {
        when(verificationService.isPhoneVerified(testPhoneNumber)).thenReturn(true);

        mockMvc.perform(get("/api/verification/phone/{phoneNumber}/status", testPhoneNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value(testPhoneNumber))
                .andExpect(jsonPath("$.verified").value(true));

        verify(verificationService, times(1)).isPhoneVerified(testPhoneNumber);
    }

    @Test
    void testCheckPhoneVerificationStatus_ShouldReturnNotVerified() throws Exception {
        when(verificationService.isPhoneVerified(testPhoneNumber)).thenReturn(false);

        mockMvc.perform(get("/api/verification/phone/{phoneNumber}/status", testPhoneNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value(testPhoneNumber))
                .andExpect(jsonPath("$.verified").value(false));

        verify(verificationService, times(1)).isPhoneVerified(testPhoneNumber);
    }

    // Test Check Email Verification Status
    @Test
    void testCheckEmailVerificationStatus_ShouldReturnVerified() throws Exception {
        when(verificationService.isEmailVerified(testEmail)).thenReturn(true);

        mockMvc.perform(get("/api/verification/email/{email}/status", testEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testEmail))
                .andExpect(jsonPath("$.verified").value(true));

        verify(verificationService, times(1)).isEmailVerified(testEmail);
    }

    @Test
    void testCheckEmailVerificationStatus_ShouldReturnNotVerified() throws Exception {
        when(verificationService.isEmailVerified(testEmail)).thenReturn(false);

        mockMvc.perform(get("/api/verification/email/{email}/status", testEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testEmail))
                .andExpect(jsonPath("$.verified").value(false));

        verify(verificationService, times(1)).isEmailVerified(testEmail);
    }

    // Test Get User Verification Status
    @Test
    void testGetUserVerificationStatus_ShouldReturnFullStatus() throws Exception {
        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/verification/user/{phoneNumber}/status", testPhoneNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value(testPhoneNumber))
                .andExpect(jsonPath("$.email").value(testEmail))
                .andExpect(jsonPath("$.phoneVerified").value(false))
                .andExpect(jsonPath("$.emailVerified").value(false))
                .andExpect(jsonPath("$.userType").value("CLIENT"));

        verify(userRepository, times(1)).findByPhoneNumber(testPhoneNumber);
    }

    @Test
    void testGetUserVerificationStatus_ShouldReturnNotFoundWhenUserNotExists() throws Exception {
        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/verification/user/{phoneNumber}/status", testPhoneNumber))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(userRepository, times(1)).findByPhoneNumber(testPhoneNumber);
    }

    @Test
    void testGetUserVerificationStatus_ShouldReturnVerifiedStatus() throws Exception {
        testUser.setPhoneVerified(true);
        testUser.setEmailVerified(true);
        when(userRepository.findByPhoneNumber(testPhoneNumber)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/verification/user/{phoneNumber}/status", testPhoneNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneVerified").value(true))
                .andExpect(jsonPath("$.emailVerified").value(true));

        verify(userRepository, times(1)).findByPhoneNumber(testPhoneNumber);
    }
}

