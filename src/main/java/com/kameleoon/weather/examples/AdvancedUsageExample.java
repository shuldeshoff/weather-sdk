package com.kameleoon.weather.examples;

import com.kameleoon.weather.WeatherSDK;
import com.kameleoon.weather.WeatherSDKFactory;
import com.kameleoon.weather.config.OperationMode;
import com.kameleoon.weather.config.SDKConfig;
import com.kameleoon.weather.model.CacheInfo;
import com.kameleoon.weather.model.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Advanced usage examples demonstrating sophisticated SDK features.
 * Includes caching strategies, performance optimization, and production patterns.
 *
 * @author Yury Shuldeshov
 */
public class AdvancedUsageExample {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvancedUsageExample.class);
    
    /**
     * Example demonstrating optimal caching strategy.
     */
    public static void cachingStrategyExample() {
        logger.info("=== Caching Strategy Example ===");
        
        // Configure cache for optimal performance
        SDKConfig config = SDKConfig.builder("your-api-key-here")
            .operationMode(OperationMode.ON_DEMAND)
            .cacheMaxSize(200)           // Store up to 200 cities
            .cacheTtlMinutes(10)         // 10 minutes TTL
            .maxRetries(3)
            .build();
        
        WeatherSDK sdk = new WeatherSDK(config);
        
        try {
            // Warm up cache with frequently accessed cities
            List<String> popularCities = List.of(
                "London", "Paris", "New York", "Tokyo", "Sydney",
                "Berlin", "Moscow", "Dubai", "Singapore", "Rome"
            );
            
            logger.info("Warming up cache with {} cities...", popularCities.size());
            long startTime = System.currentTimeMillis();
            
            for (String city : popularCities) {
                try {
                    sdk.getWeather(city);
                    logger.debug("Cached: {}", city);
                } catch (Exception e) {
                    logger.warn("Failed to cache {}: {}", city, e.getMessage());
                }
            }
            
            long warmupTime = System.currentTimeMillis() - startTime;
            logger.info("Cache warmup completed in {}ms", warmupTime);
            
            // Now requests are served from cache (much faster)
            logger.info("\nAccessing cached data...");
            startTime = System.currentTimeMillis();
            
            WeatherData london = sdk.getWeather("London");
            WeatherData paris = sdk.getWeather("Paris");
            WeatherData tokyo = sdk.getWeather("Tokyo");
            
            long cachedAccessTime = System.currentTimeMillis() - startTime;
            logger.info("Retrieved 3 cities from cache in {}ms", cachedAccessTime);
            
            // Check cache statistics
            CacheInfo cacheInfo = sdk.getCacheInfo();
            logger.info("\nCache Statistics:");
            logger.info("Cached cities: {}", cacheInfo.cachedCities());
            logger.info("Size: {}/{}", cacheInfo.currentSize(), cacheInfo.maxSize());
            logger.info("Utilization: {}%", String.format("%.2f", cacheInfo.getUtilization()));
            
        } finally {
            sdk.shutdown();
        }
    }
    
    /**
     * Example demonstrating high-throughput concurrent requests.
     */
    public static void concurrentRequestsExample() throws InterruptedException, ExecutionException {
        logger.info("\n=== Concurrent Requests Example ===");
        
        SDKConfig config = SDKConfig.builder("your-api-key-here")
            .operationMode(OperationMode.ON_DEMAND)
            .cacheMaxSize(100)
            .cacheTtlMinutes(15)
            .build();
        
        WeatherSDK sdk = new WeatherSDK(config);
        
        try {
            List<String> cities = List.of(
                "London", "Paris", "Berlin", "Madrid", "Rome",
                "Amsterdam", "Vienna", "Prague", "Warsaw", "Budapest"
            );
            
            // Use thread pool for concurrent requests
            ExecutorService executor = Executors.newFixedThreadPool(5);
            List<Future<WeatherData>> futures = new ArrayList<>();
            
            logger.info("Fetching weather for {} cities concurrently...", cities.size());
            long startTime = System.currentTimeMillis();
            
            for (String city : cities) {
                Future<WeatherData> future = executor.submit(() -> {
                    logger.debug("Fetching weather for {}...", city);
                    return sdk.getWeather(city);
                });
                futures.add(future);
            }
            
            // Collect results
            List<WeatherData> results = new ArrayList<>();
            for (Future<WeatherData> future : futures) {
                try {
                    WeatherData weather = future.get(10, TimeUnit.SECONDS);
                    results.add(weather);
                } catch (TimeoutException e) {
                    logger.warn("Request timed out");
                } catch (Exception e) {
                    logger.error("Request failed: {}", e.getMessage());
                }
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            
            logger.info("\nResults:");
            logger.info("Successfully fetched: {}/{} cities", results.size(), cities.size());
            logger.info("Total time: {}ms", totalTime);
            logger.info("Average time per city: {}ms", totalTime / cities.size());
            
            // Display sample results
            results.stream().limit(3).forEach(weather ->
                logger.info("{}: {}°C", weather.name(), weather.temperature().temp())
            );
            
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            
        } finally {
            sdk.shutdown();
        }
    }
    
    /**
     * Example demonstrating hybrid polling + on-demand strategy.
     */
    public static void hybridStrategyExample() throws InterruptedException {
        logger.info("\n=== Hybrid Strategy Example ===");
        
        // Polling mode for frequently accessed cities
        SDKConfig pollingConfig = SDKConfig.builder("your-api-key-polling")
            .operationMode(OperationMode.POLLING)
            .pollingIntervalMinutes(5)
            .cacheMaxSize(50)
            .build();
        
        WeatherSDK pollingSdk = WeatherSDKFactory.getInstance(pollingConfig);
        
        // Register most popular cities for automatic updates
        List<String> popularCities = List.of("London", "Paris", "New York", "Tokyo");
        popularCities.forEach(pollingSdk::registerLocation);
        logger.info("Registered {} cities for polling", popularCities.size());
        
        // On-demand mode for less frequent requests
        SDKConfig onDemandConfig = SDKConfig.builder("your-api-key-ondemand")
            .operationMode(OperationMode.ON_DEMAND)
            .cacheMaxSize(100)
            .cacheTtlMinutes(30)
            .build();
        
        WeatherSDK onDemandSdk = WeatherSDKFactory.getInstance(onDemandConfig);
        
        try {
            // Wait for polling to fetch initial data
            Thread.sleep(2000);
            
            // Fast access to polled cities (zero latency)
            logger.info("\nAccessing polled data (zero latency):");
            long start = System.currentTimeMillis();
            WeatherData london = pollingSdk.getWeather("London");
            logger.info("London: {}°C (fetched in {}ms)", 
                london.temperature().temp(), 
                System.currentTimeMillis() - start);
            
            // On-demand access for other cities
            logger.info("\nOn-demand request:");
            start = System.currentTimeMillis();
            WeatherData sydney = onDemandSdk.getWeather("Sydney");
            logger.info("Sydney: {}°C (fetched in {}ms)", 
                sydney.temperature().temp(), 
                System.currentTimeMillis() - start);
            
        } finally {
            WeatherSDKFactory.shutdownAll();
        }
    }
    
    /**
     * Example showing production-ready monitoring and metrics.
     */
    public static void monitoringExample() {
        logger.info("\n=== Monitoring Example ===");
        
        SDKConfig config = SDKConfig.builder("your-api-key-here")
            .operationMode(OperationMode.ON_DEMAND)
            .cacheMaxSize(100)
            .cacheTtlMinutes(10)
            .build();
        
        WeatherSDK sdk = new WeatherSDK(config);
        
        try {
            // Simulate some traffic
            List<String> cities = List.of(
                "London", "Paris", "London", "Berlin", 
                "London", "Paris", "Madrid", "London"
            );
            
            logger.info("Simulating {} requests...", cities.size());
            
            for (String city : cities) {
                try {
                    sdk.getWeather(city);
                } catch (Exception e) {
                    logger.warn("Request failed for {}: {}", city, e.getMessage());
                }
            }
            
            // Collect metrics
            CacheInfo cacheInfo = sdk.getCacheInfo();
            
            logger.info("\n=== Metrics Report ===");
            logger.info("Total requests: {}", cities.size());
            logger.info("Cached cities: {}", cacheInfo.cachedCities());
            logger.info("Cache size: {}/{}", cacheInfo.currentSize(), cacheInfo.maxSize());
            logger.info("Cache utilization: {}%", String.format("%.2f", cacheInfo.getUtilization()));
            logger.info("Cache is {}full", cacheInfo.isFull() ? "" : "not ");
            
        } finally {
            sdk.shutdown();
        }
    }
    
    /**
     * Example demonstrating time zone aware weather display.
     */
    public static void timezoneAwareExample() {
        logger.info("\n=== Timezone Aware Example ===");
        
        SDKConfig config = SDKConfig.builder("your-api-key-here")
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        
        WeatherSDK sdk = new WeatherSDK(config);
        
        try {
            List<String> cities = List.of("London", "Tokyo", "New York", "Sydney");
            
            for (String city : cities) {
                try {
                    WeatherData weather = sdk.getWeather(city);
                    displayWeatherWithTimezone(weather);
                } catch (Exception e) {
                    logger.error("Failed to fetch weather for {}: {}", city, e.getMessage());
                }
            }
            
        } finally {
            sdk.shutdown();
        }
    }
    
    /**
     * Displays weather data with local timezone information.
     */
    private static void displayWeatherWithTimezone(WeatherData weather) {
        // Calculate local time based on timezone offset
        long localTime = weather.datetime() + weather.timezone();
        Instant instant = Instant.ofEpochSecond(localTime);
        
        // Calculate sunrise/sunset local times
        Instant sunrise = Instant.ofEpochSecond(weather.sys().sunrise() + weather.timezone());
        Instant sunset = Instant.ofEpochSecond(weather.sys().sunset() + weather.timezone());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(ZoneId.of("UTC"));
        
        logger.info("\n{} ({} UTC):",
            weather.name(),
            formatter.format(instant)
        );
        logger.info("  Temperature: {}°C (feels like {}°C)",
            weather.temperature().temp(),
            weather.temperature().feelsLike()
        );
        logger.info("  Conditions: {}",
            weather.weather().description()
        );
        logger.info("  Sunrise: {} | Sunset: {}",
            formatter.format(sunrise),
            formatter.format(sunset)
        );
    }
    
    /**
     * Main method demonstrating all advanced examples.
     * Note: Replace "your-api-key-here" with actual API key.
     */
    public static void main(String[] args) {
        try {
            // Example 1: Caching strategy
            cachingStrategyExample();
            
            // Example 2: Concurrent requests
            concurrentRequestsExample();
            
            // Example 3: Hybrid strategy (polling + on-demand)
            //hybridStrategyExample();
            
            // Example 4: Monitoring
            monitoringExample();
            
            // Example 5: Timezone aware display
            timezoneAwareExample();
            
        } catch (Exception e) {
            logger.error("Example failed: {}", e.getMessage(), e);
        }
    }
}

