package com.kameleoon.weather.exception;

/**
 * Exception thrown when input validation fails.
 * This includes invalid city names, null parameters, etc.
 *
 * @author Yury Shuldeshov
 */
public class ValidationException extends WeatherSDKException {
    
    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message The detail message
     */
    public ValidationException(String message) {
        super(message);
    }
}

