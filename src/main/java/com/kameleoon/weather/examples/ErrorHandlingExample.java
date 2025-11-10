package com.kameleoon.weather.examples;

import com.kameleoon.weather.WeatherSDK;
import com.kameleoon.weather.config.OperationMode;
import com.kameleoon.weather.config.SDKConfig;
import com.kameleoon.weather.exception.*;
import com.kameleoon.weather.model.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example demonstrating proper error handling in Weather SDK.
 * Shows how to handle different types of exceptions.
 *
 * @author Yury Shuldeshov
 */
public class ErrorHandlingExample {
    
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingExample.class);
    
    /**
     * Example showing how to handle all types of SDK exceptions.
     */
    public static void comprehensiveErrorHandling() {
        logger.info("=== Comprehensive Error Handling Example ===");
        
        SDKConfig config = SDKConfig.builder("your-api-key-here")
            .operationMode(OperationMode.ON_DEMAND)
            .maxRetries(3)
            .build();
        
        WeatherSDK sdk = new WeatherSDK(config);
        
        try {
            // Example 1: Handling city not found
            logger.info("Example 1: City Not Found");
            try {
                WeatherData weather = sdk.getWeather("NonExistentCity12345");
                logger.info("Temperature: {}°C", weather.temperature().temp());
            } catch (CityNotFoundException e) {
                logger.error("City not found: {}", e.getMessage());
                logger.info("Suggestion: Check city name spelling or try alternative name");
            }
            
            // Example 2: Handling invalid API key
            logger.info("\nExample 2: Invalid API Key");
            try {
                SDKConfig invalidConfig = SDKConfig.builder("invalid-key").build();
                WeatherSDK invalidSdk = new WeatherSDK(invalidConfig);
                WeatherData weather = invalidSdk.getWeather("London");
                invalidSdk.shutdown();
            } catch (InvalidApiKeyException e) {
                logger.error("Invalid API key: {}", e.getMessage());
                logger.info("Suggestion: Check your API key at https://openweathermap.org/api");
            }
            
            // Example 3: Handling rate limit
            logger.info("\nExample 3: Rate Limit");
            try {
                // Simulate multiple rapid requests
                for (int i = 0; i < 100; i++) {
                    sdk.getWeather("London");
                }
            } catch (RateLimitException e) {
                logger.error("Rate limit exceeded: {}", e.getMessage());
                logger.info("Suggestion: Wait before retrying or upgrade your API plan");
            }
            
            // Example 4: Handling API unavailability
            logger.info("\nExample 4: API Unavailable");
            try {
                WeatherData weather = sdk.getWeather("London");
                logger.info("Temperature: {}°C", weather.temperature().temp());
            } catch (ApiUnavailableException e) {
                logger.error("API unavailable: {}", e.getMessage());
                logger.info("Suggestion: The service will retry automatically. Check API status.");
            }
            
            // Example 5: Handling validation errors
            logger.info("\nExample 5: Validation Error");
            try {
                WeatherData weather = sdk.getWeather(null);
            } catch (ValidationException e) {
                logger.error("Validation error: {}", e.getMessage());
                logger.info("Suggestion: Ensure all required parameters are provided");
            }
            
            // Example 6: Generic error handling
            logger.info("\nExample 6: Generic Error Handling");
            try {
                WeatherData weather = sdk.getWeather("London");
                logger.info("Weather fetched successfully");
            } catch (WeatherSDKException e) {
                // This catches all SDK-specific exceptions
                logger.error("SDK error occurred: {} (Type: {})", 
                    e.getMessage(), e.getClass().getSimpleName());
                handleError(e);
            }
            
        } finally {
            sdk.shutdown();
        }
    }
    
    /**
     * Example with retry logic for transient errors.
     */
    public static void retryLogicExample() {
        logger.info("\n=== Retry Logic Example ===");
        
        SDKConfig config = SDKConfig.builder("your-api-key-here")
            .operationMode(OperationMode.ON_DEMAND)
            .maxRetries(3)
            .build();
        
        WeatherSDK sdk = new WeatherSDK(config);
        
        try {
            WeatherData weather = fetchWeatherWithRetry(sdk, "London", 3, 2000);
            logger.info("Successfully fetched weather: {}°C", weather.temperature().temp());
        } finally {
            sdk.shutdown();
        }
    }
    
    /**
     * Fetches weather with custom retry logic.
     *
     * @param sdk SDK instance
     * @param cityName City name
     * @param maxRetries Maximum retry attempts
     * @param delayMs Delay between retries in milliseconds
     * @return Weather data
     */
    private static WeatherData fetchWeatherWithRetry(
            WeatherSDK sdk, 
            String cityName, 
            int maxRetries, 
            long delayMs) {
        
        int attempt = 0;
        Exception lastException = null;
        
        while (attempt < maxRetries) {
            try {
                logger.info("Attempt {} to fetch weather for {}", attempt + 1, cityName);
                return sdk.getWeather(cityName);
                
            } catch (ApiUnavailableException e) {
                // Retry for transient errors
                lastException = e;
                attempt++;
                
                if (attempt < maxRetries) {
                    logger.warn("Attempt {} failed: {}. Retrying in {}ms...", 
                        attempt, e.getMessage(), delayMs);
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            } catch (RateLimitException e) {
                // Retry with longer delay for rate limit
                lastException = e;
                attempt++;
                
                if (attempt < maxRetries) {
                    long rateLimitDelay = delayMs * 3; // Longer delay
                    logger.warn("Rate limit hit. Waiting {}ms before retry...", rateLimitDelay);
                    try {
                        Thread.sleep(rateLimitDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            } catch (CityNotFoundException | InvalidApiKeyException | ValidationException e) {
                // Don't retry for non-transient errors
                logger.error("Non-retryable error: {}", e.getMessage());
                throw e;
            }
        }
        
        logger.error("All {} retry attempts failed", maxRetries);
        throw new RuntimeException("Failed after " + maxRetries + " attempts", lastException);
    }
    
    /**
     * Handles different error types with appropriate actions.
     *
     * @param exception The exception to handle
     */
    private static void handleError(WeatherSDKException exception) {
        if (exception instanceof CityNotFoundException) {
            logger.info("Action: Verify city name and try alternative spellings");
        } else if (exception instanceof InvalidApiKeyException) {
            logger.info("Action: Update API key in configuration");
        } else if (exception instanceof RateLimitException) {
            logger.info("Action: Implement rate limiting or upgrade API plan");
        } else if (exception instanceof ApiUnavailableException) {
            logger.info("Action: Check API status and retry later");
        } else if (exception instanceof ValidationException) {
            logger.info("Action: Validate input parameters");
        } else {
            logger.info("Action: Review error message and check logs");
        }
    }
    
    /**
     * Example with graceful degradation.
     */
    public static void gracefulDegradationExample() {
        logger.info("\n=== Graceful Degradation Example ===");
        
        SDKConfig config = SDKConfig.builder("your-api-key-here")
            .operationMode(OperationMode.ON_DEMAND)
            .build();
        
        WeatherSDK sdk = new WeatherSDK(config);
        
        try {
            WeatherData weather = getWeatherWithFallback(sdk, "London");
            
            if (weather != null) {
                logger.info("Weather: {}°C, {}", 
                    weather.temperature().temp(), 
                    weather.weather().description());
            } else {
                logger.info("Using cached or default weather data");
            }
            
        } finally {
            sdk.shutdown();
        }
    }
    
    /**
     * Gets weather with fallback to cached data or default.
     *
     * @param sdk SDK instance
     * @param cityName City name
     * @return Weather data or null if unavailable
     */
    private static WeatherData getWeatherWithFallback(WeatherSDK sdk, String cityName) {
        try {
            return sdk.getWeather(cityName);
            
        } catch (CityNotFoundException e) {
            logger.warn("City not found: {}. Using default location.", cityName);
            try {
                return sdk.getWeather("London"); // Fallback city
            } catch (Exception fallbackError) {
                logger.error("Fallback also failed: {}", fallbackError.getMessage());
                return null;
            }
            
        } catch (ApiUnavailableException | RateLimitException e) {
            logger.warn("Service temporarily unavailable: {}", e.getMessage());
            // In production, you might return cached data here
            return null;
            
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Main method demonstrating all error handling examples.
     * Note: Replace "your-api-key-here" with actual API key.
     */
    public static void main(String[] args) {
        try {
            // Example 1: Comprehensive error handling
            comprehensiveErrorHandling();
            
            // Example 2: Retry logic
            retryLogicExample();
            
            // Example 3: Graceful degradation
            gracefulDegradationExample();
            
        } catch (Exception e) {
            logger.error("Example failed: {}", e.getMessage(), e);
        }
    }
}

