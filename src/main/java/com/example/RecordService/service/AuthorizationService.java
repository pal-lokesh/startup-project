package com.example.RecordService.service;

import com.example.RecordService.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for handling authorization checks
 * Provides utility methods to check if a user has permission to perform actions
 */
@Service
public class AuthorizationService {

    @Autowired
    private UserService userService;

    /**
     * Check if a user is a super admin
     * @param phoneNumber The phone number of the user to check
     * @return true if the user is a SUPER_ADMIN, false otherwise
     */
    public boolean isSuperAdmin(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        User user = userService.getUserByPhoneNumber(phoneNumber);
        return user != null && user.getRole() == User.Role.SUPER_ADMIN;
    }

    /**
     * Check if a user can access/manipulate data (either owns it or is super admin)
     * @param phoneNumber The phone number of the user requesting access
     * @param ownerPhoneNumber The phone number of the owner of the data
     * @return true if user is super admin or owns the data, false otherwise
     */
    public boolean canAccessData(String phoneNumber, String ownerPhoneNumber) {
        if (isSuperAdmin(phoneNumber)) {
            return true; // Super admin can access all data
        }
        return phoneNumber != null && phoneNumber.equals(ownerPhoneNumber);
    }

    /**
     * Check if a user is a vendor (for vendor-specific operations)
     * @param phoneNumber The phone number of the user to check
     * @return true if the user is a VENDOR, false otherwise
     */
    public boolean isVendor(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        User user = userService.getUserByPhoneNumber(phoneNumber);
        return user != null && user.getUserType() == User.UserType.VENDOR;
    }

    /**
     * Check if a user can perform vendor operations (either is vendor or super admin)
     * @param phoneNumber The phone number of the user to check
     * @return true if user is vendor or super admin, false otherwise
     */
    public boolean canPerformVendorOperations(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        User user = userService.getUserByPhoneNumber(phoneNumber);
        if (user == null) {
            return false;
        }
        return user.getRole() == User.Role.SUPER_ADMIN || user.getUserType() == User.UserType.VENDOR;
    }
}

