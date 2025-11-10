package com.kameleoon.weather.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service for polling weather data for registered locations.
 * Uses ScheduledExecutorService for periodic updates.
 *
 * @author Yury Shuldeshov
 */
public class PollingService {
    
    private static final Logger logger = LoggerFactory.getLogger(PollingService.class);
    
    private final WeatherService weatherService;
    private final LocationRegistry locationRegistry;
    private final long intervalMinutes;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running;
    
    /**
     * Creates a new PollingService.
     *
     * @param weatherService Weather service for fetching data
     * @param locationRegistry Registry of locations to poll
     * @param intervalMinutes Polling interval in minutes
     * @throws IllegalArgumentException if parameters are invalid
     */
    public PollingService(
            WeatherService weatherService,
            LocationRegistry locationRegistry,
            long intervalMinutes) {
        
        if (weatherService == null) {
            throw new IllegalArgumentException("Weather service cannot be null");
        }
        if (locationRegistry == null) {
            throw new IllegalArgumentException("Location registry cannot be null");
        }
        if (intervalMinutes <= 0) {
            throw new IllegalArgumentException("Interval must be positive");
        }
        
        this.weatherService = weatherService;
        this.locationRegistry = locationRegistry;
        this.intervalMinutes = intervalMinutes;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "weather-polling-thread");
            thread.setDaemon(true);
            return thread;
        });
        this.running = new AtomicBoolean(false);
    }
    
    /**
     * Starts the polling service.
     * Schedules periodic updates for all registered locations.
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            logger.info("Starting polling service with interval {} minutes", intervalMinutes);
            
            scheduler.scheduleAtFixedRate(
                this::pollAllLocations,
                0,  // Initial delay
                intervalMinutes,
                TimeUnit.MINUTES
            );
            
            logger.info("Polling service started successfully");
        } else {
            logger.warn("Polling service is already running");
        }
    }
    
    /**
     * Stops the polling service.
     * Waits for current polling cycle to complete.
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            logger.info("Stopping polling service...");
            
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                    logger.warn("Polling service did not terminate gracefully, forced shutdown");
                } else {
                    logger.info("Polling service stopped successfully");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                scheduler.shutdownNow();
                logger.error("Polling service shutdown interrupted", e);
            }
        } else {
            logger.warn("Polling service is not running");
        }
    }
    
    /**
     * Checks if the polling service is running.
     *
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running.get();
    }
    
    /**
     * Polls all registered locations and updates cache.
     */
    private void pollAllLocations() {
        Set<String> locations = locationRegistry.getAllLocations();
        
        if (locations.isEmpty()) {
            logger.debug("No locations registered for polling");
            return;
        }
        
        logger.debug("Polling {} locations", locations.size());
        
        int successCount = 0;
        int failureCount = 0;
        
        for (String cityName : locations) {
            try {
                weatherService.fetchFreshWeather(cityName);
                successCount++;
                logger.debug("Successfully polled weather for: {}", cityName);
                
            } catch (Exception e) {
                failureCount++;
                logger.error("Failed to poll weather for city: {}", cityName, e);
            }
        }
        
        logger.info("Polling cycle completed: {} successful, {} failed out of {} locations",
            successCount, failureCount, locations.size());
    }
    
    /**
     * Triggers an immediate poll of all locations.
     * Does not affect the scheduled polling.
     */
    public void pollNow() {
        if (!running.get()) {
            logger.warn("Cannot poll: service is not running");
            return;
        }
        
        logger.info("Triggering immediate poll");
        scheduler.execute(this::pollAllLocations);
    }
}

