package com.example.RecordService.model.dto;

import jakarta.validation.constraints.NotBlank;

public class GeocodeRequest {
    
    @NotBlank(message = "Address is required")
    private String address;

    public GeocodeRequest() {
    }

    public GeocodeRequest(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

