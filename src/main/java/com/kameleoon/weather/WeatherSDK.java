package com.kameleoon.weather;

import com.kameleoon.weather.client.OpenWeatherMapClient;
import com.kameleoon.weather.config.OperationMode;
import com.kameleoon.weather.config.SDKConfig;
import com.kameleoon.weather.model.CacheInfo;
import com.kameleoon.weather.model.WeatherData;
import com.kameleoon.weather.service.CacheService;
import com.kameleoon.weather.service.LocationRegistry;
import com.kameleoon.weather.service.PollingService;
import com.kameleoon.weather.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main facade for the Weather SDK.
 * Provides a simple and unified interface for accessing weather data.
 *
 * <p>Usage example (ON_DEMAND mode):
 * <pre>{@code
 * SDKConfig config = SDKConfig.builder("your-api-key")
 *     .operationMode(OperationMode.ON_DEMAND)
 *     .build();
 * 
 * WeatherSDK sdk = new WeatherSDK(config);
 * WeatherData weather = sdk.getWeather("London");
 * }</pre>
 *
 * <p>Usage example (POLLING mode):
 * <pre>{@code
 * SDKConfig config = SDKConfig.builder("your-api-key")
 *     .operationMode(OperationMode.POLLING)
 *     .pollingIntervalMinutes(5)
 *     .build();
 * 
 * WeatherSDK sdk = new WeatherSDK(config);
 * sdk.registerLocation("London");
 * sdk.registerLocation("Paris");
 * 
 * // Weather data will be automatically updated every 5 minutes
 * WeatherData weather = sdk.getWeather("London");
 * }</pre>
 *
 * @author Yury Shuldeshov
 */
public class WeatherSDK {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherSDK.class);
    
    private final SDKConfig config;
    private final WeatherService weatherService;
    private final CacheService cacheService;
    private final LocationRegistry locationRegistry;
    private final PollingService pollingService;
    private final boolean isPollingMode;
    
    /**
     * Creates a new WeatherSDK instance with the specified configuration.
     *
     * @param config SDK configuration
     * @throws IllegalArgumentException if config is null
     */
    public WeatherSDK(SDKConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        
        this.config = config;
        this.isPollingMode = config.operationMode() == OperationMode.POLLING;
        
        logger.info("Initializing Weather SDK in {} mode", config.operationMode());
        
        // Initialize components
        OpenWeatherMapClient apiClient = new OpenWeatherMapClient(
            config.apiKey(),
            config.maxRetries()
        );
        
        this.cacheService = new CacheService(
            config.cacheMaxSize(),
            config.cacheTtlMinutes()
        );
        
        this.weatherService = new WeatherService(apiClient, cacheService);
        
        // Initialize polling components if needed
        if (isPollingMode) {
            this.locationRegistry = new LocationRegistry();
            this.pollingService = new PollingService(
                weatherService,
                locationRegistry,
                config.pollingIntervalMinutes()
            );
            this.pollingService.start();
            
            logger.info("Weather SDK initialized in POLLING mode with interval {} minutes",
                config.pollingIntervalMinutes());
        } else {
            this.locationRegistry = null;
            this.pollingService = null;
            
            logger.info("Weather SDK initialized in ON_DEMAND mode");
        }
    }
    
    /**
     * Gets weather data for the specified city.
     * In POLLING mode, returns cached data (updated automatically).
     * In ON_DEMAND mode, fetches from cache or API as needed.
     *
     * @param cityName Name of the city
     * @return Weather data for the city
     * @throws IllegalArgumentException if city name is invalid
     */
    public WeatherData getWeather(String cityName) {
        logger.debug("Getting weather for city: {}", cityName);
        return weatherService.getWeather(cityName);
    }
    
    /**
     * Registers a location for automatic polling (POLLING mode only).
     * The location will be automatically updated at the configured interval.
     *
     * @param cityName Name of the city to register
     * @return true if registered successfully, false if already registered
     * @throws UnsupportedOperationException if not in POLLING mode
     */
    public boolean registerLocation(String cityName) {
        if (!isPollingMode) {
            throw new UnsupportedOperationException(
                "Location registration is only available in POLLING mode");
        }
        
        boolean registered = locationRegistry.register(cityName);
        if (registered) {
            logger.info("Registered location for polling: {}", cityName);
            // Immediately fetch data for the new location
            try {
                weatherService.fetchFreshWeather(cityName);
            } catch (Exception e) {
                logger.error("Failed to fetch initial data for location: {}", cityName, e);
            }
        }
        return registered;
    }
    
    /**
     * Unregisters a location from automatic polling (POLLING mode only).
     *
     * @param cityName Name of the city to unregister
     * @return true if unregistered successfully, false if not registered
     * @throws UnsupportedOperationException if not in POLLING mode
     */
    public boolean unregisterLocation(String cityName) {
        if (!isPollingMode) {
            throw new UnsupportedOperationException(
                "Location unregistration is only available in POLLING mode");
        }
        
        boolean unregistered = locationRegistry.unregister(cityName);
        if (unregistered) {
            logger.info("Unregistered location from polling: {}", cityName);
        }
        return unregistered;
    }
    
    /**
     * Gets all registered locations (POLLING mode only).
     *
     * @return Set of registered city names
     * @throws UnsupportedOperationException if not in POLLING mode
     */
    public java.util.Set<String> getRegisteredLocations() {
        if (!isPollingMode) {
            throw new UnsupportedOperationException(
                "Location registry is only available in POLLING mode");
        }
        return locationRegistry.getAllLocations();
    }
    
    /**
     * Triggers an immediate update of all registered locations (POLLING mode only).
     * Does not affect the scheduled polling interval.
     *
     * @throws UnsupportedOperationException if not in POLLING mode
     */
    public void refreshAll() {
        if (!isPollingMode) {
            throw new UnsupportedOperationException(
                "Manual refresh is only available in POLLING mode");
        }
        
        logger.info("Triggering manual refresh of all locations");
        pollingService.pollNow();
    }
    
    /**
     * Gets information about the cache state.
     *
     * @return Cache information
     */
    public CacheInfo getCacheInfo() {
        return new CacheInfo(
            cacheService.getCachedCities(),
            cacheService.getSize(),
            cacheService.getMaxSize()
        );
    }
    
    /**
     * Clears the cache.
     * In POLLING mode, the cache will be repopulated on the next polling cycle.
     */
    public void clearCache() {
        logger.info("Clearing cache");
        cacheService.clear();
    }
    
    /**
     * Gets the SDK configuration.
     *
     * @return SDK configuration
     */
    public SDKConfig getConfig() {
        return config;
    }
    
    /**
     * Shuts down the SDK and releases all resources.
     * In POLLING mode, stops the polling service.
     * Should be called when the SDK is no longer needed.
     */
    public void shutdown() {
        logger.info("Shutting down Weather SDK");
        
        if (isPollingMode && pollingService != null) {
            pollingService.stop();
        }
        
        logger.info("Weather SDK shutdown complete");
    }
}

