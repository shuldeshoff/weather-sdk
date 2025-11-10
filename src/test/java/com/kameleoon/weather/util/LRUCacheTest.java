package com.kameleoon.weather.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LRUCache.
 *
 * @author Yury Shuldeshov
 */
class LRUCacheTest {
    
    @Test
    void shouldCreateCacheWithValidSize() {
        LRUCache<String, String> cache = new LRUCache<>(5);
        assertEquals(5, cache.getMaxSize());
        assertEquals(0, cache.size());
        assertTrue(cache.isEmpty());
    }
    
    @Test
    void shouldThrowExceptionForInvalidSize() {
        assertThrows(IllegalArgumentException.class, () -> new LRUCache<String, String>(0));
        assertThrows(IllegalArgumentException.class, () -> new LRUCache<String, String>(-1));
    }
    
    @Test
    void shouldPutAndGetValues() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        
        cache.put("one", 1);
        cache.put("two", 2);
        
        assertEquals(1, cache.get("one"));
        assertEquals(2, cache.get("two"));
        assertEquals(2, cache.size());
    }
    
    @Test
    void shouldReturnNullForMissingKey() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        assertNull(cache.get("missing"));
    }
    
    @Test
    void shouldEvictLeastRecentlyUsed() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        
        cache.put("one", 1);
        cache.put("two", 2);
        cache.put("three", 3);
        
        // Cache is full, adding one more should evict "one"
        cache.put("four", 4);
        
        assertNull(cache.get("one")); // Evicted
        assertEquals(2, cache.get("two"));
        assertEquals(3, cache.get("three"));
        assertEquals(4, cache.get("four"));
        assertEquals(3, cache.size());
    }
    
    @Test
    void shouldUpdateAccessOrderOnGet() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        
        cache.put("one", 1);
        cache.put("two", 2);
        cache.put("three", 3);
        
        // Access "one" to make it recently used
        cache.get("one");
        
        // Add new entry, should evict "two" (least recently used)
        cache.put("four", 4);
        
        assertEquals(1, cache.get("one")); // Still present
        assertNull(cache.get("two")); // Evicted
        assertEquals(3, cache.get("three"));
        assertEquals(4, cache.get("four"));
    }
    
    @Test
    void shouldRemoveEntry() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        
        cache.put("one", 1);
        cache.put("two", 2);
        
        assertEquals(1, cache.remove("one"));
        assertNull(cache.get("one"));
        assertEquals(1, cache.size());
    }
    
    @Test
    void shouldContainKey() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        
        cache.put("one", 1);
        
        assertTrue(cache.containsKey("one"));
        assertFalse(cache.containsKey("two"));
    }
    
    @Test
    void shouldClearCache() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        
        cache.put("one", 1);
        cache.put("two", 2);
        
        cache.clear();
        
        assertEquals(0, cache.size());
        assertTrue(cache.isEmpty());
        assertNull(cache.get("one"));
    }
    
    @Test
    void shouldHandleConcurrentAccess() throws InterruptedException {
        LRUCache<String, Integer> cache = new LRUCache<>(100);
        
        // Create multiple threads to access cache concurrently
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    String key = "key-" + (threadId * 100 + j);
                    cache.put(key, j);
                    cache.get(key);
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
        
        // Cache should be at max capacity
        assertEquals(100, cache.size());
    }
}

