package com.kameleoon.weather.config;

/**
 * SDK operation mode.
 * Defines how the SDK retrieves and updates weather data.
 *
 * @author Yury Shuldeshov
 */
public enum OperationMode {
    
    /**
     * On-demand mode: weather data is fetched only when requested.
     * Suitable for infrequent requests and resource conservation.
     */
    ON_DEMAND,
    
    /**
     * Polling mode: weather data is automatically updated at regular intervals.
     * Provides zero-latency responses as data is always fresh in cache.
     * Suitable for applications requiring frequent, real-time weather updates.
     */
    POLLING
}

