package com.kameleoon.weather.model;

import java.util.Set;

/**
 * Information about cache state.
 * Returned by SDK to provide insight into cached data.
 *
 * @param cachedCities Set of city names currently in cache
 * @param currentSize Current number of entries in cache
 * @param maxSize Maximum cache capacity
 * @author Yury Shuldeshov
 */
public record CacheInfo(Set<String> cachedCities, int currentSize, int maxSize) {
    
    /**
     * Compact constructor with validation.
     */
    public CacheInfo {
        if (cachedCities == null) {
            throw new IllegalArgumentException("Cached cities set cannot be null");
        }
        if (currentSize < 0) {
            throw new IllegalArgumentException("Current size cannot be negative");
        }
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be positive");
        }
        if (currentSize > maxSize) {
            throw new IllegalArgumentException("Current size cannot exceed max size");
        }
    }
    
    /**
     * Check if cache is full.
     *
     * @return true if cache is at maximum capacity
     */
    public boolean isFull() {
        return currentSize >= maxSize;
    }
    
    /**
     * Check if cache is empty.
     *
     * @return true if cache has no entries
     */
    public boolean isEmpty() {
        return currentSize == 0;
    }
    
    /**
     * Get cache utilization percentage.
     *
     * @return Percentage of cache used (0-100)
     */
    public double getUtilization() {
        return (double) currentSize / maxSize * 100.0;
    }
}

