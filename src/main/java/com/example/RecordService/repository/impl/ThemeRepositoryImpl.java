package com.example.RecordService.repository.impl;

import com.example.RecordService.model.Theme;
import com.example.RecordService.repository.ThemeRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ThemeRepositoryImpl implements ThemeRepository {
    
    private static final Map<String, Theme> themes = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);
    
    @Override
    public Theme save(Theme theme) {
        if (theme.getThemeId() == null) {
            theme.setThemeId("THEME_" + idCounter.getAndIncrement());
        }
        themes.put(theme.getThemeId(), theme);
        return theme;
    }
    
    @Override
    public Theme findByThemeId(String themeId) {
        return themes.get(themeId);
    }
    
    @Override
    public List<Theme> findAll() {
        return new ArrayList<>(themes.values());
    }
    
    @Override
    public List<Theme> findByBusinessId(String businessId) {
        return themes.values().stream()
                .filter(theme -> theme.getBusinessId().equals(businessId))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<Theme> findByCategory(String category) {
        return themes.values().stream()
                .filter(theme -> theme.getThemeCategory().equals(category))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<Theme> findByActive(boolean active) {
        return themes.values().stream()
                .filter(theme -> theme.isActive() == active)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public boolean existsByThemeId(String themeId) {
        return themes.containsKey(themeId);
    }
    
    @Override
    public Theme update(Theme theme) {
        themes.put(theme.getThemeId(), theme);
        return theme;
    }
    
    @Override
    public boolean delete(String themeId) {
        Theme removed = themes.remove(themeId);
        return removed != null;
    }
    
    @Override
    public long count() {
        return themes.size();
    }
}