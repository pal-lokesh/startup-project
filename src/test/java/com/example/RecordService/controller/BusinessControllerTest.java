package com.example.RecordService.controller;

import com.example.RecordService.model.Business;
import com.example.RecordService.model.dto.GeocodeRequest;
import com.example.RecordService.model.dto.ReverseGeocodeRequest;
import com.example.RecordService.service.BusinessService;
import com.example.RecordService.service.GeolocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BusinessController.class)
class BusinessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private GeolocationService geolocationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Business testBusiness;

    @BeforeEach
    void setUp() {
        testBusiness = new Business();
        testBusiness.setBusinessId("BUSINESS_1");
        testBusiness.setPhoneNumber("1234567890");
        testBusiness.setBusinessName("Test Business");
        testBusiness.setBusinessAddress("123 Main St");
        testBusiness.setLatitude(40.7128);
        testBusiness.setLongitude(-74.0060);
    }

    // Test Geocode Endpoint
    @Test
    void testGeocodeAddress_ShouldReturnSuccess() throws Exception {
        Map<String, Double> coordinates = new HashMap<>();
        coordinates.put("latitude", 40.7128);
        coordinates.put("longitude", -74.0060);

        when(geolocationService.geocodeAddress("123 Main St")).thenReturn(coordinates);

        GeocodeRequest request = new GeocodeRequest("123 Main St");

        mockMvc.perform(post("/api/businesses/geocode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.latitude").value(40.7128))
                .andExpect(jsonPath("$.longitude").value(-74.0060));

        verify(geolocationService, times(1)).geocodeAddress("123 Main St");
    }

    @Test
    void testGeocodeAddress_ShouldReturnServiceUnavailableWhenNotImplemented() throws Exception {
        when(geolocationService.geocodeAddress("123 Main St")).thenReturn(null);

        GeocodeRequest request = new GeocodeRequest("123 Main St");

        mockMvc.perform(post("/api/businesses/geocode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").exists());

        verify(geolocationService, times(1)).geocodeAddress("123 Main St");
    }

    @Test
    void testGeocodeAddress_ShouldReturnBadRequestForInvalidRequest() throws Exception {
        GeocodeRequest request = new GeocodeRequest(""); // Empty address

        mockMvc.perform(post("/api/businesses/geocode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(geolocationService, never()).geocodeAddress(anyString());
    }

    // Test Reverse Geocode Endpoint
    @Test
    void testReverseGeocode_ShouldReturnSuccess() throws Exception {
        when(geolocationService.reverseGeocode(40.7128, -74.0060)).thenReturn("123 Main St, New York");

        ReverseGeocodeRequest request = new ReverseGeocodeRequest(40.7128, -74.0060);

        mockMvc.perform(post("/api/businesses/reverse-geocode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(40.7128))
                .andExpect(jsonPath("$.longitude").value(-74.0060))
                .andExpect(jsonPath("$.address").value("123 Main St, New York"));

        verify(geolocationService, times(1)).reverseGeocode(40.7128, -74.0060);
    }

    @Test
    void testReverseGeocode_ShouldReturnServiceUnavailableWhenNotImplemented() throws Exception {
        when(geolocationService.reverseGeocode(40.7128, -74.0060)).thenReturn(null);

        ReverseGeocodeRequest request = new ReverseGeocodeRequest(40.7128, -74.0060);

        mockMvc.perform(post("/api/businesses/reverse-geocode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").exists());

        verify(geolocationService, times(1)).reverseGeocode(40.7128, -74.0060);
    }

    @Test
    void testReverseGeocode_ShouldReturnBadRequestForInvalidCoordinates() throws Exception {
        ReverseGeocodeRequest request = new ReverseGeocodeRequest(91.0, -74.0060); // Invalid latitude

        mockMvc.perform(post("/api/businesses/reverse-geocode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(geolocationService, never()).reverseGeocode(anyDouble(), anyDouble());
    }

    // Test Find Nearby Businesses Endpoint
    @Test
    void testFindNearbyBusinesses_ShouldReturnSuccess() throws Exception {
        List<Business> businesses = new ArrayList<>();
        businesses.add(testBusiness);

        when(geolocationService.isValidCoordinates(40.7128, -74.0060)).thenReturn(true);
        when(businessService.findNearbyBusinesses(40.7128, -74.0060, 10.0)).thenReturn(businesses);

        mockMvc.perform(get("/api/businesses/nearby")
                .param("latitude", "40.7128")
                .param("longitude", "-74.0060")
                .param("radiusKm", "10.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(40.7128))
                .andExpect(jsonPath("$.longitude").value(-74.0060))
                .andExpect(jsonPath("$.radiusKm").value(10.0))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.businesses").isArray());

        verify(businessService, times(1)).findNearbyBusinesses(40.7128, -74.0060, 10.0);
    }

    @Test
    void testFindNearbyBusinesses_ShouldUseDefaultRadius() throws Exception {
        when(geolocationService.isValidCoordinates(40.7128, -74.0060)).thenReturn(true);
        when(businessService.findNearbyBusinesses(40.7128, -74.0060, 10.0)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/businesses/nearby")
                .param("latitude", "40.7128")
                .param("longitude", "-74.0060"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.radiusKm").value(10.0));

        verify(businessService, times(1)).findNearbyBusinesses(40.7128, -74.0060, 10.0);
    }

    @Test
    void testFindNearbyBusinesses_ShouldReturnBadRequestForInvalidCoordinates() throws Exception {
        when(geolocationService.isValidCoordinates(91.0, -74.0060)).thenReturn(false);

        mockMvc.perform(get("/api/businesses/nearby")
                .param("latitude", "91.0")
                .param("longitude", "-74.0060"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(businessService, never()).findNearbyBusinesses(anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void testFindNearbyBusinesses_ShouldReturnBadRequestForInvalidRadius() throws Exception {
        when(geolocationService.isValidCoordinates(40.7128, -74.0060)).thenReturn(true);

        mockMvc.perform(get("/api/businesses/nearby")
                .param("latitude", "40.7128")
                .param("longitude", "-74.0060")
                .param("radiusKm", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Radius must be between 0 and 1000 kilometers"));

        verify(businessService, never()).findNearbyBusinesses(anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void testFindNearbyBusinesses_ShouldReturnBadRequestForRadiusTooLarge() throws Exception {
        when(geolocationService.isValidCoordinates(40.7128, -74.0060)).thenReturn(true);

        mockMvc.perform(get("/api/businesses/nearby")
                .param("latitude", "40.7128")
                .param("longitude", "-74.0060")
                .param("radiusKm", "1001"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Radius must be between 0 and 1000 kilometers"));

        verify(businessService, never()).findNearbyBusinesses(anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void testFindNearbyBusinesses_ShouldReturnEmptyListWhenNoBusinessesNearby() throws Exception {
        when(geolocationService.isValidCoordinates(40.7128, -74.0060)).thenReturn(true);
        when(businessService.findNearbyBusinesses(40.7128, -74.0060, 5.0)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/businesses/nearby")
                .param("latitude", "40.7128")
                .param("longitude", "-74.0060")
                .param("radiusKm", "5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.businesses").isArray());

        verify(businessService, times(1)).findNearbyBusinesses(40.7128, -74.0060, 5.0);
    }
}

