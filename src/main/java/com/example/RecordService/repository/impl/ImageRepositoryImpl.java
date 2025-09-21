package com.example.RecordService.repository.impl;

import com.example.RecordService.model.Image;
import com.example.RecordService.repository.ImageRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ImageRepositoryImpl implements ImageRepository {
    
    private static final Map<String, Image> images = new ConcurrentHashMap<>();
    private static final Map<String, List<Image>> imagesByTheme = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);
    
    @Override
    public Image save(Image image) {
        if (image.getImageId() == null) {
            image.setImageId("IMAGE_" + idCounter.getAndIncrement());
        }
        images.put(image.getImageId(), image);
        
        // Update theme images mapping
        imagesByTheme.computeIfAbsent(image.getThemeId(), k -> new ArrayList<>()).add(image);
        
        return image;
    }
    
    @Override
    public Image findByImageId(String imageId) {
        return images.get(imageId);
    }
    
    @Override
    public List<Image> findByThemeId(String themeId) {
        return imagesByTheme.getOrDefault(themeId, new ArrayList<>());
    }
    
    @Override
    public List<Image> findByPrimary(boolean isPrimary) {
        return images.values().stream()
                .filter(image -> image.isPrimary() == isPrimary)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<Image> findAll() {
        return new ArrayList<>(images.values());
    }
    
    @Override
    public boolean existsByImageId(String imageId) {
        return images.containsKey(imageId);
    }
    
    @Override
    public Image update(Image image) {
        images.put(image.getImageId(), image);
        return image;
    }
    
    @Override
    public boolean delete(String imageId) {
        Image image = images.remove(imageId);
        if (image != null) {
            List<Image> themeImages = imagesByTheme.get(image.getThemeId());
            if (themeImages != null) {
                themeImages.removeIf(img -> img.getImageId().equals(imageId));
            }
            return true;
        }
        return false;
    }
    
    @Override
    public long count() {
        return images.size();
    }
}
