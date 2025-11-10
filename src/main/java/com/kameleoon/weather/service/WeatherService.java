package com.kameleoon.weather.service;

import com.kameleoon.weather.client.OpenWeatherMapClient;
import com.kameleoon.weather.exception.ValidationException;
import com.kameleoon.weather.model.*;
import com.kameleoon.weather.model.api.OpenWeatherMapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Service for retrieving weather data.
 * Coordinates between cache and API client.
 *
 * @author Yury Shuldeshov
 */
public class WeatherService {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    
    private final OpenWeatherMapClient apiClient;
    private final CacheService cacheService;
    
    /**
     * Creates a new WeatherService.
     *
     * @param apiClient Client for API communication
     * @param cacheService Service for caching
     * @throws IllegalArgumentException if parameters are null
     */
    public WeatherService(OpenWeatherMapClient apiClient, CacheService cacheService) {
        if (apiClient == null) {
            throw new IllegalArgumentException("API client cannot be null");
        }
        if (cacheService == null) {
            throw new IllegalArgumentException("Cache service cannot be null");
        }
        
        this.apiClient = apiClient;
        this.cacheService = cacheService;
        
        logger.info("WeatherService initialized");
    }
    
    /**
     * Gets weather data for a city.
     * Checks cache first, then fetches from API if needed.
     *
     * @param cityName Name of the city
     * @return Weather data
     * @throws ValidationException if city name is invalid
     */
    public WeatherData getWeather(String cityName) {
        validateCityName(cityName);
        
        // Try cache first
        Optional<WeatherData> cached = cacheService.get(cityName);
        if (cached.isPresent()) {
            logger.debug("Returning cached weather for city: {}", cityName);
            return cached.get();
        }
        
        // Fetch from API
        return fetchFreshWeather(cityName);
    }
    
    /**
     * Fetches fresh weather data from API, bypassing cache.
     * Updates cache with new data.
     *
     * @param cityName Name of the city
     * @return Weather data
     * @throws ValidationException if city name is invalid
     */
    public WeatherData fetchFreshWeather(String cityName) {
        validateCityName(cityName);
        
        logger.debug("Fetching fresh weather for city: {}", cityName);
        
        OpenWeatherMapResponse response = apiClient.getCurrentWeather(cityName);
        WeatherData data = mapToWeatherData(response);
        
        // Update cache
        cacheService.put(cityName, data);
        
        logger.info("Fetched and cached weather for city: {}", cityName);
        return data;
    }
    
    /**
     * Maps API response to internal WeatherData model.
     */
    private WeatherData mapToWeatherData(OpenWeatherMapResponse response) {
        try {
            OpenWeatherMapResponse.WeatherInfo weatherInfo = response.getWeather().get(0);
            
            Weather weather = new Weather(
                weatherInfo.getMain(),
                weatherInfo.getDescription()
            );
            
            Temperature temperature = new Temperature(
                response.getMain().getTemp(),
                response.getMain().getFeelsLike()
            );
            
            Wind wind = new Wind(
                response.getWind().getSpeed()
            );
            
            Sys sys = new Sys(
                response.getSys().getSunrise(),
                response.getSys().getSunset()
            );
            
            return new WeatherData(
                weather,
                temperature,
                response.getVisibility(),
                wind,
                response.getDt(),
                sys,
                response.getTimezone(),
                response.getName()
            );
            
        } catch (Exception e) {
            logger.error("Failed to map API response to WeatherData", e);
            throw new ValidationException("Failed to process API response: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates city name.
     */
    private void validateCityName(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            throw new ValidationException("City name cannot be null or blank");
        }
        if (cityName.length() > 200) {
            throw new ValidationException("City name is too long (max 200 characters)");
        }
        // Additional validation could be added here
    }
}

