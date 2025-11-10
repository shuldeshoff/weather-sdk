package com.kameleoon.weather.service;

import com.kameleoon.weather.model.CacheEntry;
import com.kameleoon.weather.model.WeatherData;
import com.kameleoon.weather.util.LRUCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for caching weather data.
 * Implements LRU eviction policy with TTL (Time-To-Live).
 * Thread-safe for concurrent access.
 *
 * @author Yury Shuldeshov
 */
public class CacheService {
    
    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);
    
    private final LRUCache<String, CacheEntry> cache;
    private final long ttlMillis;
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    
    /**
     * Creates a new CacheService with specified capacity and TTL.
     *
     * @param maxSize Maximum number of cities to cache
     * @param ttlMinutes Time-to-live for cache entries in minutes
     * @throws IllegalArgumentException if parameters are invalid
     */
    public CacheService(int maxSize, long ttlMinutes) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be positive");
        }
        if (ttlMinutes <= 0) {
            throw new IllegalArgumentException("TTL must be positive");
        }
        
        this.cache = new LRUCache<>(maxSize);
        this.ttlMillis = ttlMinutes * 60 * 1000;
        
        logger.info("CacheService initialized: maxSize={}, ttlMinutes={}", maxSize, ttlMinutes);
    }
    
    /**
     * Gets weather data from cache if present and not expired.
     *
     * @param cityName Name of the city (case-insensitive)
     * @return Optional containing weather data if valid, empty otherwise
     */
    public Optional<WeatherData> get(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            return Optional.empty();
        }
        
        String normalizedCity = normalizeCityName(cityName);
        CacheEntry entry = cache.get(normalizedCity);
        
        if (entry == null) {
            cacheMisses.incrementAndGet();
            logger.debug("Cache miss for city: {}", cityName);
            return Optional.empty();
        }
        
        if (entry.isExpired(ttlMillis)) {
            cache.remove(normalizedCity);
            cacheMisses.incrementAndGet();
            logger.debug("Cache entry expired for city: {}", cityName);
            return Optional.empty();
        }
        
        cacheHits.incrementAndGet();
        logger.debug("Cache hit for city: {} (age: {}ms)", cityName, entry.getAge());
        return Optional.of(entry.data());
    }
    
    /**
     * Puts weather data into the cache.
     *
     * @param cityName Name of the city
     * @param data Weather data to cache
     */
    public void put(String cityName, WeatherData data) {
        if (cityName == null || cityName.isBlank()) {
            logger.warn("Attempted to cache with null/blank city name");
            return;
        }
        if (data == null) {
            logger.warn("Attempted to cache null data for city: {}", cityName);
            return;
        }
        
        String normalizedCity = normalizeCityName(cityName);
        CacheEntry entry = new CacheEntry(data, System.currentTimeMillis());
        cache.put(normalizedCity, entry);
        
        logger.debug("Cached weather data for city: {} (cache size: {})", 
            cityName, cache.size());
    }
    
    /**
     * Checks if a city's cached data is still valid.
     *
     * @param cityName Name of the city
     * @return true if cached and not expired, false otherwise
     */
    public boolean isValid(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            return false;
        }
        
        String normalizedCity = normalizeCityName(cityName);
        CacheEntry entry = cache.get(normalizedCity);
        
        return entry != null && !entry.isExpired(ttlMillis);
    }
    
    /**
     * Removes a specific city from the cache.
     *
     * @param cityName Name of the city
     */
    public void evict(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            return;
        }
        
        String normalizedCity = normalizeCityName(cityName);
        cache.remove(normalizedCity);
        
        logger.debug("Evicted city from cache: {}", cityName);
    }
    
    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        cache.clear();
        cacheHits.set(0);
        cacheMisses.set(0);
        logger.info("Cache cleared");
    }
    
    /**
     * Gets the set of all cached city names.
     * Returns a read-only view of normalized city names.
     *
     * @return Set of city names currently in cache (normalized to lowercase)
     */
    public Set<String> getCachedCities() {
        return Set.copyOf(cache.keySet());
    }
    
    /**
     * Gets the current number of entries in the cache.
     *
     * @return Number of cached entries
     */
    public int getSize() {
        return cache.size();
    }
    
    /**
     * Gets the maximum capacity of the cache.
     *
     * @return Maximum number of entries
     */
    public int getMaxSize() {
        return cache.getMaxSize();
    }
    
    /**
     * Gets the cache hit rate.
     *
     * @return Hit rate as a value between 0.0 and 1.0
     */
    public double getCacheHitRate() {
        long total = cacheHits.get() + cacheMisses.get();
        return total == 0 ? 0.0 : (double) cacheHits.get() / total;
    }
    
    /**
     * Gets the number of cache hits.
     *
     * @return Number of cache hits
     */
    public long getCacheHits() {
        return cacheHits.get();
    }
    
    /**
     * Gets the number of cache misses.
     *
     * @return Number of cache misses
     */
    public long getCacheMisses() {
        return cacheMisses.get();
    }
    
    /**
     * Normalizes city name for consistent cache keys (lowercase, trimmed).
     */
    private String normalizeCityName(String cityName) {
        return cityName.toLowerCase().trim();
    }
}

