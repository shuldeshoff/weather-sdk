package com.kameleoon.weather.exception;

/**
 * Exception thrown when the requested city is not found in the weather API.
 * HTTP Status Code: 404
 *
 * @author Yury Shuldeshov
 */
public class CityNotFoundException extends ApiException {
    
    private final String cityName;
    
    /**
     * Constructs a new CityNotFoundException with the specified detail message.
     *
     * @param message The detail message
     * @param cityName The name of the city that was not found
     */
    public CityNotFoundException(String message, String cityName) {
        super(message);
        this.cityName = cityName;
    }
    
    /**
     * Gets the name of the city that was not found.
     *
     * @return The city name
     */
    public String getCityName() {
        return cityName;
    }
}

