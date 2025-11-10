package com.kameleoon.weather.client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kameleoon.weather.exception.*;
import com.kameleoon.weather.model.api.OpenWeatherMapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * HTTP client for OpenWeatherMap API.
 * Handles communication with the weather API including retries and error handling.
 *
 * @author Yury Shuldeshov
 */
public class OpenWeatherMapClient {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenWeatherMapClient.class);
    
    private static final String DEFAULT_API_BASE_URL = "https://api.openweathermap.org/data/2.5";
    private static final String CURRENT_WEATHER_ENDPOINT = "/weather";
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;
    
    private final String apiKey;
    private final String apiBaseUrl;
    private final HttpClient httpClient;
    private final Gson gson;
    private final int maxRetries;
    
    /**
     * Creates a new OpenWeatherMapClient with the specified API key.
     *
     * @param apiKey The OpenWeatherMap API key
     * @throws ValidationException if API key is null or blank
     */
    public OpenWeatherMapClient(String apiKey) {
        this(apiKey, DEFAULT_MAX_RETRIES);
    }
    
    /**
     * Creates a new OpenWeatherMapClient with the specified API key and max retries.
     *
     * @param apiKey The OpenWeatherMap API key
     * @param maxRetries Maximum number of retry attempts
     * @throws ValidationException if API key is null or blank or maxRetries is invalid
     */
    public OpenWeatherMapClient(String apiKey, int maxRetries) {
        this(apiKey, DEFAULT_API_BASE_URL, maxRetries);
    }
    
    /**
     * Creates a new OpenWeatherMapClient with the specified API key, base URL, and max retries.
     * This constructor is primarily for testing purposes.
     *
     * @param apiKey The OpenWeatherMap API key
     * @param apiBaseUrl The base URL for the API (for testing)
     * @param maxRetries Maximum number of retry attempts
     * @throws ValidationException if parameters are invalid
     */
    public OpenWeatherMapClient(String apiKey, String apiBaseUrl, int maxRetries) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ValidationException("API key cannot be null or blank");
        }
        if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
            throw new ValidationException("API base URL cannot be null or blank");
        }
        if (maxRetries < 0) {
            throw new ValidationException("Max retries cannot be negative");
        }
        
        this.apiKey = apiKey;
        this.apiBaseUrl = apiBaseUrl;
        this.maxRetries = maxRetries;
        this.gson = new Gson();
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
            .build();
        
        logger.debug("OpenWeatherMapClient initialized with baseUrl={}, maxRetries={}", apiBaseUrl, maxRetries);
    }
    
    /**
     * Gets current weather data for the specified city.
     *
     * @param cityName Name of the city
     * @return OpenWeatherMapResponse containing weather data
     * @throws ValidationException if city name is invalid
     * @throws InvalidApiKeyException if API key is invalid (HTTP 401)
     * @throws CityNotFoundException if city is not found (HTTP 404)
     * @throws RateLimitException if rate limit is exceeded (HTTP 429)
     * @throws ApiUnavailableException if API is unavailable or network error occurs
     */
    public OpenWeatherMapResponse getCurrentWeather(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            throw new ValidationException("City name cannot be null or blank");
        }
        
        String url = buildUrl(cityName);
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.debug("Fetching weather for city: {} (attempt {}/{})", cityName, attempt, maxRetries);
                String responseBody = executeRequest(url);
                return parseResponse(responseBody);
                
            } catch (InvalidApiKeyException | CityNotFoundException | ValidationException | RateLimitException e) {
                // Don't retry on client errors (4xx)
                logger.error("Client error for city {}: {}", cityName, e.getMessage());
                throw e;
                
            } catch (ApiUnavailableException e) {
                // Retry on server errors (5xx) and network errors
                if (attempt == maxRetries) {
                    logger.error("Failed to fetch weather for city {} after {} attempts", cityName, maxRetries);
                    throw e;
                }
                
                logger.warn("Request failed for city {}, retrying... (attempt {}/{})", 
                    cityName, attempt, maxRetries);
                sleep(calculateBackoffDelay(attempt));
            }
        }
        
        throw new ApiUnavailableException("Failed to fetch weather after " + maxRetries + " attempts");
    }
    
    /**
     * Builds the full URL for the weather API request.
     */
    private String buildUrl(String cityName) {
        try {
            String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
            return String.format("%s%s?q=%s&appid=%s&units=metric",
                apiBaseUrl, CURRENT_WEATHER_ENDPOINT, encodedCity, apiKey);
        } catch (Exception e) {
            throw new ValidationException("Failed to encode city name: " + cityName, e);
        }
    }
    
    /**
     * Executes the HTTP request and returns the response body.
     */
    private String executeRequest(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );
            
            logger.debug("Received HTTP response: status={}", response.statusCode());
            
            handleStatusCode(response.statusCode(), response.body());
            return response.body();
            
        } catch (IOException e) {
            logger.error("Network error: {}", e.getMessage());
            throw new ApiUnavailableException("Network error: " + e.getMessage(), e);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Request interrupted: {}", e.getMessage());
            throw new ApiUnavailableException("Request interrupted", e);
        }
    }
    
    /**
     * Handles HTTP status codes and throws appropriate exceptions.
     */
    private void handleStatusCode(int statusCode, String body) {
        if (statusCode == 200) {
            return; // Success
        }
        
        logger.warn("API returned non-200 status code: {}", statusCode);
        
        switch (statusCode) {
            case 401:
                throw new InvalidApiKeyException("Invalid API key. Please check your API key.", body);
            case 404:
                throw new CityNotFoundException("Unknown", body);
            case 429:
                throw new RateLimitException("API rate limit exceeded. Please try again later.", body);
            case 500:
            case 502:
            case 503:
            case 504:
                throw new ApiUnavailableException("API server error", statusCode, body);
            default:
                throw new ApiException("API error: HTTP " + statusCode + " - " + body);
        }
    }
    
    /**
     * Parses the JSON response into OpenWeatherMapResponse object.
     */
    private OpenWeatherMapResponse parseResponse(String json) {
        try {
            OpenWeatherMapResponse response = gson.fromJson(json, OpenWeatherMapResponse.class);
            
            if (response == null) {
                throw new ApiException("Received null response from API");
            }
            
            validateResponse(response);
            return response;
            
        } catch (JsonSyntaxException e) {
            logger.error("Failed to parse API response: {}", e.getMessage());
            throw new ApiException("Failed to parse API response", e);
        }
    }
    
    /**
     * Validates that the response contains all required fields.
     */
    private void validateResponse(OpenWeatherMapResponse response) {
        if (response.getWeather() == null || response.getWeather().isEmpty()) {
            throw new ApiException("API response missing weather information");
        }
        if (response.getMain() == null) {
            throw new ApiException("API response missing temperature information");
        }
        if (response.getWind() == null) {
            throw new ApiException("API response missing wind information");
        }
        if (response.getSys() == null) {
            throw new ApiException("API response missing sys information");
        }
        if (response.getName() == null || response.getName().isBlank()) {
            throw new ApiException("API response missing city name");
        }
    }
    
    /**
     * Extracts city name from error response body (if available).
     */
    private String extractCityFromError(String body) {
        // Simple extraction, could be improved with JSON parsing
        return "unknown";
    }
    
    /**
     * Calculates exponential backoff delay.
     */
    private long calculateBackoffDelay(int attempt) {
        return Math.min(1000L * (long) Math.pow(2, attempt - 1), 10000L);
    }
    
    /**
     * Sleeps for the specified number of milliseconds.
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted");
        }
    }
}

