package com.kameleoon.weather.model.api;

import java.util.List;

/**
 * Response model from OpenWeatherMap API.
 * Represents the raw JSON structure returned by the API.
 * This is an internal model that gets mapped to WeatherData.
 *
 * @author Yury Shuldeshov
 */
public class OpenWeatherMapResponse {
    
    private List<WeatherInfo> weather;
    private MainInfo main;
    private Integer visibility;
    private WindInfo wind;
    private Long dt;
    private SysInfo sys;
    private Integer timezone;
    private String name;
    
    public static class WeatherInfo {
        private String main;
        private String description;
        
        public String getMain() { return main; }
        public void setMain(String main) { this.main = main; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class MainInfo {
        private Double temp;
        private Double feels_like;
        
        public Double getTemp() { return temp; }
        public void setTemp(Double temp) { this.temp = temp; }
        public Double getFeelsLike() { return feels_like; }
        public void setFeelsLike(Double feels_like) { this.feels_like = feels_like; }
    }
    
    public static class WindInfo {
        private Double speed;
        
        public Double getSpeed() { return speed; }
        public void setSpeed(Double speed) { this.speed = speed; }
    }
    
    public static class SysInfo {
        private Long sunrise;
        private Long sunset;
        
        public Long getSunrise() { return sunrise; }
        public void setSunrise(Long sunrise) { this.sunrise = sunrise; }
        public Long getSunset() { return sunset; }
        public void setSunset(Long sunset) { this.sunset = sunset; }
    }
    
    // Getters and setters
    public List<WeatherInfo> getWeather() { return weather; }
    public void setWeather(List<WeatherInfo> weather) { this.weather = weather; }
    public MainInfo getMain() { return main; }
    public void setMain(MainInfo main) { this.main = main; }
    public Integer getVisibility() { return visibility; }
    public void setVisibility(Integer visibility) { this.visibility = visibility; }
    public WindInfo getWind() { return wind; }
    public void setWind(WindInfo wind) { this.wind = wind; }
    public Long getDt() { return dt; }
    public void setDt(Long dt) { this.dt = dt; }
    public SysInfo getSys() { return sys; }
    public void setSys(SysInfo sys) { this.sys = sys; }
    public Integer getTimezone() { return timezone; }
    public void setTimezone(Integer timezone) { this.timezone = timezone; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

