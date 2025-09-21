package com.example.RecordService.service;

import com.example.RecordService.model.Theme;
import com.example.RecordService.repository.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThemeService {
    
    @Autowired
    private ThemeRepository themeRepository;
    
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
        return themeRepository.update(theme);
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
}
