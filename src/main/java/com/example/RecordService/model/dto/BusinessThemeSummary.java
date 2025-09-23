package com.example.RecordService.model.dto;

import java.util.List;

public class BusinessThemeSummary {
    private String businessId;
    private String businessName;
    private List<ThemeSummary> themes;

    public BusinessThemeSummary() {
    }

    public BusinessThemeSummary(String businessId, String businessName, List<ThemeSummary> themes) {
        this.businessId = businessId;
        this.businessName = businessName;
        this.themes = themes;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public List<ThemeSummary> getThemes() {
        return themes;
    }

    public void setThemes(List<ThemeSummary> themes) {
        this.themes = themes;
    }
}


