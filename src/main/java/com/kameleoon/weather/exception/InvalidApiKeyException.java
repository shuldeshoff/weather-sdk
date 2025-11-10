package com.kameleoon.weather.exception;

/**
 * Exception thrown when the API key is invalid or unauthorized.
 * HTTP Status Code: 401
 *
 * @author Yury Shuldeshov
 */
public class InvalidApiKeyException extends ApiException {
    
    /**
     * Constructs a new InvalidApiKeyException with the specified detail message.
     *
     * @param message The detail message
     */
    public InvalidApiKeyException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new InvalidApiKeyException with the specified detail message and API response.
     *
     * @param message The detail message
     * @param apiResponse The API error response body
     */
    public InvalidApiKeyException(String message, String apiResponse) {
        super(message + " - API response: " + apiResponse);
    }
}

