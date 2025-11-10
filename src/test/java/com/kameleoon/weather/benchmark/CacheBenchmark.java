package com.kameleoon.weather.benchmark;

import com.kameleoon.weather.model.CacheEntry;
import com.kameleoon.weather.model.WeatherData;
import com.kameleoon.weather.model.Temperature;
import com.kameleoon.weather.model.Weather;
import com.kameleoon.weather.model.Wind;
import com.kameleoon.weather.model.Sys;
import com.kameleoon.weather.service.CacheService;
import com.kameleoon.weather.util.LRUCache;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark for cache performance.
 * 
 * <p>Measures the performance of:
 * <ul>
 *   <li>Cache put operations</li>
 *   <li>Cache get operations (hits)</li>
 *   <li>Cache get operations (misses)</li>
 *   <li>LRU eviction performance</li>
 * </ul>
 *
 * <p>To run this benchmark:
 * <pre>{@code
 * mvn clean test-compile
 * java -cp target/test-classes:target/classes com.kameleoon.weather.benchmark.CacheBenchmark
 * }</pre>
 *
 * @author Yury Shuldeshov
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, warmups = 1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CacheBenchmark {

    private CacheService cacheService;
    private LRUCache<String, CacheEntry> lruCache;
    private WeatherData sampleWeatherData;
    
    private static final int CACHE_SIZE = 100;
    private static final int TTL_MINUTES = 10;

    @Setup(Level.Iteration)
    public void setup() {
        cacheService = new CacheService(CACHE_SIZE, TTL_MINUTES);
        lruCache = new LRUCache<>(CACHE_SIZE);
        sampleWeatherData = createSampleWeatherData();
        
        // Pre-populate cache for hit tests
        for (int i = 0; i < 50; i++) {
            cacheService.put("city" + i, sampleWeatherData);
        }
    }

    /**
     * Benchmark: Put operation into CacheService
     */
    @Benchmark
    public void benchmarkCacheServicePut() {
        cacheService.put("London", sampleWeatherData);
    }

    /**
     * Benchmark: Get operation from CacheService (cache hit)
     */
    @Benchmark
    public void benchmarkCacheServiceGetHit() {
        cacheService.get("city10");
    }

    /**
     * Benchmark: Get operation from CacheService (cache miss)
     */
    @Benchmark
    public void benchmarkCacheServiceGetMiss() {
        cacheService.get("nonexistent-city");
    }

    /**
     * Benchmark: Put operation into LRUCache directly
     */
    @Benchmark
    public void benchmarkLRUCachePut() {
        CacheEntry entry = new CacheEntry(sampleWeatherData, System.currentTimeMillis());
        lruCache.put("London", entry);
    }

    /**
     * Benchmark: Get operation from LRUCache (cache hit)
     */
    @Benchmark
    public CacheEntry benchmarkLRUCacheGetHit() {
        return lruCache.get("London");
    }

    /**
     * Benchmark: Cache eviction (LRU replacement)
     */
    @Benchmark
    public void benchmarkCacheEviction() {
        // Fill cache to capacity
        for (int i = 0; i < CACHE_SIZE + 10; i++) {
            cacheService.put("city" + i, sampleWeatherData);
        }
    }

    /**
     * Benchmark: Concurrent cache access
     */
    @Benchmark
    @Threads(4)
    public void benchmarkConcurrentAccess() {
        cacheService.put("concurrent-city", sampleWeatherData);
        cacheService.get("concurrent-city");
    }

    /**
     * Creates sample weather data for benchmarking
     */
    private WeatherData createSampleWeatherData() {
        Temperature temp = new Temperature(15.5, 12.3);
        Weather weather = new Weather("Clouds", "Scattered clouds");
        Wind wind = new Wind(5.5);
        Sys sys = new Sys(1640000000L, 1640036400L);
        
        return new WeatherData(
            weather,
            temp,
            10000,
            wind,
            1640012345L,
            sys,
            0,
            "London"
        );
    }

    /**
     * Main method to run the benchmark
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(CacheBenchmark.class.getSimpleName())
            .forks(1)
            .build();

        new Runner(opt).run();
    }
}

