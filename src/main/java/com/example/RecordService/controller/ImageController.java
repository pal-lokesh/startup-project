package com.example.RecordService.controller;

import com.example.RecordService.model.Image;
import com.example.RecordService.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageController {
    
    @Autowired
    private ImageService imageService;
    
    /**
     * POST endpoint to create a new image
     * @param image the image details to be saved
     * @return ResponseEntity with the created image and HTTP status
     */
    @PostMapping
    public ResponseEntity<Image> createImage(@RequestBody Image image) {
        try {
            // Validate required fields
            if (image.getThemeId() == null || image.getThemeId().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (image.getImageName() == null || image.getImageName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (image.getImageUrl() == null || image.getImageUrl().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Save the image
            Image savedImage = imageService.saveImage(image);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedImage);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET endpoint to retrieve all images
     * @return ResponseEntity with list of all images
     */
    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        List<Image> images = imageService.getAllImages();
        return ResponseEntity.ok(images);
    }
    
    /**
     * GET endpoint to retrieve an image by image ID
     * @param imageId the image ID
     * @return ResponseEntity with the image if found
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<Image> getImageById(@PathVariable String imageId) {
        Image image = imageService.getImageById(imageId);
        if (image != null) {
            return ResponseEntity.ok(image);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET endpoint to retrieve images by theme ID
     * @param themeId the theme ID
     * @return ResponseEntity with list of images for the specified theme
     */
    @GetMapping("/theme/{themeId}")
    public ResponseEntity<List<Image>> getImagesByThemeId(@PathVariable String themeId) {
        List<Image> images = imageService.getImagesByThemeId(themeId);
        return ResponseEntity.ok(images);
    }
    
    /**
     * GET endpoint to retrieve primary images
     * @param isPrimary primary status
     * @return ResponseEntity with list of images with specified primary status
     */
    @GetMapping("/primary/{isPrimary}")
    public ResponseEntity<List<Image>> getImagesByPrimary(@PathVariable boolean isPrimary) {
        List<Image> images = imageService.getImagesByPrimary(isPrimary);
        return ResponseEntity.ok(images);
    }
    
    /**
     * PUT endpoint to update an existing image
     * @param imageId the image ID of the image to update
     * @param image the updated image data
     * @return ResponseEntity with the updated image
     */
    @PutMapping("/{imageId}")
    public ResponseEntity<Image> updateImage(@PathVariable String imageId, @RequestBody Image image) {
        try {
            image.setImageId(imageId); // Ensure image ID consistency
            Image updatedImage = imageService.updateImage(image);
            if (updatedImage != null) {
                return ResponseEntity.ok(updatedImage);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * POST endpoint to set an image as primary for its theme
     * @param imageId the image ID
     * @return ResponseEntity with the updated image
     */
    @PostMapping("/{imageId}/set-primary")
    public ResponseEntity<Image> setPrimaryImage(@PathVariable String imageId) {
        try {
            Image image = imageService.setPrimaryImage(imageId);
            if (image != null) {
                return ResponseEntity.ok(image);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * DELETE endpoint to delete an image by image ID
     * @param imageId the image ID
     * @return ResponseEntity with success status
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable String imageId) {
        boolean deleted = imageService.deleteImage(imageId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET endpoint to get the total count of images
     * @return ResponseEntity with image count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getImageCount() {
        long count = imageService.getImageCount();
        return ResponseEntity.ok(count);
    }
}
