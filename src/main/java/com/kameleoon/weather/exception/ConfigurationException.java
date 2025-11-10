package com.kameleoon.weather.exception;

/**
 * Exception thrown when there's an error with SDK configuration.
 * This includes invalid configuration parameters, missing required settings, etc.
 *
 * @author Yury Shuldeshov
 */
public class ConfigurationException extends WeatherSDKException {
    
    /**
     * Constructs a new ConfigurationException with the specified detail message.
     *
     * @param message The detail message
     */
    public ConfigurationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ConfigurationException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

