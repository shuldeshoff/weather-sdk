package com.kameleoon.weather.exception;

/**
 * Exception thrown when the weather API is unavailable or experiencing issues.
 * This includes network errors, timeouts, and HTTP 5xx errors.
 *
 * @author Yury Shuldeshov
 */
public class ApiUnavailableException extends ApiException {
    
    /**
     * Constructs a new ApiUnavailableException with the specified detail message.
     *
     * @param message The detail message
     */
    public ApiUnavailableException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ApiUnavailableException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public ApiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new ApiUnavailableException with status code and API response.
     *
     * @param message The detail message
     * @param statusCode The HTTP status code
     * @param apiResponse The API error response body
     */
    public ApiUnavailableException(String message, int statusCode, String apiResponse) {
        super(message + " (HTTP " + statusCode + ") - API response: " + apiResponse);
    }
}

