package com.kameleoon.weather;

import com.kameleoon.weather.config.OperationMode;
import com.kameleoon.weather.config.SDKConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WeatherSDKFactory.
 *
 * @author Yury Shuldeshov
 */
class WeatherSDKFactoryTest {
    
    @BeforeEach
    void setUp() {
        // Clean up before each test
        WeatherSDKFactory.clearInstances();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        WeatherSDKFactory.shutdownAll();
    }
    
    @Test
    @DisplayName("Should create SDK instance with config")
    void shouldCreateInstanceWithConfig() {
        SDKConfig config = SDKConfig.builder("test-api-key-1").build();
        
        WeatherSDK sdk = WeatherSDKFactory.getInstance(config);
        
        assertNotNull(sdk);
        assertEquals(1, WeatherSDKFactory.getInstanceCount());
    }
    
    @Test
    @DisplayName("Should return same instance for same API key")
    void shouldReturnSameInstanceForSameApiKey() {
        SDKConfig config1 = SDKConfig.builder("test-api-key-1")
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        
        SDKConfig config2 = SDKConfig.builder("test-api-key-1")
            .operationMode(OperationMode.POLLING)
            .pollingIntervalMinutes(10)
            .build();
        
        WeatherSDK sdk1 = WeatherSDKFactory.getInstance(config1);
        WeatherSDK sdk2 = WeatherSDKFactory.getInstance(config2);
        
        assertSame(sdk1, sdk2, "Should return the same instance");
        assertEquals(1, WeatherSDKFactory.getInstanceCount());
    }
    
    @Test
    @DisplayName("Should create different instances for different API keys")
    void shouldCreateDifferentInstancesForDifferentApiKeys() {
        SDKConfig config1 = SDKConfig.builder("test-api-key-1").build();
        SDKConfig config2 = SDKConfig.builder("test-api-key-2").build();
        
        WeatherSDK sdk1 = WeatherSDKFactory.getInstance(config1);
        WeatherSDK sdk2 = WeatherSDKFactory.getInstance(config2);
        
        assertNotSame(sdk1, sdk2);
        assertEquals(2, WeatherSDKFactory.getInstanceCount());
    }
    
    @Test
    @DisplayName("Should throw exception for null config")
    void shouldThrowExceptionForNullConfig() {
        assertThrows(IllegalArgumentException.class, () ->
            WeatherSDKFactory.getInstance((SDKConfig) null));
    }
    
    @Test
    @DisplayName("Should get existing instance by API key")
    void shouldGetExistingInstanceByApiKey() {
        SDKConfig config = SDKConfig.builder("test-api-key-1").build();
        WeatherSDK sdk1 = WeatherSDKFactory.getInstance(config);
        
        WeatherSDK sdk2 = WeatherSDKFactory.getInstance("test-api-key-1");
        
        assertSame(sdk1, sdk2);
    }
    
    @Test
    @DisplayName("Should return null for non-existent API key")
    void shouldReturnNullForNonExistentApiKey() {
        WeatherSDK sdk = WeatherSDKFactory.getInstance("non-existent-key");
        
        assertNull(sdk);
    }
    
    @Test
    @DisplayName("Should throw exception for null API key in getInstance")
    void shouldThrowExceptionForNullApiKeyInGetInstance() {
        assertThrows(IllegalArgumentException.class, () ->
            WeatherSDKFactory.getInstance((String) null));
    }
    
    @Test
    @DisplayName("Should throw exception for blank API key in getInstance")
    void shouldThrowExceptionForBlankApiKeyInGetInstance() {
        assertThrows(IllegalArgumentException.class, () ->
            WeatherSDKFactory.getInstance(""));
        
        assertThrows(IllegalArgumentException.class, () ->
            WeatherSDKFactory.getInstance("   "));
    }
    
    @Test
    @DisplayName("Should check if instance exists")
    void shouldCheckIfInstanceExists() {
        assertFalse(WeatherSDKFactory.hasInstance("test-api-key-1"));
        
        SDKConfig config = SDKConfig.builder("test-api-key-1").build();
        WeatherSDKFactory.getInstance(config);
        
        assertTrue(WeatherSDKFactory.hasInstance("test-api-key-1"));
        assertFalse(WeatherSDKFactory.hasInstance("non-existent-key"));
    }
    
    @Test
    @DisplayName("Should return false for null/blank API key in hasInstance")
    void shouldReturnFalseForNullBlankApiKeyInHasInstance() {
        assertFalse(WeatherSDKFactory.hasInstance(null));
        assertFalse(WeatherSDKFactory.hasInstance(""));
        assertFalse(WeatherSDKFactory.hasInstance("   "));
    }
    
    @Test
    @DisplayName("Should get active keys")
    void shouldGetActiveKeys() {
        SDKConfig config1 = SDKConfig.builder("test-api-key-1").build();
        SDKConfig config2 = SDKConfig.builder("test-api-key-2").build();
        
        WeatherSDKFactory.getInstance(config1);
        WeatherSDKFactory.getInstance(config2);
        
        var activeKeys = WeatherSDKFactory.getActiveKeys();
        
        assertEquals(2, activeKeys.size());
    }
    
