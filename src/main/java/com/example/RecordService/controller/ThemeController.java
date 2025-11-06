package com.example.RecordService.controller;

import com.example.RecordService.model.Theme;
import com.example.RecordService.model.dto.BusinessThemesResponse;
import com.example.RecordService.model.dto.BusinessThemeSummary;
import com.example.RecordService.service.ThemeService;
import com.example.RecordService.service.BusinessService;
import com.example.RecordService.service.UserService;
import com.example.RecordService.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/themes")
@CrossOrigin(origins = "*")
public class ThemeController {
    
    @Autowired
    private ThemeService themeService;
    
    @Autowired
    private BusinessService businessService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private com.example.RecordService.service.AuthorizationService authorizationService;
    
    /**
     * POST endpoint to create a new theme
     * @param theme the theme details to be saved
     * @param vendorPhone the vendor's phone number from header (for authorization)
     * @return ResponseEntity with the created theme and HTTP status
     */
    @PostMapping
    public ResponseEntity<?> createTheme(
            @RequestBody Theme theme,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Validate required fields
            if (theme.getBusinessId() == null || theme.getBusinessId().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (theme.getThemeName() == null || theme.getThemeName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (theme.getThemeCategory() == null || theme.getThemeCategory().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can create themes for any business
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Only vendors or super admins can create themes."));
                    }
                    
                    // Verify vendor owns the business
                    com.example.RecordService.model.Business business = businessService.getBusinessById(theme.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(theme.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Map.of("error", "You can only create themes for your own businesses."));
                        }
                    }
                }
            }
            
