package com.kameleoon.weather;

import com.kameleoon.weather.config.OperationMode;
import com.kameleoon.weather.config.SDKConfig;
import com.kameleoon.weather.model.CacheInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WeatherSDK.
 *
 * @author Yury Shuldeshov
 */
class WeatherSDKTest {
    
    private WeatherSDK sdk;
    
    @AfterEach
    void tearDown() {
        if (sdk != null) {
            sdk.shutdown();
        }
    }
    
    @Test
    @DisplayName("Should create SDK in ON_DEMAND mode")
    void shouldCreateSdkInOnDemandMode() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        
        sdk = new WeatherSDK(config);
        
        assertNotNull(sdk);
        assertEquals(config, sdk.getConfig());
    }
    
    @Test
    @DisplayName("Should create SDK in POLLING mode")
    void shouldCreateSdkInPollingMode() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .operationMode(OperationMode.POLLING)
            .pollingIntervalMinutes(1)
            .build();
        
        sdk = new WeatherSDK(config);
        
        assertNotNull(sdk);
        assertEquals(config, sdk.getConfig());
    }
    
    @Test
    @DisplayName("Should throw exception for null config")
    void shouldThrowExceptionForNullConfig() {
        assertThrows(IllegalArgumentException.class, () ->
            new WeatherSDK(null));
    }
    
    @Test
    @DisplayName("Should register location in POLLING mode")
    void shouldRegisterLocationInPollingMode() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .operationMode(OperationMode.POLLING)
            .pollingIntervalMinutes(1)
            .build();
        
        sdk = new WeatherSDK(config);
        
        boolean result = sdk.registerLocation("London");
        
        assertTrue(result);
        assertTrue(sdk.getRegisteredLocations().contains("london"));
    }
    
    @Test
    @DisplayName("Should throw exception when registering location in ON_DEMAND mode")
    void shouldThrowExceptionWhenRegisteringLocationInOnDemandMode() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        
        sdk = new WeatherSDK(config);
        
        assertThrows(UnsupportedOperationException.class, () ->
            sdk.registerLocation("London"));
    }
    
    @Test
    @DisplayName("Should unregister location in POLLING mode")
    void shouldUnregisterLocationInPollingMode() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .operationMode(OperationMode.POLLING)
            .pollingIntervalMinutes(1)
            .build();
        
        sdk = new WeatherSDK(config);
        sdk.registerLocation("London");
        
        boolean result = sdk.unregisterLocation("London");
        
        assertTrue(result);
        assertFalse(sdk.getRegisteredLocations().contains("london"));
    }
    
    @Test
    @DisplayName("Should throw exception when unregistering location in ON_DEMAND mode")
    void shouldThrowExceptionWhenUnregisteringLocationInOnDemandMode() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        
        sdk = new WeatherSDK(config);
        
        assertThrows(UnsupportedOperationException.class, () ->
            sdk.unregisterLocation("London"));
    }
    
    @Test
    @DisplayName("Should get cache info")
    void shouldGetCacheInfo() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .cacheMaxSize(100)
            .build();
        
        sdk = new WeatherSDK(config);
        CacheInfo cacheInfo = sdk.getCacheInfo();
        
        assertNotNull(cacheInfo);
        assertEquals(100, cacheInfo.maxSize());
        assertEquals(0, cacheInfo.currentSize());
    }
    
    @Test
    @DisplayName("Should clear cache")
    void shouldClearCache() {
        SDKConfig config = SDKConfig.builder("test-api-key").build();
        
        sdk = new WeatherSDK(config);
        sdk.clearCache();
        
        CacheInfo cacheInfo = sdk.getCacheInfo();
        assertEquals(0, cacheInfo.currentSize());
    }
    
    @Test
    @DisplayName("Should throw exception when calling refreshAll in ON_DEMAND mode")
    void shouldThrowExceptionWhenCallingRefreshAllInOnDemandMode() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        
        sdk = new WeatherSDK(config);
        
        assertThrows(UnsupportedOperationException.class, () ->
            sdk.refreshAll());
    }
    
    @Test
    @DisplayName("Should throw exception when getting registered locations in ON_DEMAND mode")
    void shouldThrowExceptionWhenGettingRegisteredLocationsInOnDemandMode() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        
        sdk = new WeatherSDK(config);
        
        assertThrows(UnsupportedOperationException.class, () ->
            sdk.getRegisteredLocations());
    }
    
    @Test
    @DisplayName("Should shutdown gracefully")
    void shouldShutdownGracefully() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .operationMode(OperationMode.POLLING)
            .pollingIntervalMinutes(1)
            .build();
        
        sdk = new WeatherSDK(config);
        
        assertDoesNotThrow(() -> sdk.shutdown());
    }
    
    @Test
    @DisplayName("Should register multiple locations in POLLING mode")
    void shouldRegisterMultipleLocationsInPollingMode() {
        SDKConfig config = SDKConfig.builder("test-api-key")
            .operationMode(OperationMode.POLLING)
            .pollingIntervalMinutes(1)
            .build();
        
        sdk = new WeatherSDK(config);
        
        sdk.registerLocation("London");
        sdk.registerLocation("Paris");
        sdk.registerLocation("Berlin");
        
        assertEquals(3, sdk.getRegisteredLocations().size());
    }
}

