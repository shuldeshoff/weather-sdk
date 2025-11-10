package com.kameleoon.weather.config;

/**
 * Configuration for the Weather SDK.
 * Immutable configuration object using Java 17 Record.
 *
 * @param apiKey OpenWeatherMap API key
 * @param operationMode Operation mode (ON_DEMAND or POLLING)
 * @param cacheMaxSize Maximum number of cities to cache
 * @param cacheTtlMinutes Cache TTL in minutes
 * @param pollingIntervalMinutes Polling interval for POLLING mode (in minutes)
 * @param maxRetries Maximum number of retry attempts for API calls
 * @author Yury Shuldeshov
 */
public record SDKConfig(
        String apiKey,
        OperationMode operationMode,
        int cacheMaxSize,
        long cacheTtlMinutes,
        long pollingIntervalMinutes,
        int maxRetries
) {
    /**
     * Default configuration values.
     */
    public static final int DEFAULT_CACHE_MAX_SIZE = 100;
    public static final long DEFAULT_CACHE_TTL_MINUTES = 10;
    public static final long DEFAULT_POLLING_INTERVAL_MINUTES = 5;
    public static final int DEFAULT_MAX_RETRIES = 3;
    public static final OperationMode DEFAULT_OPERATION_MODE = OperationMode.ON_DEMAND;
    
    /**
     * Compact constructor with validation.
     */
    public SDKConfig {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key cannot be null or blank");
        }
        if (operationMode == null) {
            throw new IllegalArgumentException("Operation mode cannot be null");
        }
        if (cacheMaxSize <= 0) {
            throw new IllegalArgumentException("Cache max size must be positive");
        }
        if (cacheTtlMinutes <= 0) {
            throw new IllegalArgumentException("Cache TTL must be positive");
        }
        if (pollingIntervalMinutes <= 0) {
            throw new IllegalArgumentException("Polling interval must be positive");
        }
        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries cannot be negative");
        }
    }
    
    /**
     * Creates a new builder for SDKConfig.
     *
     * @param apiKey OpenWeatherMap API key (required)
     * @return A new builder instance
     */
    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }
    
    /**
     * Builder for SDKConfig.
     */
    public static class Builder {
        private final String apiKey;
        private OperationMode operationMode = DEFAULT_OPERATION_MODE;
        private int cacheMaxSize = DEFAULT_CACHE_MAX_SIZE;
        private long cacheTtlMinutes = DEFAULT_CACHE_TTL_MINUTES;
        private long pollingIntervalMinutes = DEFAULT_POLLING_INTERVAL_MINUTES;
        private int maxRetries = DEFAULT_MAX_RETRIES;
        
        /**
         * Creates a new builder with the required API key.
         *
         * @param apiKey OpenWeatherMap API key
         */
        private Builder(String apiKey) {
            this.apiKey = apiKey;
        }
        
        /**
         * Sets the operation mode.
         *
         * @param operationMode Operation mode
         * @return This builder
         */
        public Builder operationMode(OperationMode operationMode) {
            this.operationMode = operationMode;
            return this;
        }
        
        /**
         * Sets the maximum cache size.
         *
         * @param cacheMaxSize Maximum number of cities to cache
         * @return This builder
         */
        public Builder cacheMaxSize(int cacheMaxSize) {
            this.cacheMaxSize = cacheMaxSize;
            return this;
        }
        
        /**
         * Sets the cache TTL.
         *
         * @param cacheTtlMinutes Cache TTL in minutes
         * @return This builder
         */
        public Builder cacheTtlMinutes(long cacheTtlMinutes) {
            this.cacheTtlMinutes = cacheTtlMinutes;
            return this;
        }
        
        /**
         * Sets the polling interval.
         *
         * @param pollingIntervalMinutes Polling interval in minutes
         * @return This builder
         */
        public Builder pollingIntervalMinutes(long pollingIntervalMinutes) {
            this.pollingIntervalMinutes = pollingIntervalMinutes;
            return this;
        }
        
        /**
         * Sets the maximum number of retry attempts.
         *
         * @param maxRetries Maximum retries
         * @return This builder
         */
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }
        
        /**
         * Builds the SDKConfig.
         *
         * @return A new SDKConfig instance
         */
        public SDKConfig build() {
            return new SDKConfig(
                apiKey,
                operationMode,
                cacheMaxSize,
                cacheTtlMinutes,
                pollingIntervalMinutes,
                maxRetries
            );
        }
    }
}

