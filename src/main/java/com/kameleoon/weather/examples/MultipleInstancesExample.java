package com.kameleoon.weather.examples;

import com.kameleoon.weather.WeatherSDK;
import com.kameleoon.weather.WeatherSDKFactory;
import com.kameleoon.weather.config.OperationMode;
import com.kameleoon.weather.config.SDKConfig;
import com.kameleoon.weather.model.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example demonstrating the use of WeatherSDKFactory to manage multiple SDK instances.
 * Shows the Multiton pattern in action with different API keys.
 *
 * @author Yury Shuldeshov
 */
public class MultipleInstancesExample {
    
    private static final Logger logger = LoggerFactory.getLogger(MultipleInstancesExample.class);
    
    /**
     * Example using factory to manage multiple SDK instances.
     */
    public static void multipleInstancesExample() {
        logger.info("=== Multiple Instances Example ===");
        
        try {
            // Create configuration for first API key (Free tier)
            SDKConfig config1 = SDKConfig.builder("api-key-free-tier")
                .operationMode(OperationMode.ON_DEMAND)
                .cacheMaxSize(50)
                .cacheTtlMinutes(15)
                .maxRetries(2)
                .build();
            
            // Create configuration for second API key (Pro tier)
            SDKConfig config2 = SDKConfig.builder("api-key-pro-tier")
                .operationMode(OperationMode.POLLING)
                .cacheMaxSize(200)
                .cacheTtlMinutes(5)
                .pollingIntervalMinutes(3)
                .maxRetries(5)
                .build();
            
            // Get SDK instances via factory
            WeatherSDK sdk1 = WeatherSDKFactory.getInstance(config1);
            WeatherSDK sdk2 = WeatherSDKFactory.getInstance(config2);
            
            logger.info("Created {} SDK instances", WeatherSDKFactory.getInstanceCount());
            
            // Use first SDK (Free tier)
            logger.info("Using Free tier SDK:");
            WeatherData weather1 = sdk1.getWeather("London");
            logger.info("Temperature in {}: {}°C", weather1.name(), weather1.temperature().temp());
            
            // Use second SDK (Pro tier with polling)
            logger.info("Using Pro tier SDK:");
            sdk2.registerLocation("Paris");
            sdk2.registerLocation("Berlin");
            sdk2.registerLocation("Madrid");
            
            Thread.sleep(2000); // Wait for initial polling
            
            WeatherData weather2 = sdk2.getWeather("Paris");
            logger.info("Temperature in {}: {}°C", weather2.name(), weather2.temperature().temp());
            
            // Get same instance by API key
            WeatherSDK sdk1Again = WeatherSDKFactory.getInstance("api-key-free-tier");
            logger.info("Retrieved same instance: {}", sdk1 == sdk1Again);
            
            // Check active instances
            logger.info("Active SDK instances: {}", WeatherSDKFactory.getActiveKeys());
            
            // Shutdown specific instance
            logger.info("Shutting down Free tier SDK");
            WeatherSDKFactory.shutdownInstance("api-key-free-tier");
            logger.info("Remaining instances: {}", WeatherSDKFactory.getInstanceCount());
            
        } catch (Exception e) {
            logger.error("Error in multiple instances example: {}", e.getMessage(), e);
        } finally {
            // Shutdown all remaining instances
            WeatherSDKFactory.shutdownAll();
            logger.info("All SDK instances shut down");
        }
    }
    
    /**
     * Example showing thread-safe instance creation.
     */
    public static void concurrentAccessExample() throws InterruptedException {
        logger.info("=== Concurrent Access Example ===");
        
        SDKConfig config = SDKConfig.builder("api-key-shared")
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        
        // Create multiple threads trying to get the same instance
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                WeatherSDK sdk = WeatherSDKFactory.getInstance(config);
                logger.info("Thread {} got SDK instance: {}", threadId, System.identityHashCode(sdk));
                
                try {
                    WeatherData weather = sdk.getWeather("Tokyo");
                    logger.info("Thread {} got temperature: {}°C", threadId, weather.temperature().temp());
                } catch (Exception e) {
                    logger.error("Thread {} error: {}", threadId, e.getMessage());
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
        
        logger.info("All threads completed. Total instances: {}", WeatherSDKFactory.getInstanceCount());
        
        // Cleanup
        WeatherSDKFactory.shutdownAll();
    }
    
    /**
     * Main method demonstrating both examples.
     * Note: Replace "api-key-xxx" with actual API keys.
     */
    public static void main(String[] args) {
        try {
            // Example 1: Multiple instances with different configurations
            multipleInstancesExample();
            
            logger.info("\n");
            
            // Example 2: Concurrent access to shared instance
            concurrentAccessExample();
            
        } catch (Exception e) {
            logger.error("Example failed: {}", e.getMessage(), e);
        }
    }
}

