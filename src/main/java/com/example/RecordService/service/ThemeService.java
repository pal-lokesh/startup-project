package com.example.RecordService.service;

import com.example.RecordService.model.Business;
import com.example.RecordService.model.Theme;
import com.example.RecordService.model.dto.BusinessThemesResponse;
import com.example.RecordService.model.dto.BusinessThemeSummary;
import com.example.RecordService.model.dto.ThemeSummary;
import com.example.RecordService.repository.BusinessRepository;
import com.example.RecordService.repository.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThemeService {
    
    @Autowired
    private ThemeRepository themeRepository;
    
    @Autowired
    private BusinessRepository businessRepository;
    
    @Autowired
    private com.example.RecordService.service.StockNotificationService stockNotificationService;
    
    /**
     * Save a new theme
     * @param theme the theme to be saved
     * @return the saved theme
     */
    public Theme saveTheme(Theme theme) {
        return themeRepository.save(theme);
    }
    
    /**
     * Get all themes
     * @return list of all themes
     */
    public List<Theme> getAllThemes() {
        return themeRepository.findAll();
    }
    
    /**
     * Get theme by theme ID
     * @param themeId the theme ID
     * @return the theme if found, null otherwise
     */
    public Theme getThemeById(String themeId) {
        return themeRepository.findByThemeId(themeId);
    }
    
    /**
     * Get themes by business ID
     * @param businessId the business ID
     * @return list of themes for the specified business
     */
    public List<Theme> getThemesByBusinessId(String businessId) {
        return themeRepository.findByBusinessId(businessId);
    }
    
    /**
     * Get themes by category
     * @param category the theme category
     * @return list of themes with specified category
     */
    public List<Theme> getThemesByCategory(String category) {
        return themeRepository.findByCategory(category);
    }
    
    /**
     * Get active themes
     * @param active active status
     * @return list of themes with specified active status
     */
    public List<Theme> getThemesByActive(boolean active) {
        return themeRepository.findByActive(active);
    }
    
    /**
     * Check if theme exists by theme ID
     * @param themeId the theme ID
     * @return true if theme exists, false otherwise
     */
    public boolean themeExistsById(String themeId) {
        return themeRepository.existsByThemeId(themeId);
    }
    
    /**
     * Update an existing theme
     * @param theme the updated theme data
     * @return the updated theme
     */
    public Theme updateTheme(Theme theme) {
        // Get existing theme to check if stock changed from 0 to >0
        Theme existingTheme = themeRepository.findByThemeId(theme.getThemeId());
        int previousQuantity = existingTheme != null ? existingTheme.getQuantity() : 0;
        
        Theme updatedTheme = themeRepository.update(theme);
        
        // If item was out of stock and now has stock, notify subscribers
        if (previousQuantity == 0 && updatedTheme.getQuantity() > 0) {
            try {
                stockNotificationService.notifySubscribers(
                    updatedTheme.getThemeId(), 
                    "THEME", 
                    updatedTheme.getThemeName()
                );
            } catch (Exception e) {
                System.err.println("Failed to notify subscribers for theme " + updatedTheme.getThemeId() + ": " + e.getMessage());
            }
        }
        
        return updatedTheme;
    }
    
    /**
     * Delete a theme by theme ID
     * @param themeId the theme ID
     * @return true if theme was deleted, false if not found
     */
    public boolean deleteTheme(String themeId) {
        return themeRepository.delete(themeId);
    }
    
    /**
     * Get total number of themes
     * @return count of themes
     */
    public long getThemeCount() {
        return themeRepository.count();
    }

    /**
     * Return all businesses with their associated themes, grouped and sorted by business name
     */
    public List<BusinessThemesResponse> getThemesGroupedByBusiness() {
        List<Business> businesses = businessRepository.findAll();
        // ensure themes list is not null on Business model
        List<BusinessThemesResponse> result = new ArrayList<>();
        for (Business business : businesses) {
            List<Theme> themes = themeRepository.findByBusinessId(business.getBusinessId());
            // sort themes by name for stable output
            List<Theme> sortedThemes = themes.stream()
                    .sorted(Comparator.comparing(Theme::getThemeName, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .collect(Collectors.toList());
            BusinessThemesResponse dto = new BusinessThemesResponse(business, sortedThemes);
            result.add(dto);
        }
        // sort businesses by name
        result.sort(Comparator.comparing(
                b -> b.getBusiness().getBusinessName(),
                Comparator.nullsLast(String::compareToIgnoreCase)
        ));
        return result;
    }

    /**
     * Return minimal view: business name and themes (id + name) only
     */
    public List<BusinessThemeSummary> getThemesByBusinessSummary() {
        List<Business> businesses = businessRepository.findAll();
        List<BusinessThemeSummary> result = new ArrayList<>();
        for (Business business : businesses) {
            List<Theme> themes = themeRepository.findByBusinessId(business.getBusinessId());
            List<ThemeSummary> themeSummaries = themes.stream()
                    .sorted(Comparator.comparing(Theme::getThemeName, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .map(t -> new ThemeSummary(t.getThemeId(), t.getThemeName()))
                    .collect(Collectors.toList());
            result.add(new BusinessThemeSummary(business.getBusinessId(), business.getBusinessName(), themeSummaries));
        }
        result.sort(Comparator.comparing(BusinessThemeSummary::getBusinessName, Comparator.nullsLast(String::compareToIgnoreCase)));
        return result;
    }
}
