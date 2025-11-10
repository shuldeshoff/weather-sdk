package com.kameleoon.weather.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CacheEntry record.
 *
 * @author Yury Shuldeshov
 */
class CacheEntryTest {
    
    private WeatherData createSampleWeatherData() {
        return new WeatherData(
            new Weather("Clouds", "scattered clouds"),
            new Temperature(20.0, 18.0),
            10000,
            new Wind(5.0),
            1699632000L,
            new Sys(1699594800L, 1699632000L),
            3600,
            "London"
        );
    }
    
    @Test
    void shouldCreateCacheEntryWithValidData() {
        WeatherData data = createSampleWeatherData();
        long timestamp = System.currentTimeMillis();
        
        CacheEntry entry = new CacheEntry(data, timestamp);
        
        assertEquals(data, entry.data());
        assertEquals(timestamp, entry.timestamp());
    }
    
    @Test
    void shouldThrowExceptionWhenDataIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CacheEntry(null, System.currentTimeMillis()));
    }
    
    @Test
    void shouldThrowExceptionWhenTimestampIsNegative() {
        WeatherData data = createSampleWeatherData();
        
        assertThrows(IllegalArgumentException.class, () -> 
            new CacheEntry(data, -1L));
    }
    
    @Test
    void shouldReturnTrueWhenExpired() throws InterruptedException {
        WeatherData data = createSampleWeatherData();
        long timestamp = System.currentTimeMillis() - 100; // 100ms ago
        CacheEntry entry = new CacheEntry(data, timestamp);
        
        assertTrue(entry.isExpired(50)); // TTL of 50ms
    }
    
    @Test
    void shouldReturnFalseWhenNotExpired() {
        WeatherData data = createSampleWeatherData();
        long timestamp = System.currentTimeMillis();
        CacheEntry entry = new CacheEntry(data, timestamp);
        
        assertFalse(entry.isExpired(10000)); // TTL of 10 seconds
    }
    
    @Test
    void shouldCalculateAgeCorrectly() throws InterruptedException {
        WeatherData data = createSampleWeatherData();
        long timestamp = System.currentTimeMillis();
        CacheEntry entry = new CacheEntry(data, timestamp);
        
        Thread.sleep(50); // Wait 50ms
        
        assertTrue(entry.getAge() >= 50);
        assertTrue(entry.getAge() < 200); // Should be reasonable
    }
}

