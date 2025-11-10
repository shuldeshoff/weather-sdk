package com.kameleoon.weather.model;

/**
 * Internal model for cache storage.
 * Contains weather data along with cache metadata.
 *
 * @param data The weather data
 * @param timestamp When the data was cached (System.currentTimeMillis())
 * @author Yury Shuldeshov
 */
public record CacheEntry(WeatherData data, long timestamp) {
    
    /**
     * Compact constructor with validation.
     */
    public CacheEntry {
        if (data == null) {
            throw new IllegalArgumentException("Weather data cannot be null");
        }
        if (timestamp < 0) {
            throw new IllegalArgumentException("Timestamp cannot be negative");
        }
    }
    
    /**
     * Check if the cache entry is expired based on TTL.
     *
     * @param ttlMillis Time-to-live in milliseconds
     * @return true if expired, false otherwise
     */
    public boolean isExpired(long ttlMillis) {
        return System.currentTimeMillis() - timestamp > ttlMillis;
    }
    
    /**
     * Get the age of this cache entry in milliseconds.
     *
     * @return Age in milliseconds
     */
    public long getAge() {
        return System.currentTimeMillis() - timestamp;
    }
}

