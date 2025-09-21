package com.example.RecordService.model;

import java.time.LocalDateTime;

public class Vendor {
    private String vendorId; // Primary key
    private String phoneNumber; // Foreign key to User
    private String businessName;
    private String businessDescription;
    private String businessAddress;
    private String businessPhone;
    private String businessEmail;
    private String registrationNumber;
    private String taxId;
    private boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Reference to Business (One-to-One) - will be handled by service layer
    // private Business business;

    // Default constructor
    public Vendor() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isVerified = false;
    }

    // Constructor with parameters
    public Vendor(String phoneNumber, String businessName, String businessDescription, 
                  String businessAddress, String businessPhone, String businessEmail) {
        this.phoneNumber = phoneNumber;
        this.businessName = businessName;
        this.businessDescription = businessDescription;
        this.businessAddress = businessAddress;
        this.businessPhone = businessPhone;
        this.businessEmail = businessEmail;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isVerified = false;
    }

    // Getters and Setters
    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Business relationship handled by service layer
    // public Business getBusiness() {
    //     return business;
    // }
    //
    // public void setBusiness(Business business) {
    //     this.business = business;
    // }

    @Override
    public String toString() {
        return "Vendor{" +
                "vendorId='" + vendorId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", businessName='" + businessName + '\'' +
                ", businessDescription='" + businessDescription + '\'' +
                ", businessAddress='" + businessAddress + '\'' +
                ", businessPhone='" + businessPhone + '\'' +
                ", businessEmail='" + businessEmail + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", taxId='" + taxId + '\'' +
                ", isVerified=" + isVerified +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