    @Test
    @DisplayName("Should return immutable set of active keys")
    void shouldReturnImmutableSetOfActiveKeys() {
        SDKConfig config = SDKConfig.builder("test-api-key-1").build();
        WeatherSDKFactory.getInstance(config);
        
        var activeKeys = WeatherSDKFactory.getActiveKeys();
        
        assertThrows(UnsupportedOperationException.class, () ->
            activeKeys.add("new-key"));
    }
    
    @Test
    @DisplayName("Should get instance count")
    void shouldGetInstanceCount() {
        assertEquals(0, WeatherSDKFactory.getInstanceCount());
        
        SDKConfig config1 = SDKConfig.builder("test-api-key-1").build();
        WeatherSDKFactory.getInstance(config1);
        assertEquals(1, WeatherSDKFactory.getInstanceCount());
        
        SDKConfig config2 = SDKConfig.builder("test-api-key-2").build();
        WeatherSDKFactory.getInstance(config2);
        assertEquals(2, WeatherSDKFactory.getInstanceCount());
    }
    
    @Test
    @DisplayName("Should shutdown specific instance")
    void shouldShutdownSpecificInstance() {
        SDKConfig config1 = SDKConfig.builder("test-api-key-1").build();
        SDKConfig config2 = SDKConfig.builder("test-api-key-2").build();
        
        WeatherSDKFactory.getInstance(config1);
        WeatherSDKFactory.getInstance(config2);
        
        boolean result = WeatherSDKFactory.shutdownInstance("test-api-key-1");
        
        assertTrue(result);
        assertEquals(1, WeatherSDKFactory.getInstanceCount());
        assertFalse(WeatherSDKFactory.hasInstance("test-api-key-1"));
        assertTrue(WeatherSDKFactory.hasInstance("test-api-key-2"));
    }
    
    @Test
    @DisplayName("Should return false when shutting down non-existent instance")
    void shouldReturnFalseWhenShuttingDownNonExistentInstance() {
        boolean result = WeatherSDKFactory.shutdownInstance("non-existent-key");
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should throw exception for null API key in shutdownInstance")
    void shouldThrowExceptionForNullApiKeyInShutdownInstance() {
        assertThrows(IllegalArgumentException.class, () ->
            WeatherSDKFactory.shutdownInstance(null));
    }
    
    @Test
    @DisplayName("Should throw exception for blank API key in shutdownInstance")
    void shouldThrowExceptionForBlankApiKeyInShutdownInstance() {
        assertThrows(IllegalArgumentException.class, () ->
            WeatherSDKFactory.shutdownInstance(""));
        
        assertThrows(IllegalArgumentException.class, () ->
            WeatherSDKFactory.shutdownInstance("   "));
    }
    
    @Test
    @DisplayName("Should shutdown all instances")
    void shouldShutdownAllInstances() {
        SDKConfig config1 = SDKConfig.builder("test-api-key-1").build();
        SDKConfig config2 = SDKConfig.builder("test-api-key-2").build();
        SDKConfig config3 = SDKConfig.builder("test-api-key-3").build();
        
        WeatherSDKFactory.getInstance(config1);
        WeatherSDKFactory.getInstance(config2);
        WeatherSDKFactory.getInstance(config3);
        
        assertEquals(3, WeatherSDKFactory.getInstanceCount());
        
        WeatherSDKFactory.shutdownAll();
        
        assertEquals(0, WeatherSDKFactory.getInstanceCount());
        assertFalse(WeatherSDKFactory.hasInstance("test-api-key-1"));
        assertFalse(WeatherSDKFactory.hasInstance("test-api-key-2"));
        assertFalse(WeatherSDKFactory.hasInstance("test-api-key-3"));
    }
    
    @Test
    @DisplayName("Should handle concurrent getInstance calls")
    void shouldHandleConcurrentGetInstanceCalls() throws InterruptedException {
        int threadCount = 10;
        SDKConfig config = SDKConfig.builder("test-api-key-1").build();
        
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        List<WeatherSDK> sdks = new ArrayList<>();
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    WeatherSDK sdk = WeatherSDKFactory.getInstance(config);
                    synchronized (sdks) {
                        sdks.add(sdk);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        
        startLatch.countDown();
        doneLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        
        // All threads should get the same instance
        assertEquals(threadCount, sdks.size());
        WeatherSDK firstSdk = sdks.get(0);
        for (WeatherSDK sdk : sdks) {
            assertSame(firstSdk, sdk, "All threads should get the same instance");
        }
        
        assertEquals(1, WeatherSDKFactory.getInstanceCount());
    }
    
    @Test
    @DisplayName("Should handle concurrent getInstance calls with different keys")
    void shouldHandleConcurrentGetInstanceCallsWithDifferentKeys() throws InterruptedException {
        int threadCount = 10;
        int keysCount = 3;
        
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int keyIndex = i % keysCount;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    SDKConfig config = SDKConfig.builder("test-api-key-" + keyIndex).build();
                    WeatherSDKFactory.getInstance(config);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        
        startLatch.countDown();
        doneLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        
        assertEquals(keysCount, WeatherSDKFactory.getInstanceCount());
    }
}

