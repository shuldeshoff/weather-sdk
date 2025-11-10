package com.kameleoon.weather.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Temperature record.
 *
 * @author Yury Shuldeshov
 */
class TemperatureTest {
    
    @Test
    void shouldCreateTemperatureWithValidData() {
        Temperature temp = new Temperature(20.5, 18.3);
        
        assertEquals(20.5, temp.temp());
        assertEquals(18.3, temp.feelsLike());
    }
    
    @Test
    void shouldThrowExceptionWhenTempIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Temperature(null, 18.3));
    }
    
    @Test
    void shouldThrowExceptionWhenFeelsLikeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Temperature(20.5, null));
    }
    
    @Test
    void shouldThrowExceptionWhenTempIsBelowAbsoluteZero() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Temperature(-300.0, 0.0));
    }
    
    @Test
    void shouldAcceptTemperatureAtAbsoluteZero() {
        assertDoesNotThrow(() -> new Temperature(-273.15, -273.15));
    }
    
    @Test
    void shouldAcceptNegativeTemperatures() {
        Temperature temp = new Temperature(-10.0, -15.0);
        
        assertEquals(-10.0, temp.temp());
        assertEquals(-15.0, temp.feelsLike());
    }
    
    @Test
    void shouldImplementEqualsCorrectly() {
        Temperature temp1 = new Temperature(20.5, 18.3);
        Temperature temp2 = new Temperature(20.5, 18.3);
        Temperature temp3 = new Temperature(15.0, 13.0);
        
        assertEquals(temp1, temp2);
        assertNotEquals(temp1, temp3);
    }
}

