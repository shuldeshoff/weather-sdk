package com.kameleoon.weather.exception;

/**
 * Exception thrown when there's an error communicating with the weather API.
 * Base class for all API-related errors.
 *
 * @author Yury Shuldeshov
 */
public class ApiException extends WeatherSDKException {
    
    /**
     * Constructs a new ApiException with the specified detail message.
     *
     * @param message The detail message
     */
    public ApiException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ApiException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

