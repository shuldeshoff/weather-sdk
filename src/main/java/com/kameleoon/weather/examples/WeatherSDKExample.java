package com.kameleoon.weather.examples;

import com.kameleoon.weather.WeatherSDK;
import com.kameleoon.weather.config.OperationMode;
import com.kameleoon.weather.config.SDKConfig;
import com.kameleoon.weather.model.CacheInfo;
import com.kameleoon.weather.model.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example usage of the Weather SDK.
 * Demonstrates both ON_DEMAND and POLLING modes.
 *
 * @author Yury Shuldeshov
 */
public class WeatherSDKExample {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherSDKExample.class);
    
    /**
     * Example of using SDK in ON_DEMAND mode.
     * Weather data is fetched only when explicitly requested.
     */
    public static void onDemandModeExample() {
        logger.info("=== ON_DEMAND Mode Example ===");
        
        // Create configuration
        SDKConfig config = SDKConfig.builder("your-api-key-here")
            .operationMode(OperationMode.ON_DEMAND)
            .cacheMaxSize(100)
            .cacheTtlMinutes(10)
            .maxRetries(3)
            .build();
        
        // Create SDK instance
        WeatherSDK sdk = new WeatherSDK(config);
        
        try {
            // Get weather for a city
            WeatherData weather = sdk.getWeather("London");
            printWeatherData(weather);
            
            // Subsequent requests within TTL will use cache
            WeatherData cachedWeather = sdk.getWeather("London");
            printWeatherData(cachedWeather);
            
            // Check cache statistics
            CacheInfo cacheInfo = sdk.getCacheInfo();
            logger.info("Cache Statistics:");
            logger.info("Size: {}/{}", cacheInfo.currentSize(), cacheInfo.maxSize());
            logger.info("Utilization: {}", String.format("%.2f%%", cacheInfo.getUtilization()));
            
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
        } finally {
            // Always shutdown when done
            sdk.shutdown();
        }
    }
    
    /**
     * Example of using SDK in POLLING mode.
     * Weather data is automatically updated at regular intervals.
     */
    public static void pollingModeExample() throws InterruptedException {
        logger.info("=== POLLING Mode Example ===");
        
        // Create configuration for POLLING mode
        SDKConfig config = SDKConfig.builder("your-api-key-here")
            .operationMode(OperationMode.POLLING)
            .cacheMaxSize(100)
            .cacheTtlMinutes(10)
            .pollingIntervalMinutes(5)  // Update every 5 minutes
            .maxRetries(3)
            .build();
        
        // Create SDK instance
        WeatherSDK sdk = new WeatherSDK(config);
        
        try {
            // Register locations for automatic polling
            sdk.registerLocation("London");
            sdk.registerLocation("Paris");
            sdk.registerLocation("New York");
            
            logger.info("Registered {} locations", sdk.getRegisteredLocations().size());
            logger.info("Locations: {}", sdk.getRegisteredLocations());
            
            // Wait for initial data to be fetched
            Thread.sleep(2000);
            
            // Get weather data (will be served from cache, updated automatically)
            WeatherData londonWeather = sdk.getWeather("London");
            printWeatherData(londonWeather);
            
            WeatherData parisWeather = sdk.getWeather("Paris");
            printWeatherData(parisWeather);
            
            // Manually trigger refresh of all locations
            sdk.refreshAll();
            
            // Unregister a location
            sdk.unregisterLocation("New York");
            logger.info("Remaining locations: {}", sdk.getRegisteredLocations());
            
            // Check cache statistics
            CacheInfo cacheInfo = sdk.getCacheInfo();
            logger.info("Cache Statistics:");
            logger.info("Size: {}/{}", cacheInfo.currentSize(), cacheInfo.maxSize());
            logger.info("Utilization: {}", String.format("%.2f%%", cacheInfo.getUtilization()));
            
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
        } finally {
            // Always shutdown when done (stops polling service)
            sdk.shutdown();
        }
    }
    
    /**
     * Prints weather data in a formatted way.
     */
    private static void printWeatherData(WeatherData weather) {
        logger.info("--- Weather for {} ---", weather.name());
        logger.info("Conditions: {} - {}", weather.weather().main(), weather.weather().description());
        logger.info("Temperature: {}°C", weather.temperature().temp());
        logger.info("Feels like: {}°C", weather.temperature().feelsLike());
        logger.info("Wind speed: {} m/s", weather.wind().speed());
        logger.info("Visibility: {} meters", weather.visibility());
        logger.info("Timezone offset: {} seconds from UTC", weather.timezone());
    }
    
    /**
     * Main method demonstrating both modes.
     * Note: Replace "your-api-key-here" with actual API key.
     */
    public static void main(String[] args) {
        try {
            // Example 1: ON_DEMAND mode
            onDemandModeExample();
            
            // Example 2: POLLING mode
            pollingModeExample();
            
        } catch (Exception e) {
            logger.error("Example failed: {}", e.getMessage(), e);
        }
    }
}

