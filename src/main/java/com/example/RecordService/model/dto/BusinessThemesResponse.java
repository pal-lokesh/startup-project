package com.example.RecordService.model.dto;

import com.example.RecordService.model.Business;
import com.example.RecordService.model.Theme;

import java.util.List;

public class BusinessThemesResponse {
    private Business business;
    private List<Theme> themes;

    public BusinessThemesResponse() {
    }

    public BusinessThemesResponse(Business business, List<Theme> themes) {
        this.business = business;
        this.themes = themes;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public void setThemes(List<Theme> themes) {
        this.themes = themes;
    }
}


