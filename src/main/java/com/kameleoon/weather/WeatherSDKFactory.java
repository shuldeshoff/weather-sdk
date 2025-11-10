package com.kameleoon.weather;

import com.kameleoon.weather.config.SDKConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for managing multiple WeatherSDK instances (Multiton pattern).
 * Ensures that each unique API key has at most one SDK instance.
 * Thread-safe implementation using ConcurrentHashMap.
 *
 * <p>Usage example:
 * <pre>{@code
 * SDKConfig config = SDKConfig.builder("api-key-1").build();
 * WeatherSDK sdk1 = WeatherSDKFactory.getInstance(config);
 * WeatherSDK sdk2 = WeatherSDKFactory.getInstance(config); // Returns same instance
 * 
 * WeatherSDKFactory.shutdownInstance("api-key-1");
 * WeatherSDKFactory.shutdownAll();
 * }</pre>
 *
 * @author Yury Shuldeshov
 */
public final class WeatherSDKFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherSDKFactory.class);
    
    private static final Map<String, WeatherSDK> instances = new ConcurrentHashMap<>();
    private static final Object lock = new Object();
    
    /**
     * Private constructor to prevent instantiation.
     */
    private WeatherSDKFactory() {
        throw new AssertionError("Cannot instantiate factory class");
    }
    
    /**
     * Gets or creates a WeatherSDK instance for the specified configuration.
     * If an instance already exists for the same API key, returns that instance.
     * The configuration of the first instance is used for subsequent calls with the same API key.
     *
     * @param config SDK configuration
     * @return WeatherSDK instance
     * @throws IllegalArgumentException if config is null
     */
    public static WeatherSDK getInstance(SDKConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        
        String apiKey = config.apiKey();
        
        return instances.computeIfAbsent(apiKey, key -> {
            synchronized (lock) {
                // Double-check pattern
                if (instances.containsKey(key)) {
                    logger.debug("SDK instance already exists for API key: {}", maskApiKey(key));
                    return instances.get(key);
                }
                
                // Create new instance
                logger.info("Creating new SDK instance for API key: {}", maskApiKey(key));
                WeatherSDK sdk = new WeatherSDK(config);
                logger.info("Successfully created SDK instance for API key: {}", maskApiKey(key));
                return sdk;
            }
        });
    }
    
    /**
     * Gets an existing SDK instance by API key.
     *
     * @param apiKey The API key
     * @return WeatherSDK instance or null if not found
     */
    public static WeatherSDK getInstance(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key cannot be null or blank");
        }
        
        WeatherSDK sdk = instances.get(apiKey);
        if (sdk == null) {
            logger.debug("No SDK instance found for API key: {}", maskApiKey(apiKey));
        }
        return sdk;
    }
    
    /**
     * Checks if an SDK instance exists for the specified API key.
     *
     * @param apiKey The API key
     * @return true if instance exists, false otherwise
     */
    public static boolean hasInstance(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return false;
        }
        return instances.containsKey(apiKey);
    }
    
    /**
     * Gets all active API keys.
     *
     * @return Immutable set of API keys (masked for security)
     */
    public static Set<String> getActiveKeys() {
        return Set.copyOf(instances.keySet().stream()
            .map(WeatherSDKFactory::maskApiKey)
            .toList());
    }
    
    /**
     * Gets the number of active SDK instances.
     *
     * @return Number of instances
     */
    public static int getInstanceCount() {
        return instances.size();
    }
    
    /**
     * Shuts down and removes a specific SDK instance.
     *
     * @param apiKey The API key of the instance to shutdown
     * @return true if instance was removed, false if not found
     */
    public static boolean shutdownInstance(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key cannot be null or blank");
        }
        
        synchronized (lock) {
            WeatherSDK sdk = instances.remove(apiKey);
            if (sdk != null) {
                logger.info("Shutting down SDK instance for API key: {}", maskApiKey(apiKey));
                try {
                    sdk.shutdown();
                    logger.info("Successfully shut down SDK instance for API key: {}", maskApiKey(apiKey));
                    return true;
                } catch (Exception e) {
                    logger.error("Error shutting down SDK instance for API key: {}", maskApiKey(apiKey), e);
                    // Re-add to map if shutdown failed
                    instances.put(apiKey, sdk);
                    throw new RuntimeException("Failed to shutdown SDK instance", e);
                }
            } else {
                logger.debug("No SDK instance found to shutdown for API key: {}", maskApiKey(apiKey));
                return false;
            }
        }
    }
    
    /**
     * Shuts down all SDK instances and clears the registry.
     * This should be called when the application is shutting down.
     */
    public static void shutdownAll() {
        synchronized (lock) {
            logger.info("Shutting down all {} SDK instances", instances.size());
            
            int successCount = 0;
            int failCount = 0;
            
            for (Map.Entry<String, WeatherSDK> entry : instances.entrySet()) {
                String apiKey = entry.getKey();
                WeatherSDK sdk = entry.getValue();
                
                try {
                    logger.debug("Shutting down SDK instance for API key: {}", maskApiKey(apiKey));
                    sdk.shutdown();
                    successCount++;
                } catch (Exception e) {
                    logger.error("Error shutting down SDK instance for API key: {}", maskApiKey(apiKey), e);
                    failCount++;
                }
            }
            
            instances.clear();
            
            if (failCount > 0) {
                logger.warn("Completed shutdown: {} successful, {} failed", successCount, failCount);
            } else {
                logger.info("Successfully shut down all {} SDK instances", successCount);
            }
        }
    }
    
    /**
     * Clears all instances without shutdown (for testing purposes only).
     * <b>WARNING:</b> This may leave resources uncleaned. Use {@link #shutdownAll()} instead.
     */
    static void clearInstances() {
        synchronized (lock) {
            logger.warn("Clearing all SDK instances without shutdown (testing only)");
            instances.clear();
        }
    }
    
    /**
     * Masks an API key for logging (shows first 4 and last 4 characters).
     *
     * @param apiKey The API key to mask
     * @return Masked API key
     */
    private static String maskApiKey(String apiKey) {
        if (apiKey == null) {
            return "null";
        }
        if (apiKey.length() <= 8) {
            return "****";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}

