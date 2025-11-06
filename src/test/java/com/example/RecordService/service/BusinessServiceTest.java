package com.example.RecordService.service;

import com.example.RecordService.model.Business;
import com.example.RecordService.repository.BusinessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessServiceTest {

    @Mock
    private BusinessRepository businessRepository;

    @InjectMocks
    private BusinessService businessService;

    private Business testBusiness;

    @BeforeEach
    void setUp() {
        testBusiness = new Business();
        testBusiness.setBusinessId("BUSINESS_1");
        testBusiness.setPhoneNumber("1234567890");
        testBusiness.setBusinessName("Test Business");
        testBusiness.setBusinessDescription("Test Description");
        testBusiness.setBusinessCategory("catering");
        testBusiness.setBusinessAddress("123 Main St");
        testBusiness.setLatitude(40.7128);
        testBusiness.setLongitude(-74.0060);
        testBusiness.setBusinessPhone("1234567890");
        testBusiness.setBusinessEmail("test@example.com");
        testBusiness.setActive(true);
    }

    @Test
    void testFindNearbyBusinesses_ShouldReturnBusinessesWithinRadius() {
        List<Business> nearbyBusinesses = new ArrayList<>();
        nearbyBusinesses.add(testBusiness);

        when(businessRepository.findNearby(40.7128, -74.0060, 10.0)).thenReturn(nearbyBusinesses);

        List<Business> result = businessService.findNearbyBusinesses(40.7128, -74.0060, 10.0);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBusiness.getBusinessId(), result.get(0).getBusinessId());
        verify(businessRepository, times(1)).findNearby(40.7128, -74.0060, 10.0);
    }

    @Test
    void testFindNearbyBusinesses_ShouldReturnEmptyListWhenNoBusinessesNearby() {
        when(businessRepository.findNearby(40.7128, -74.0060, 5.0)).thenReturn(new ArrayList<>());

        List<Business> result = businessService.findNearbyBusinesses(40.7128, -74.0060, 5.0);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(businessRepository, times(1)).findNearby(40.7128, -74.0060, 5.0);
    }

    @Test
    void testFindNearbyBusinesses_ShouldHandleLargeRadius() {
        List<Business> businesses = new ArrayList<>();
        businesses.add(testBusiness);

        when(businessRepository.findNearby(40.7128, -74.0060, 100.0)).thenReturn(businesses);

        List<Business> result = businessService.findNearbyBusinesses(40.7128, -74.0060, 100.0);

        assertNotNull(result);
        verify(businessRepository, times(1)).findNearby(40.7128, -74.0060, 100.0);
    }

    @Test
    void testFindNearbyBusinesses_ShouldHandleSmallRadius() {
        when(businessRepository.findNearby(40.7128, -74.0060, 0.5)).thenReturn(new ArrayList<>());

        List<Business> result = businessService.findNearbyBusinesses(40.7128, -74.0060, 0.5);

        assertNotNull(result);
        verify(businessRepository, times(1)).findNearby(40.7128, -74.0060, 0.5);
    }
}

