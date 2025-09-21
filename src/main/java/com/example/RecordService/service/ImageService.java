package com.example.RecordService.service;

import com.example.RecordService.model.Image;
import com.example.RecordService.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {
    
    @Autowired
    private ImageRepository imageRepository;
    
    /**
     * Save a new image
     * @param image the image to be saved
     * @return the saved image
     */
    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }
    
    /**
     * Get all images
     * @return list of all images
     */
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }
    
    /**
     * Get image by image ID
     * @param imageId the image ID
     * @return the image if found, null otherwise
     */
    public Image getImageById(String imageId) {
        return imageRepository.findByImageId(imageId);
    }
    
    /**
     * Get images by theme ID
     * @param themeId the theme ID
     * @return list of images for the specified theme
     */
    public List<Image> getImagesByThemeId(String themeId) {
        return imageRepository.findByThemeId(themeId);
    }
    
    /**
     * Get primary images
     * @param isPrimary primary status
     * @return list of images with specified primary status
     */
    public List<Image> getImagesByPrimary(boolean isPrimary) {
        return imageRepository.findByPrimary(isPrimary);
    }
    
    /**
     * Check if image exists by image ID
     * @param imageId the image ID
     * @return true if image exists, false otherwise
     */
    public boolean imageExistsById(String imageId) {
        return imageRepository.existsByImageId(imageId);
    }
    
    /**
     * Update an existing image
     * @param image the updated image data
     * @return the updated image
     */
    public Image updateImage(Image image) {
        return imageRepository.update(image);
    }
    
    /**
     * Set an image as primary for a theme
     * @param imageId the image ID
     * @return the updated image
     */
    public Image setPrimaryImage(String imageId) {
        Image image = imageRepository.findByImageId(imageId);
        if (image != null) {
            // Remove primary status from other images in the same theme
            List<Image> themeImages = imageRepository.findByThemeId(image.getThemeId());
            for (Image themeImage : themeImages) {
                if (themeImage.isPrimary()) {
                    themeImage.setPrimary(false);
                    imageRepository.update(themeImage);
                }
            }
            
            // Set this image as primary
            image.setPrimary(true);
            return imageRepository.update(image);
        }
        return null;
    }
    
    /**
     * Delete an image by image ID
     * @param imageId the image ID
     * @return true if image was deleted, false if not found
     */
    public boolean deleteImage(String imageId) {
        return imageRepository.delete(imageId);
    }
    
    /**
     * Get total number of images
     * @return count of images
     */
    public long getImageCount() {
        return imageRepository.count();
    }
}
