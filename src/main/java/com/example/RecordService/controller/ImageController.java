package com.example.RecordService.controller;

import com.example.RecordService.model.Image;
import com.example.RecordService.model.Theme;
import com.example.RecordService.model.User;
import com.example.RecordService.service.ImageService;
import com.example.RecordService.service.ThemeService;
import com.example.RecordService.service.BusinessService;
import com.example.RecordService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageController {
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private ThemeService themeService;
    
    @Autowired
    private BusinessService businessService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private com.example.RecordService.service.AuthorizationService authorizationService;
    
    /**
     * POST endpoint to create a new image
     * @param image the image details to be saved
     * @param vendorPhone the vendor's phone number from header (for authorization)
     * @return ResponseEntity with the created image and HTTP status
     */
    @PostMapping
    public ResponseEntity<?> createImage(
            @RequestBody Image image,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
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
            
            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can upload images for any theme
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Only vendors or super admins can upload images."));
                    }
                    
                    // Get theme to verify ownership
                    Theme theme = themeService.getThemeById(image.getThemeId());
                    if (theme == null) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "Theme not found."));
                    }
                    
                    // Verify vendor owns the business
                    com.example.RecordService.model.Business business = businessService.getBusinessById(theme.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        // Check if vendor owns the business through any of their businesses
                        List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(theme.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Map.of("error", "You can only upload images for your own products."));
                        }
                    }
                }
            } else {
                // If no vendor phone provided but trying to create, deny access
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only vendors or super admins can upload images."));
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
     * @param vendorPhone the vendor's phone number from header (for authorization)
     * @return ResponseEntity with the updated image
     */
    @PutMapping("/{imageId}")
    public ResponseEntity<?> updateImage(
            @PathVariable String imageId, 
            @RequestBody Image image,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Get existing image to get themeId
            Image existingImage = imageService.getImageById(imageId);
            if (existingImage == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // First, verify user is a VENDOR, not a CLIENT
                User user = userService.getUserByPhoneNumber(vendorPhone);
                if (user == null || user.getUserType() != User.UserType.VENDOR) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Only vendors or super admins can update images."));
                }
                
                // Get theme to verify ownership
                Theme theme = themeService.getThemeById(existingImage.getThemeId());
                if (theme == null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Theme not found."));
                }
                
                // Verify vendor owns the business
                com.example.RecordService.model.Business business = businessService.getBusinessById(theme.getBusinessId());
                if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                    // Check if vendor owns the business through any of their businesses
                    List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                    boolean ownsBusiness = vendorBusinesses.stream()
                            .anyMatch(b -> b.getBusinessId().equals(theme.getBusinessId()));
                    if (!ownsBusiness) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "You can only update images for your own products."));
                    }
                }
            } else {
                // If no vendor phone provided but trying to update, deny access
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only vendors can update images."));
            }
            
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
    public ResponseEntity<?> setPrimaryImage(
            @PathVariable String imageId,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Get existing image to get themeId
            Image existingImage = imageService.getImageById(imageId);
            if (existingImage == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can set primary images for any theme
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Only vendors or super admins can set primary images."));
                    }
                    
                    // Get theme to verify ownership
                Theme theme = themeService.getThemeById(existingImage.getThemeId());
                if (theme == null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Theme not found."));
                }
                
                // Verify vendor owns the business
                com.example.RecordService.model.Business business = businessService.getBusinessById(theme.getBusinessId());
                if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                    // Check if vendor owns the business through any of their businesses
                    List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                    boolean ownsBusiness = vendorBusinesses.stream()
                            .anyMatch(b -> b.getBusinessId().equals(theme.getBusinessId()));
                    if (!ownsBusiness) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "You can only set primary images for your own products."));
                    }
                }
                }
            } else {
                // If no vendor phone provided but trying to set primary, deny access
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only vendors or super admins can set primary images."));
            }
            
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
     * @param vendorPhone the vendor's phone number from header (for authorization)
     * @return ResponseEntity with success status
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(
            @PathVariable String imageId,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Get existing image to get themeId
            Image existingImage = imageService.getImageById(imageId);
            if (existingImage == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // First, verify user is a VENDOR, not a CLIENT
                User user = userService.getUserByPhoneNumber(vendorPhone);
                if (user == null || user.getUserType() != User.UserType.VENDOR) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Only vendors or super admins can delete images."));
                }
                
                // Get theme to verify ownership
                Theme theme = themeService.getThemeById(existingImage.getThemeId());
                if (theme == null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Theme not found."));
                }
                
                // Verify vendor owns the business
                com.example.RecordService.model.Business business = businessService.getBusinessById(theme.getBusinessId());
                if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                    // Check if vendor owns the business through any of their businesses
                    List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                    boolean ownsBusiness = vendorBusinesses.stream()
                            .anyMatch(b -> b.getBusinessId().equals(theme.getBusinessId()));
                    if (!ownsBusiness) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "You can only delete images for your own products."));
                    }
                }
            } else {
                // If no vendor phone provided but trying to delete, deny access
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only vendors can delete images."));
            }
            
            boolean deleted = imageService.deleteImage(imageId);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
