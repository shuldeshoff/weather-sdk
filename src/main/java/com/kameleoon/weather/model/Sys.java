package com.kameleoon.weather.model;

/**
 * Represents system data (sunrise and sunset times).
 *
 * @param sunrise Sunrise time (Unix timestamp, UTC)
 * @param sunset Sunset time (Unix timestamp, UTC)
 * @author Yury Shuldeshov
 */
public record Sys(Long sunrise, Long sunset) {
    
    /**
     * Compact constructor with validation.
     */
    public Sys {
        if (sunrise == null) {
            throw new IllegalArgumentException("Sunrise time cannot be null");
        }
        if (sunset == null) {
            throw new IllegalArgumentException("Sunset time cannot be null");
        }
        if (sunrise < 0) {
            throw new IllegalArgumentException("Sunrise time cannot be negative");
        }
        if (sunset < 0) {
            throw new IllegalArgumentException("Sunset time cannot be negative");
        }
    }
}

