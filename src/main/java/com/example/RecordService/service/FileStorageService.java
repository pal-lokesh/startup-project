package com.example.RecordService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    public String storeFile(MultipartFile file, String category, String itemId) {
        try {
            // Create directory structure: uploads/category/itemId/
            Path uploadPath = Paths.get(uploadDir, category, itemId);
            Files.createDirectories(uploadPath);
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Copy file to target location
            Path targetLocation = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // Return relative path for database storage
            return String.format("/uploads/%s/%s/%s", category, itemId, uniqueFilename);
            
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), ex);
        }
    }
    
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadDir + filePath);
            return Files.deleteIfExists(path);
        } catch (IOException ex) {
            return false;
        }
    }
    
    public byte[] loadFileAsBytes(String filePath) {
        try {
            Path path = Paths.get(uploadDir + filePath);
            return Files.readAllBytes(path);
        } catch (IOException ex) {
            throw new RuntimeException("Could not read file " + filePath, ex);
        }
    }
}
