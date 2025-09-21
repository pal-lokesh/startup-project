package com.example.RecordService.repository;

import com.example.RecordService.model.Theme;
import java.util.List;

public interface ThemeRepository {
    Theme save(Theme theme);
    Theme findByThemeId(String themeId);
    List<Theme> findByBusinessId(String businessId);
    List<Theme> findAll();
    List<Theme> findByCategory(String category);
    List<Theme> findByActive(boolean active);
    boolean existsByThemeId(String themeId);
    Theme update(Theme theme);
    boolean delete(String themeId);
    long count();
}
