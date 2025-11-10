package com.kameleoon.weather.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LocationRegistry.
 *
 * @author Yury Shuldeshov
 */
class LocationRegistryTest {
    
    private LocationRegistry registry;
    
    @BeforeEach
    void setUp() {
        registry = new LocationRegistry();
    }
    
    @Test
    @DisplayName("Should register a location")
    void shouldRegisterLocation() {
        boolean result = registry.register("London");
        
        assertTrue(result);
        assertTrue(registry.isRegistered("London"));
        assertEquals(1, registry.size());
    }
    
    @Test
    @DisplayName("Should not register duplicate location")
    void shouldNotRegisterDuplicateLocation() {
        registry.register("London");
        boolean result = registry.register("London");
        
        assertFalse(result);
        assertEquals(1, registry.size());
    }
    
    @Test
    @DisplayName("Should handle case-insensitive city names")
    void shouldHandleCaseInsensitiveCityNames() {
        registry.register("London");
        
        assertTrue(registry.isRegistered("london"));
        assertTrue(registry.isRegistered("LONDON"));
        assertTrue(registry.isRegistered("LoNdOn"));
        
        // Should not add duplicates
        assertFalse(registry.register("london"));
        assertEquals(1, registry.size());
    }
    
    @Test
    @DisplayName("Should unregister a location")
    void shouldUnregisterLocation() {
        registry.register("London");
        boolean result = registry.unregister("London");
        
        assertTrue(result);
        assertFalse(registry.isRegistered("London"));
        assertEquals(0, registry.size());
    }
    
    @Test
    @DisplayName("Should return false when unregistering non-existent location")
    void shouldReturnFalseWhenUnregisteringNonExistentLocation() {
        boolean result = registry.unregister("NonExistent");
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should register multiple locations")
    void shouldRegisterMultipleLocations() {
        registry.register("London");
        registry.register("Paris");
        registry.register("Berlin");
        
        assertEquals(3, registry.size());
        assertTrue(registry.isRegistered("London"));
        assertTrue(registry.isRegistered("Paris"));
        assertTrue(registry.isRegistered("Berlin"));
    }
    
    @Test
    @DisplayName("Should get all registered locations")
    void shouldGetAllRegisteredLocations() {
        registry.register("London");
        registry.register("Paris");
        registry.register("Berlin");
        
        Set<String> locations = registry.getAllLocations();
        
        assertEquals(3, locations.size());
        assertTrue(locations.contains("london"));
        assertTrue(locations.contains("paris"));
        assertTrue(locations.contains("berlin"));
    }
    
    @Test
    @DisplayName("Should return immutable set of locations")
    void shouldReturnImmutableSetOfLocations() {
        registry.register("London");
        Set<String> locations = registry.getAllLocations();
        
        assertThrows(UnsupportedOperationException.class, () ->
            locations.add("Paris"));
    }
    
    @Test
    @DisplayName("Should clear all locations")
    void shouldClearAllLocations() {
        registry.register("London");
        registry.register("Paris");
        registry.register("Berlin");
        
        registry.clear();
        
        assertEquals(0, registry.size());
        assertFalse(registry.isRegistered("London"));
        assertFalse(registry.isRegistered("Paris"));
        assertFalse(registry.isRegistered("Berlin"));
    }
    
    @Test
    @DisplayName("Should throw exception for null city name on register")
    void shouldThrowExceptionForNullCityNameOnRegister() {
        assertThrows(IllegalArgumentException.class, () ->
            registry.register(null));
    }
    
    @Test
    @DisplayName("Should throw exception for blank city name on register")
    void shouldThrowExceptionForBlankCityNameOnRegister() {
        assertThrows(IllegalArgumentException.class, () ->
            registry.register(""));
        
        assertThrows(IllegalArgumentException.class, () ->
            registry.register("   "));
    }
    
    @Test
    @DisplayName("Should return false for null city name on isRegistered")
    void shouldReturnFalseForNullCityNameOnIsRegistered() {
        assertFalse(registry.isRegistered(null));
        assertFalse(registry.isRegistered(""));
        assertFalse(registry.isRegistered("   "));
    }
    
    @Test
    @DisplayName("Should handle concurrent registration")
    void shouldHandleConcurrentRegistration() throws InterruptedException {
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    registry.register("City-" + (threadId * 100 + j));
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Should have 1000 unique cities
        assertEquals(1000, registry.size());
    }
}

