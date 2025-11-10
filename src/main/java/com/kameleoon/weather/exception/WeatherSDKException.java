package com.kameleoon.weather.exception;

/**
 * Base exception for all Weather SDK errors.
 * All SDK-specific exceptions extend this class.
 *
 * @author Yury Shuldeshov
 */
public class WeatherSDKException extends RuntimeException {
    
    /**
     * Constructs a new WeatherSDKException with the specified detail message.
     *
     * @param message The detail message
     */
    public WeatherSDKException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new WeatherSDKException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public WeatherSDKException(String message, Throwable cause) {
        super(message, cause);
    }
}

