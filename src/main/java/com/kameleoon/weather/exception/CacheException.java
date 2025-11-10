package com.kameleoon.weather.exception;

/**
 * Exception thrown when there's an error with cache operations.
 *
 * @author Yury Shuldeshov
 */
public class CacheException extends WeatherSDKException {
    
    /**
     * Constructs a new CacheException with the specified detail message.
     *
     * @param message The detail message
     */
    public CacheException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new CacheException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}

