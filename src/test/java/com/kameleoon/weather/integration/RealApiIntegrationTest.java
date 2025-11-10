package com.kameleoon.weather.integration;

import com.kameleoon.weather.WeatherSDK;
import com.kameleoon.weather.config.OperationMode;
import com.kameleoon.weather.config.SDKConfig;
import com.kameleoon.weather.exception.CityNotFoundException;
import com.kameleoon.weather.exception.InvalidApiKeyException;
import com.kameleoon.weather.model.CacheInfo;
import com.kameleoon.weather.model.WeatherData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Integration tests with real OpenWeatherMap API.
 * 
 * <p>These tests require a valid OpenWeatherMap API key set in the
 * OPENWEATHERMAP_API_KEY environment variable. If the key is not set,
 * tests will be skipped.
 *
 * <p>To run these tests:
 * <pre>{@code
 * export OPENWEATHERMAP_API_KEY=your-real-api-key
 * mvn test -Dtest=RealApiIntegrationTest
 * }</pre>
 *
 * <p><strong>Note:</strong> These tests make real API calls and will count
 * against your API rate limits.
 *
 * @author Yury Shuldeshov
 */
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RealApiIntegrationTest {

    private static String apiKey;
    private WeatherSDK sdk;

    @BeforeAll
    static void checkApiKey() {
        apiKey = System.getenv("OPENWEATHERMAP_API_KEY");
        assumeTrue(apiKey != null && !apiKey.isBlank(), 
            "OPENWEATHERMAP_API_KEY environment variable is not set. Skipping integration tests.");
    }

    @AfterEach
    void tearDown() {
        if (sdk != null) {
            sdk.shutdown();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should successfully fetch weather for major city")
    void shouldFetchWeatherForMajorCity() {
        // Given
        SDKConfig config = SDKConfig.builder(apiKey)
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        sdk = new WeatherSDK(config);

        // When
        WeatherData weather = sdk.getWeather("London");

        // Then
        assertNotNull(weather);
        assertEquals("London", weather.name());
        assertNotNull(weather.temperature());
        assertNotNull(weather.weather());
        assertNotNull(weather.wind());
        assertTrue(weather.temperature().temp() > -100); // Reasonable temperature
        assertTrue(weather.temperature().temp() < 100);
        
        System.out.println("Successfully fetched weather for London:");
        System.out.println("  Temperature: " + weather.temperature().temp() + "°C");
        System.out.println("  Conditions: " + weather.weather().description());
    }

    @Test
    @Order(2)
    @DisplayName("Should fail with invalid city name")
    void shouldFailWithInvalidCityName() {
        // Given
        SDKConfig config = SDKConfig.builder(apiKey)
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        sdk = new WeatherSDK(config);

        // When & Then
        assertThrows(CityNotFoundException.class, () -> {
            sdk.getWeather("ThisCityDefinitelyDoesNotExist123456789");
        });
    }

    @Test
    @Order(3)
    @DisplayName("Should use cache for repeated requests")
    void shouldUseCacheForRepeatedRequests() {
        // Given
        SDKConfig config = SDKConfig.builder(apiKey)
            .operationMode(OperationMode.ON_DEMAND)
            .cacheMaxSize(100)
            .cacheTtlMinutes(10)
            .build();
        sdk = new WeatherSDK(config);

        // When
        WeatherData firstRequest = sdk.getWeather("Paris");
        WeatherData secondRequest = sdk.getWeather("Paris");

        // Then
        assertNotNull(firstRequest);
        assertNotNull(secondRequest);
        assertEquals(firstRequest.name(), secondRequest.name());
        
        CacheInfo cacheInfo = sdk.getCacheInfo();
        assertTrue(cacheInfo.currentSize() > 0);
        assertTrue(cacheInfo.cachedCities().contains("paris"));
        
        System.out.println("Cache stats after 2 requests:");
        System.out.println("  Cache size: " + cacheInfo.currentSize());
        System.out.println("  Cached cities: " + cacheInfo.cachedCities());
    }

    @Test
    @Order(4)
    @DisplayName("Should work with multiple cities")
    void shouldWorkWithMultipleCities() {
        // Given
        SDKConfig config = SDKConfig.builder(apiKey)
            .operationMode(OperationMode.ON_DEMAND)
            .cacheMaxSize(100)
            .cacheTtlMinutes(10)
            .build();
        sdk = new WeatherSDK(config);
        
        String[] cities = {"London", "Paris", "Berlin", "Madrid", "Rome"};

        // When
        for (String city : cities) {
            WeatherData weather = sdk.getWeather(city);
            
            // Then
            assertNotNull(weather);
            assertEquals(city, weather.name());
            System.out.println(city + ": " + weather.temperature().temp() + "°C - " + 
                             weather.weather().description());
        }

        // Verify cache
        CacheInfo cacheInfo = sdk.getCacheInfo();
        assertEquals(5, cacheInfo.currentSize());
        assertEquals(5, cacheInfo.cachedCities().size());
    }

    @Test
    @Order(5)
    @DisplayName("Should work in POLLING mode")
    void shouldWorkInPollingMode() throws InterruptedException {
        // Given
        SDKConfig config = SDKConfig.builder(apiKey)
            .operationMode(OperationMode.POLLING)
            .pollingIntervalMinutes(1)
            .cacheMaxSize(100)
            .cacheTtlMinutes(10)
            .build();
        sdk = new WeatherSDK(config);

        // When
        sdk.registerLocation("London");
        sdk.registerLocation("Paris");
        
        // Wait for initial polling
        Thread.sleep(3000);

        // Then
        WeatherData londonWeather = sdk.getWeather("London");
        WeatherData parisWeather = sdk.getWeather("Paris");
        
        assertNotNull(londonWeather);
        assertNotNull(parisWeather);
        
        assertEquals(2, sdk.getRegisteredLocations().size());
        assertTrue(sdk.getRegisteredLocations().contains("london"));
        assertTrue(sdk.getRegisteredLocations().contains("paris"));
        
        System.out.println("Polling mode test successful:");
        System.out.println("  Registered locations: " + sdk.getRegisteredLocations());
        System.out.println("  London: " + londonWeather.temperature().temp() + "°C");
        System.out.println("  Paris: " + parisWeather.temperature().temp() + "°C");
    }

    @Test
    @Order(6)
    @DisplayName("Should fail with invalid API key")
    void shouldFailWithInvalidApiKey() {
        // Given
        SDKConfig config = SDKConfig.builder("invalid-api-key-12345")
            .operationMode(OperationMode.ON_DEMAND)
            .maxRetries(1) // Reduce retries for faster test
            .build();
        sdk = new WeatherSDK(config);

        // When & Then
        assertThrows(InvalidApiKeyException.class, () -> {
            sdk.getWeather("London");
        });
    }

    @Test
    @Order(7)
    @DisplayName("Should handle city names with spaces and special characters")
    void shouldHandleCityNamesWithSpacesAndSpecialCharacters() {
        // Given
        SDKConfig config = SDKConfig.builder(apiKey)
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        sdk = new WeatherSDK(config);

        // When
        WeatherData weather = sdk.getWeather("New York");

        // Then
        assertNotNull(weather);
        assertTrue(weather.name().contains("New York") || weather.name().contains("New"));
        assertNotNull(weather.temperature());
        
        System.out.println("Fetched weather for 'New York': " + 
                         weather.name() + " - " + weather.temperature().temp() + "°C");
    }

    @Test
    @Order(8)
    @DisplayName("Should clear cache successfully")
    void shouldClearCacheSuccessfully() {
        // Given
        SDKConfig config = SDKConfig.builder(apiKey)
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        sdk = new WeatherSDK(config);
        
        // Add some data to cache
        sdk.getWeather("London");
        sdk.getWeather("Paris");
        
        CacheInfo beforeClear = sdk.getCacheInfo();
        assertTrue(beforeClear.currentSize() > 0);

        // When
        sdk.clearCache();

        // Then
        CacheInfo afterClear = sdk.getCacheInfo();
        assertEquals(0, afterClear.currentSize());
        assertTrue(afterClear.isEmpty());
        
        System.out.println("Cache cleared successfully:");
        System.out.println("  Before: " + beforeClear.currentSize() + " entries");
        System.out.println("  After: " + afterClear.currentSize() + " entries");
    }

    @Test
    @Order(9)
    @DisplayName("Should respect cache TTL")
    void shouldRespectCacheTTL() throws InterruptedException {
        // Given - very short TTL for testing
        SDKConfig config = SDKConfig.builder(apiKey)
            .operationMode(OperationMode.ON_DEMAND)
            .cacheTtlMinutes(0) // Essentially expired immediately
            .build();
        sdk = new WeatherSDK(config);

        // When
        WeatherData firstRequest = sdk.getWeather("London");
        Thread.sleep(1000); // Wait for cache to expire
        WeatherData secondRequest = sdk.getWeather("London");

        // Then
        assertNotNull(firstRequest);
        assertNotNull(secondRequest);
        assertEquals("London", firstRequest.name());
        assertEquals("London", secondRequest.name());
        
        System.out.println("TTL test completed - both requests succeeded");
    }
}

