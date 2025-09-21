package com.example.RecordService.model;

import java.time.LocalDateTime;

public class Image {
    private String imageId; // Primary key
    private String themeId; // Foreign key to Theme
    private String imageName;
    private String imageUrl;
    private String imagePath;
    private long imageSize;
    private String imageType;
    private boolean isPrimary;
    private LocalDateTime uploadedAt;
    private String metadata; // JSON string for additional metadata

    // Default constructor
    public Image() {
        this.uploadedAt = LocalDateTime.now();
        this.isPrimary = false;
    }

    // Constructor with parameters
    public Image(String themeId, String imageName, String imageUrl, String imagePath, 
                long imageSize, String imageType) {
        this.themeId = themeId;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.imagePath = imagePath;
        this.imageSize = imageSize;
        this.imageType = imageType;
        this.uploadedAt = LocalDateTime.now();
        this.isPrimary = false;
    }

    // Getters and Setters
    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getImageSize() {
        return imageSize;
    }

    public void setImageSize(long imageSize) {
        this.imageSize = imageSize;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageId='" + imageId + '\'' +
                ", themeId='" + themeId + '\'' +
                ", imageName='" + imageName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", imageSize=" + imageSize +
                ", imageType='" + imageType + '\'' +
                ", isPrimary=" + isPrimary +
                ", uploadedAt=" + uploadedAt +
                ", metadata='" + metadata + '\'' +
                '}';
    }
}
