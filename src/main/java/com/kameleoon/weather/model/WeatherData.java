package com.kameleoon.weather.model;

/**
 * Main weather data model returned by the SDK.
 * This is the standardized format for weather information.
 *
 * @param weather Weather conditions
 * @param temperature Temperature data
 * @param visibility Visibility in meters
 * @param wind Wind data
 * @param datetime Time of data calculation (Unix timestamp, UTC)
 * @param sys Sunrise and sunset data
 * @param timezone Timezone shift in seconds from UTC
 * @param name City name
 * @author Yury Shuldeshov
 */
public record WeatherData(
    Weather weather,
    Temperature temperature,
    Integer visibility,
    Wind wind,
    Long datetime,
    Sys sys,
    Integer timezone,
    String name
) {
    
    /**
     * Compact constructor with validation.
     */
    public WeatherData {
        if (weather == null) {
            throw new IllegalArgumentException("Weather cannot be null");
        }
        if (temperature == null) {
            throw new IllegalArgumentException("Temperature cannot be null");
        }
        if (visibility == null) {
            throw new IllegalArgumentException("Visibility cannot be null");
        }
        if (wind == null) {
            throw new IllegalArgumentException("Wind cannot be null");
        }
        if (datetime == null) {
            throw new IllegalArgumentException("Datetime cannot be null");
        }
        if (sys == null) {
            throw new IllegalArgumentException("Sys data cannot be null");
        }
        if (timezone == null) {
            throw new IllegalArgumentException("Timezone cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("City name cannot be null or blank");
        }
    }
}

