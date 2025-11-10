package com.kameleoon.weather.exception;

/**
 * Exception thrown when the API rate limit is exceeded.
 * HTTP Status Code: 429
 *
 * @author Yury Shuldeshov
 */
public class RateLimitException extends ApiException {
    
    /**
     * Constructs a new RateLimitException with the specified detail message.
     *
     * @param message The detail message
     */
    public RateLimitException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new RateLimitException with the specified detail message and API response.
     *
     * @param message The detail message
     * @param apiResponse The API error response body
     */
    public RateLimitException(String message, String apiResponse) {
        super(message + " - API response: " + apiResponse);
    }
}

