package com.kameleoon.weather.model;

/**
 * Represents weather conditions.
 *
 * @param main Weather group (Rain, Snow, Clouds, etc.)
 * @param description Detailed weather description
 * @author Yury Shuldeshov
 */
public record Weather(String main, String description) {
    
    /**
     * Compact constructor with validation.
     */
    public Weather {
        if (main == null || main.isBlank()) {
            throw new IllegalArgumentException("Weather main cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Weather description cannot be null or blank");
        }
    }
}

