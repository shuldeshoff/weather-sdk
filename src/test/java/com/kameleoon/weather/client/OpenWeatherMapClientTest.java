package com.kameleoon.weather.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.kameleoon.weather.exception.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OpenWeatherMapClient using WireMock.
 *
 * @author Yury Shuldeshov
 */
class OpenWeatherMapClientTest {
    
    private WireMockServer wireMockServer;
    private OpenWeatherMapClient client;
    private static final String API_KEY = "test-api-key";
    
    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
        
        client = new OpenWeatherMapClient(API_KEY, "http://localhost:8089", 3);
    }
    
    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }
    
    private String getSuccessResponse() {
        return """
            {
                "weather": [
                    {
                        "main": "Clouds",
                        "description": "scattered clouds"
                    }
                ],
                "main": {
                    "temp": 20.5,
                    "feels_like": 19.0
                },
                "visibility": 10000,
                "wind": {
                    "speed": 5.5
                },
                "dt": 1699594800,
                "sys": {
                    "sunrise": 1699594800,
                    "sunset": 1699632000
                },
                "timezone": 3600,
                "name": "London"
            }
            """;
    }
    
    @Test
    void shouldThrowExceptionForNullApiKey() {
        assertThrows(ValidationException.class, () -> new OpenWeatherMapClient(null));
    }
    
    @Test
    void shouldThrowExceptionForBlankApiKey() {
        assertThrows(ValidationException.class, () -> new OpenWeatherMapClient(""));
        assertThrows(ValidationException.class, () -> new OpenWeatherMapClient("   "));
    }
    
    @Test
    void shouldThrowExceptionForNegativeMaxRetries() {
        assertThrows(ValidationException.class, () -> new OpenWeatherMapClient(API_KEY, -1));
    }
    
    @Test
    void shouldThrowExceptionForNullCityName() {
        assertThrows(ValidationException.class, () -> client.getCurrentWeather(null));
    }
    
    @Test
    void shouldThrowExceptionForBlankCityName() {
        assertThrows(ValidationException.class, () -> client.getCurrentWeather(""));
        assertThrows(ValidationException.class, () -> client.getCurrentWeather("   "));
    }
    
    @Test
    void shouldThrowInvalidApiKeyExceptionFor401() {
        stubFor(get(urlPathEqualTo("/weather"))
            .willReturn(aResponse()
                .withStatus(401)
                .withBody("{\"message\":\"Invalid API key\"}")));
        
        assertThrows(InvalidApiKeyException.class, () -> client.getCurrentWeather("London"));
    }
    
    @Test
    void shouldThrowCityNotFoundExceptionFor404() {
        stubFor(get(urlPathEqualTo("/weather"))
            .willReturn(aResponse()
                .withStatus(404)
                .withBody("{\"message\":\"city not found\"}")));
        
        assertThrows(CityNotFoundException.class, () -> client.getCurrentWeather("NonExistentCity"));
    }
    
    @Test
    void shouldThrowRateLimitExceptionFor429() {
        stubFor(get(urlPathEqualTo("/weather"))
            .willReturn(aResponse()
                .withStatus(429)
                .withBody("{\"message\":\"rate limit exceeded\"}")));
        
        assertThrows(RateLimitException.class, () -> client.getCurrentWeather("London"));
    }
    
    @Test
    void shouldThrowApiUnavailableExceptionFor500() {
        stubFor(get(urlPathEqualTo("/weather"))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("{\"message\":\"internal server error\"}")));
        
        assertThrows(ApiUnavailableException.class, () -> client.getCurrentWeather("London"));
    }
    
    @Test
    void shouldThrowApiUnavailableExceptionFor503() {
        stubFor(get(urlPathEqualTo("/weather"))
            .willReturn(aResponse()
                .withStatus(503)
                .withBody("{\"message\":\"service unavailable\"}")));
        
        assertThrows(ApiUnavailableException.class, () -> client.getCurrentWeather("London"));
    }
}