            // Save the theme
            Theme savedTheme = themeService.saveTheme(theme);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTheme);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * POST endpoint to create a new theme with image validation
     * This endpoint requires that at least one image is uploaded for the theme
     * @param theme the theme details to be saved
     * @return ResponseEntity with the created theme and HTTP status
     */
    @PostMapping("/with-images")
    public ResponseEntity<?> createThemeWithImages(
            @RequestBody Theme theme,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Validate required fields
            if (theme.getBusinessId() == null || theme.getBusinessId().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (theme.getThemeName() == null || theme.getThemeName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (theme.getThemeCategory() == null || theme.getThemeCategory().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Validate vendor authorization if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can create themes for any business
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                    
                    // Verify vendor owns the business
                    com.example.RecordService.model.Business business = businessService.getBusinessById(theme.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(theme.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                        }
                    }
                }
            }
            
            // Save the theme first
            Theme savedTheme = themeService.saveTheme(theme);
            
            // Check if theme has images - this will be validated by the frontend
            // The frontend should ensure images are uploaded before calling this endpoint
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTheme);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET endpoint to retrieve all themes
     * @return ResponseEntity with list of all themes
     */
    @GetMapping
    public ResponseEntity<List<Theme>> getAllThemes() {
        List<Theme> themes = themeService.getAllThemes();
        return ResponseEntity.ok(themes);
    }
    
    /**
     * GET endpoint to retrieve all businesses with their themes, grouped by business
     */
    @GetMapping("/by-business")
    public ResponseEntity<List<BusinessThemesResponse>> getThemesByBusinessGrouping() {
        List<BusinessThemesResponse> grouped = themeService.getThemesGroupedByBusiness();
        return ResponseEntity.ok(grouped);
    }
    
    /**
     * GET endpoint to retrieve minimal view: business name with themes (id and name only)
     */
    @GetMapping("/by-business/summary")
    public ResponseEntity<List<BusinessThemeSummary>> getThemesByBusinessSummary() {
        List<BusinessThemeSummary> grouped = themeService.getThemesByBusinessSummary();
        return ResponseEntity.ok(grouped);
    }
    
    /**
     * GET endpoint to retrieve a theme by theme ID
     * @param themeId the theme ID
     * @return ResponseEntity with the theme if found
     */
    @GetMapping("/{themeId}")
    public ResponseEntity<Theme> getThemeById(@PathVariable String themeId) {
        Theme theme = themeService.getThemeById(themeId);
        if (theme != null) {
            return ResponseEntity.ok(theme);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET endpoint to retrieve themes by business ID
     * @param businessId the business ID
     * @return ResponseEntity with list of themes for the specified business
     */
    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<Theme>> getThemesByBusinessId(@PathVariable String businessId) {
        List<Theme> themes = themeService.getThemesByBusinessId(businessId);
        return ResponseEntity.ok(themes);
    }
    
    /**
     * GET endpoint to retrieve themes by category
     * @param category the theme category
     * @return ResponseEntity with list of themes with specified category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Theme>> getThemesByCategory(@PathVariable String category) {
        List<Theme> themes = themeService.getThemesByCategory(category);
        return ResponseEntity.ok(themes);
    }
    
    /**
     * GET endpoint to retrieve active themes
     * @param active active status
     * @return ResponseEntity with list of themes with specified active status
     */
    @GetMapping("/active/{active}")
    public ResponseEntity<List<Theme>> getThemesByActive(@PathVariable boolean active) {
        List<Theme> themes = themeService.getThemesByActive(active);
        return ResponseEntity.ok(themes);
    }
    
    /**
     * PUT endpoint to update an existing theme
     * @param themeId the theme ID of the theme to update
     * @param theme the updated theme data
     * @param vendorPhone the vendor's phone number from header (for authorization)
     * @return ResponseEntity with the updated theme
     */
    @PutMapping("/{themeId}")
    public ResponseEntity<?> updateTheme(
            @PathVariable String themeId, 
            @RequestBody Theme theme,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        try {
            // Get existing theme to verify ownership
            Theme existingTheme = themeService.getThemeById(themeId);
            if (existingTheme == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Validate vendor ownership if vendor phone is provided
            if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
                // Super admin can update any theme
                if (!authorizationService.isSuperAdmin(vendorPhone)) {
                    if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Only vendors or super admins can update product details."));
                    }
                    
                    // Then verify ownership
                    com.example.RecordService.model.Business business = businessService.getBusinessById(existingTheme.getBusinessId());
                    if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                        // Check if vendor owns the business through any of their businesses
                        List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                        boolean ownsBusiness = vendorBusinesses.stream()
                                .anyMatch(b -> b.getBusinessId().equals(existingTheme.getBusinessId()));
                        if (!ownsBusiness) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Map.of("error", "You can only update your own products."));
                        }
                    }
                }
            } else {
                // If no vendor phone provided but trying to update, deny access
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only vendors or super admins can update product details."));
            }
            
            theme.setThemeId(themeId); // Ensure theme ID consistency
            Theme updatedTheme = themeService.updateTheme(theme);
            if (updatedTheme != null) {
                return ResponseEntity.ok(updatedTheme);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * DELETE endpoint to delete a theme by theme ID
     * @param themeId the theme ID
     * @param vendorPhone the vendor's phone number from header (for authorization)
     * @return ResponseEntity with success status
     */
    @DeleteMapping("/{themeId}")
    public ResponseEntity<Void> deleteTheme(
            @PathVariable String themeId,
            @RequestHeader(value = "X-Vendor-Phone", required = false) String vendorPhone) {
        // Get existing theme to verify ownership
        Theme existingTheme = themeService.getThemeById(themeId);
        if (existingTheme == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Validate vendor ownership if vendor phone is provided
        if (vendorPhone != null && !vendorPhone.trim().isEmpty()) {
            // Super admin can delete any theme
            if (!authorizationService.isSuperAdmin(vendorPhone)) {
                if (!authorizationService.canPerformVendorOperations(vendorPhone)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                
                // Then verify ownership
                com.example.RecordService.model.Business business = businessService.getBusinessById(existingTheme.getBusinessId());
                if (business == null || !business.getPhoneNumber().equals(vendorPhone)) {
                    // Check if vendor owns the business through any of their businesses
                    List<com.example.RecordService.model.Business> vendorBusinesses = businessService.getBusinessesByVendorPhoneNumber(vendorPhone);
                    boolean ownsBusiness = vendorBusinesses.stream()
                            .anyMatch(b -> b.getBusinessId().equals(existingTheme.getBusinessId()));
                    if (!ownsBusiness) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                }
            }
        }
        
        boolean deleted = themeService.deleteTheme(themeId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET endpoint to get the total count of themes
     * @return ResponseEntity with theme count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getThemeCount() {
        long count = themeService.getThemeCount();
        return ResponseEntity.ok(count);
    }
}
