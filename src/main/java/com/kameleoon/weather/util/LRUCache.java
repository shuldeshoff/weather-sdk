package com.kameleoon.weather.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Thread-safe LRU (Least Recently Used) Cache implementation.
 * Automatically evicts the least recently used entry when capacity is exceeded.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @author Yury Shuldeshov
 */
public class LRUCache<K, V> {
    
    private final int maxSize;
    private final Map<K, V> cache;
    
    /**
     * Creates a new LRU cache with the specified maximum size.
     *
     * @param maxSize Maximum number of entries in the cache
     * @throws IllegalArgumentException if maxSize is not positive
     */
    public LRUCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be positive");
        }
        
        this.maxSize = maxSize;
        
        // LinkedHashMap with access order and automatic eviction
        this.cache = new LinkedHashMap<K, V>(maxSize + 1, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }
    
    /**
     * Gets a value from the cache.
     * Updates the access order (moves to end).
     *
     * @param key The key
     * @return The value, or null if not present
     */
    public synchronized V get(K key) {
        return cache.get(key);
    }
    
    /**
     * Puts a value into the cache.
     * If cache is full, evicts the least recently used entry.
     *
     * @param key The key
     * @param value The value
     */
    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }
    
    /**
     * Removes a value from the cache.
     *
     * @param key The key
     * @return The removed value, or null if not present
     */
    public synchronized V remove(K key) {
        return cache.remove(key);
    }
    
    /**
     * Checks if the cache contains a key.
     *
     * @param key The key
     * @return true if present, false otherwise
     */
    public synchronized boolean containsKey(K key) {
        return cache.containsKey(key);
    }
    
    /**
     * Clears all entries from the cache.
     */
    public synchronized void clear() {
        cache.clear();
    }
    
    /**
     * Gets the current size of the cache.
     *
     * @return Number of entries in the cache
     */
    public synchronized int size() {
        return cache.size();
    }
    
    /**
     * Checks if the cache is empty.
     *
     * @return true if empty, false otherwise
     */
    public synchronized boolean isEmpty() {
        return cache.isEmpty();
    }
    
    /**
     * Gets the maximum capacity of the cache.
     *
     * @return Maximum number of entries
     */
    public int getMaxSize() {
        return maxSize;
    }
    
    /**
     * Gets a copy of all keys currently in the cache.
     *
     * @return Set of all keys
     */
    public synchronized java.util.Set<K> keySet() {
        return new java.util.HashSet<>(cache.keySet());
    }
}

