package com.example.RecordService.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GeolocationServiceTest {

    @InjectMocks
    private GeolocationService geolocationService;

    @Test
    void testGeocodeAddress_ShouldReturnNullForNullAddress() {
        Map<String, Double> result = geolocationService.geocodeAddress(null);
        assertNull(result);
    }

    @Test
    void testGeocodeAddress_ShouldReturnNullForEmptyAddress() {
        Map<String, Double> result = geolocationService.geocodeAddress("");
        assertNull(result);
    }

    @Test
    void testGeocodeAddress_ShouldReturnNullForBlankAddress() {
        Map<String, Double> result = geolocationService.geocodeAddress("   ");
        assertNull(result);
    }

    @Test
    void testGeocodeAddress_ShouldReturnNullWhenNotImplemented() {
        // Currently returns null as geocoding service is not integrated
        Map<String, Double> result = geolocationService.geocodeAddress("123 Main St, City");
        assertNull(result);
    }

    @Test
    void testReverseGeocode_ShouldReturnNullForNullLatitude() {
        String result = geolocationService.reverseGeocode(null, 74.0060);
        assertNull(result);
    }

    @Test
    void testReverseGeocode_ShouldReturnNullForNullLongitude() {
        String result = geolocationService.reverseGeocode(40.7128, null);
        assertNull(result);
    }

    @Test
    void testReverseGeocode_ShouldReturnNullForInvalidLatitude() {
        String result = geolocationService.reverseGeocode(91.0, 74.0060); // Invalid latitude
        assertNull(result);
    }

    @Test
    void testReverseGeocode_ShouldReturnNullForInvalidLongitude() {
        String result = geolocationService.reverseGeocode(40.7128, 181.0); // Invalid longitude
        assertNull(result);
    }

    @Test
    void testReverseGeocode_ShouldReturnNullForNegativeLatitudeOutOfRange() {
        String result = geolocationService.reverseGeocode(-91.0, 74.0060);
        assertNull(result);
    }

    @Test
    void testReverseGeocode_ShouldReturnNullForNegativeLongitudeOutOfRange() {
        String result = geolocationService.reverseGeocode(40.7128, -181.0);
        assertNull(result);
    }

    @Test
    void testReverseGeocode_ShouldReturnNullWhenNotImplemented() {
        // Currently returns null as reverse geocoding service is not integrated
        String result = geolocationService.reverseGeocode(40.7128, -74.0060);
        assertNull(result);
    }

    @Test
    void testCalculateDistance_ShouldCalculateCorrectDistance() {
        // Distance between New York (40.7128, -74.0060) and Los Angeles (34.0522, -118.2437)
        // Expected distance: approximately 3944 km
        double distance = geolocationService.calculateDistance(40.7128, -74.0060, 34.0522, -118.2437);
        
        assertTrue(distance > 3900 && distance < 4000, "Distance should be approximately 3944 km");
    }

    @Test
    void testCalculateDistance_ShouldReturnZeroForSameCoordinates() {
        double distance = geolocationService.calculateDistance(40.7128, -74.0060, 40.7128, -74.0060);
        assertEquals(0.0, distance, 0.1, "Distance should be 0 for same coordinates");
    }

    @Test
    void testCalculateDistance_ShouldHandleNegativeCoordinates() {
        // Test with coordinates in southern hemisphere
        double distance = geolocationService.calculateDistance(-34.6037, -58.3816, -33.4489, -70.6693);
        assertTrue(distance > 0, "Distance should be positive");
        assertTrue(distance < 1500, "Distance between Buenos Aires and Santiago should be less than 1500 km");
    }

    @Test
    void testCalculateDistance_ShouldHandleCrossingEquator() {
        // Test coordinates crossing equator
        double distance = geolocationService.calculateDistance(1.0, 0.0, -1.0, 0.0);
        assertTrue(distance > 200 && distance < 250, "Distance across equator should be approximately 222 km");
    }

    @Test
    void testCalculateDistance_ShouldHandleCrossingPrimeMeridian() {
        // Test coordinates crossing prime meridian
        double distance = geolocationService.calculateDistance(0.0, 1.0, 0.0, -1.0);
        assertTrue(distance > 200 && distance < 250, "Distance across prime meridian should be approximately 222 km");
    }

    @Test
    void testIsValidCoordinates_ShouldReturnTrueForValidCoordinates() {
        assertTrue(geolocationService.isValidCoordinates(40.7128, -74.0060));
        assertTrue(geolocationService.isValidCoordinates(0.0, 0.0));
        assertTrue(geolocationService.isValidCoordinates(-90.0, -180.0));
        assertTrue(geolocationService.isValidCoordinates(90.0, 180.0));
    }

    @Test
    void testIsValidCoordinates_ShouldReturnFalseForNullLatitude() {
        assertFalse(geolocationService.isValidCoordinates(null, -74.0060));
    }

    @Test
    void testIsValidCoordinates_ShouldReturnFalseForNullLongitude() {
        assertFalse(geolocationService.isValidCoordinates(40.7128, null));
    }

    @Test
    void testIsValidCoordinates_ShouldReturnFalseForInvalidLatitude() {
        assertFalse(geolocationService.isValidCoordinates(91.0, -74.0060));
        assertFalse(geolocationService.isValidCoordinates(-91.0, -74.0060));
    }

    @Test
    void testIsValidCoordinates_ShouldReturnFalseForInvalidLongitude() {
        assertFalse(geolocationService.isValidCoordinates(40.7128, 181.0));
        assertFalse(geolocationService.isValidCoordinates(40.7128, -181.0));
    }

    @Test
    void testIsValidCoordinates_ShouldReturnTrueForBoundaryValues() {
        assertTrue(geolocationService.isValidCoordinates(90.0, 180.0));
        assertTrue(geolocationService.isValidCoordinates(-90.0, -180.0));
        assertTrue(geolocationService.isValidCoordinates(0.0, 0.0));
    }

    @Test
    void testIsValidCoordinates_ShouldReturnFalseForJustOutsideBoundary() {
        assertFalse(geolocationService.isValidCoordinates(90.0001, 180.0));
        assertFalse(geolocationService.isValidCoordinates(-90.0001, -180.0));
        assertFalse(geolocationService.isValidCoordinates(40.7128, 180.0001));
        assertFalse(geolocationService.isValidCoordinates(40.7128, -180.0001));
    }
}

