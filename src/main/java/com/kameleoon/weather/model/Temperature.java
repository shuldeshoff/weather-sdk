package com.kameleoon.weather.model;

/**
 * Represents temperature data.
 *
 * @param temp Actual temperature in Celsius
 * @param feelsLike Feels like temperature in Celsius
 * @author Yury Shuldeshov
 */
public record Temperature(Double temp, Double feelsLike) {
    
    /**
     * Compact constructor with validation.
     */
    public Temperature {
        if (temp == null) {
            throw new IllegalArgumentException("Temperature cannot be null");
        }
        if (feelsLike == null) {
            throw new IllegalArgumentException("Feels like temperature cannot be null");
        }
        // Basic sanity check (absolute zero is -273.15°C)
        if (temp < -273.15) {
            throw new IllegalArgumentException("Temperature below absolute zero");
        }
    }
}

