package com.kameleoon.weather.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SDKConfig.
 *
 * @author Yury Shuldeshov
 */
class SDKConfigTest {
    
    @Test
    @DisplayName("Should create config with valid parameters")
    void shouldCreateConfigWithValidParameters() {
        SDKConfig config = new SDKConfig(
            "test-api-key",
            OperationMode.ON_DEMAND,
            100,
            10,
            5,
            3
        );
        
        assertNotNull(config);
        assertEquals("test-api-key", config.apiKey());
        assertEquals(OperationMode.ON_DEMAND, config.operationMode());
        assertEquals(100, config.cacheMaxSize());
        assertEquals(10, config.cacheTtlMinutes());
        assertEquals(5, config.pollingIntervalMinutes());
        assertEquals(3, config.maxRetries());
    }
    
    @Test
    @DisplayName("Should throw exception for null API key")
    void shouldThrowExceptionForNullApiKey() {
        assertThrows(IllegalArgumentException.class, () ->
            new SDKConfig(null, OperationMode.ON_DEMAND, 100, 10, 5, 3));
    }
    
    @Test
    @DisplayName("Should throw exception for blank API key")
    void shouldThrowExceptionForBlankApiKey() {
        assertThrows(IllegalArgumentException.class, () ->
            new SDKConfig("", OperationMode.ON_DEMAND, 100, 10, 5, 3));
    }
    
    @Test
    @DisplayName("Should throw exception for null operation mode")
    void shouldThrowExceptionForNullOperationMode() {
        assertThrows(IllegalArgumentException.class, () ->
            new SDKConfig("test-key", null, 100, 10, 5, 3));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid cache size")
    void shouldThrowExceptionForInvalidCacheSize() {
        assertThrows(IllegalArgumentException.class, () ->
            new SDKConfig("test-key", OperationMode.ON_DEMAND, 0, 10, 5, 3));
        
        assertThrows(IllegalArgumentException.class, () ->
            new SDKConfig("test-key", OperationMode.ON_DEMAND, -1, 10, 5, 3));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid TTL")
    void shouldThrowExceptionForInvalidTtl() {
        assertThrows(IllegalArgumentException.class, () ->
            new SDKConfig("test-key", OperationMode.ON_DEMAND, 100, 0, 5, 3));
        
        assertThrows(IllegalArgumentException.class, () ->
            new SDKConfig("test-key", OperationMode.ON_DEMAND, 100, -1, 5, 3));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid polling interval")
    void shouldThrowExceptionForInvalidPollingInterval() {
        assertThrows(IllegalArgumentException.class, () ->
            new SDKConfig("test-key", OperationMode.POLLING, 100, 10, 0, 3));
        
        assertThrows(IllegalArgumentException.class, () ->
            new SDKConfig("test-key", OperationMode.POLLING, 100, 10, -1, 3));
    }
    
    @Test
    @DisplayName("Should throw exception for negative max retries")
    void shouldThrowExceptionForNegativeMaxRetries() {
        assertThrows(IllegalArgumentException.class, () ->
            new SDKConfig("test-key", OperationMode.ON_DEMAND, 100, 10, 5, -1));
    }
    
    @Test
    @DisplayName("Should create config using builder with defaults")
    void shouldCreateConfigUsingBuilderWithDefaults() {
        SDKConfig config = SDKConfig.builder("test-api-key").build();
        
        assertNotNull(config);
        assertEquals("test-api-key", config.apiKey());
        assertEquals(SDKConfig.DEFAULT_OPERATION_MODE, config.operationMode());
        assertEquals(SDKConfig.DEFAULT_CACHE_MAX_SIZE, config.cacheMaxSize());
        assertEquals(SDKConfig.DEFAULT_CACHE_TTL_MINUTES, config.cacheTtlMinutes());
        assertEquals(SDKConfig.DEFAULT_POLLING_INTERVAL_MINUTES, config.pollingIntervalMinutes());
        assertEquals(SDKConfig.DEFAULT_MAX_RETRIES, config.maxRetries());
    }
    
    @Test
    @DisplayName("Should create config using builder with custom values")
    void shouldCreateConfigUsingBuilderWithCustomValues() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .operationMode(OperationMode.POLLING)
            .cacheMaxSize(200)
            .cacheTtlMinutes(15)
            .pollingIntervalMinutes(10)
            .maxRetries(5)
            .build();
        
        assertNotNull(config);
        assertEquals("test-api-key", config.apiKey());
        assertEquals(OperationMode.POLLING, config.operationMode());
        assertEquals(200, config.cacheMaxSize());
        assertEquals(15, config.cacheTtlMinutes());
        assertEquals(10, config.pollingIntervalMinutes());
        assertEquals(5, config.maxRetries());
    }
    
    @Test
    @DisplayName("Should support method chaining in builder")
    void shouldSupportMethodChainingInBuilder() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .operationMode(OperationMode.POLLING)
            .cacheMaxSize(50)
            .cacheTtlMinutes(20)
            .pollingIntervalMinutes(3)
            .maxRetries(2)
            .build();
        
        assertNotNull(config);
        assertEquals(OperationMode.POLLING, config.operationMode());
        assertEquals(50, config.cacheMaxSize());
    }
}

