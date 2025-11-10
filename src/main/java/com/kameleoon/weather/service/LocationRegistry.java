package com.kameleoon.weather.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for managing locations in POLLING mode.
 * Thread-safe registry using ConcurrentHashMap.
 *
 * @author Yury Shuldeshov
 */
public class LocationRegistry {
    
    private final Set<String> locations;
    
    /**
     * Creates a new LocationRegistry.
     */
    public LocationRegistry() {
        this.locations = ConcurrentHashMap.newKeySet();
    }
    
    /**
     * Registers a location for polling.
     * City names are stored in lowercase for consistency.
     *
     * @param cityName Name of the city to register
     * @return true if the city was added, false if it was already registered
     */
    public boolean register(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            throw new IllegalArgumentException("City name cannot be null or blank");
        }
        return locations.add(normalizeCityName(cityName));
    }
    
    /**
     * Unregisters a location from polling.
     *
     * @param cityName Name of the city to unregister
     * @return true if the city was removed, false if it was not registered
     */
    public boolean unregister(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            return false;
        }
        return locations.remove(normalizeCityName(cityName));
    }
    
    /**
     * Checks if a location is registered.
     *
     * @param cityName Name of the city
     * @return true if registered, false otherwise
     */
    public boolean isRegistered(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            return false;
        }
        return locations.contains(normalizeCityName(cityName));
    }
    
    /**
     * Gets all registered locations.
     *
     * @return Set of registered city names (normalized to lowercase)
     */
    public Set<String> getAllLocations() {
        return Set.copyOf(locations);
    }
    
    /**
     * Gets the number of registered locations.
     *
     * @return Number of locations
     */
    public int size() {
        return locations.size();
    }
    
    /**
     * Clears all registered locations.
     */
    public void clear() {
        locations.clear();
    }
    
    /**
     * Normalizes city name to lowercase.
     */
    private String normalizeCityName(String cityName) {
        return cityName.toLowerCase().trim();
    }
}

