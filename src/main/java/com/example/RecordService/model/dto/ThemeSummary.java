package com.example.RecordService.model.dto;

public class ThemeSummary {
    private String themeId;
    private String themeName;

    public ThemeSummary() {
    }

    public ThemeSummary(String themeId, String themeName) {
        this.themeId = themeId;
        this.themeName = themeName;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }
}


