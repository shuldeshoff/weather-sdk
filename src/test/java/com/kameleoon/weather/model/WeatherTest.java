package com.kameleoon.weather.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Weather record.
 *
 * @author Yury Shuldeshov
 */
class WeatherTest {
    
    @Test
    void shouldCreateWeatherWithValidData() {
        Weather weather = new Weather("Clouds", "scattered clouds");
        
        assertEquals("Clouds", weather.main());
        assertEquals("scattered clouds", weather.description());
    }
    
    @Test
    void shouldThrowExceptionWhenMainIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Weather(null, "description"));
    }
    
    @Test
    void shouldThrowExceptionWhenMainIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Weather("  ", "description"));
    }
    
    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Weather("Clouds", null));
    }
    
    @Test
    void shouldThrowExceptionWhenDescriptionIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Weather("Clouds", "  "));
    }
    
    @Test
    void shouldImplementEqualsCorrectly() {
        Weather weather1 = new Weather("Clouds", "scattered clouds");
        Weather weather2 = new Weather("Clouds", "scattered clouds");
        Weather weather3 = new Weather("Rain", "light rain");
        
        assertEquals(weather1, weather2);
        assertNotEquals(weather1, weather3);
    }
    
    @Test
    void shouldImplementHashCodeCorrectly() {
        Weather weather1 = new Weather("Clouds", "scattered clouds");
        Weather weather2 = new Weather("Clouds", "scattered clouds");
        
        assertEquals(weather1.hashCode(), weather2.hashCode());
    }
    
    @Test
    void shouldImplementToStringCorrectly() {
        Weather weather = new Weather("Clouds", "scattered clouds");
        String str = weather.toString();
        
        assertTrue(str.contains("Clouds"));
        assertTrue(str.contains("scattered clouds"));
    }
}

