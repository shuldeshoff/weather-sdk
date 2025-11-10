package com.kameleoon.weather.model;

/**
 * Represents wind data.
 *
 * @param speed Wind speed in meters/second
 * @author Yury Shuldeshov
 */
public record Wind(Double speed) {
    
    /**
     * Compact constructor with validation.
     */
    public Wind {
        if (speed == null) {
            throw new IllegalArgumentException("Wind speed cannot be null");
        }
        if (speed < 0) {
            throw new IllegalArgumentException("Wind speed cannot be negative");
        }
    }
}

