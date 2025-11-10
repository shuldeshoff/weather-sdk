# План реализации Weather SDK на Java

## Дата: 10 ноября 2025
## Версия: 1.0

---

## 1. Структура проекта

### 1.1 Дерево директорий

```
weather-sdk/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── kameleoon/
│   │   │           └── weather/
│   │   │               ├── WeatherSDK.java
│   │   │               ├── WeatherSDKFactory.java
│   │   │               ├── config/
│   │   │               │   ├── SDKConfig.java
│   │   │               │   ├── OperationMode.java
│   │   │               │   └── ConfigBuilder.java
│   │   │               ├── service/
│   │   │               │   ├── WeatherService.java
│   │   │               │   ├── CacheService.java
│   │   │               │   └── PollingService.java
│   │   │               ├── client/
│   │   │               │   ├── OpenWeatherMapClient.java
│   │   │               │   ├── HttpClientWrapper.java
│   │   │               │   └── ApiResponseMapper.java
│   │   │               ├── model/
│   │   │               │   ├── WeatherData.java
│   │   │               │   ├── Weather.java
│   │   │               │   ├── Temperature.java
│   │   │               │   ├── Wind.java
│   │   │               │   ├── Sys.java
│   │   │               │   ├── CacheEntry.java
│   │   │               │   ├── CacheInfo.java
│   │   │               │   └── api/
│   │   │               │       └── OpenWeatherMapResponse.java
│   │   │               ├── exception/
│   │   │               │   ├── WeatherSDKException.java
│   │   │               │   ├── ApiException.java
│   │   │               │   ├── InvalidApiKeyException.java
│   │   │               │   ├── RateLimitException.java
│   │   │               │   ├── CityNotFoundException.java
│   │   │               │   ├── ApiUnavailableException.java
│   │   │               │   ├── CacheException.java
│   │   │               │   ├── ValidationException.java
│   │   │               │   └── ConfigurationException.java
│   │   │               └── util/
│   │   │                   ├── JsonUtil.java
│   │   │                   ├── ValidationUtil.java
│   │   │                   └── LRUCache.java
│   │   └── resources/
│   │       ├── logback.xml
│   │       └── sdk.properties
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── kameleoon/
│       │           └── weather/
│       │               ├── WeatherSDKTest.java
│       │               ├── WeatherSDKFactoryTest.java
│       │               ├── integration/
│       │               │   ├── OnDemandModeIntegrationTest.java
│       │               │   └── PollingModeIntegrationTest.java
│       │               ├── service/
│       │               │   ├── WeatherServiceTest.java
│       │               │   ├── CacheServiceTest.java
│       │               │   └── PollingServiceTest.java
│       │               ├── client/
│       │               │   └── OpenWeatherMapClientTest.java
│       │               └── util/
│       │                   └── LRUCacheTest.java
│       └── resources/
│           ├── test-responses/
│           │   ├── valid-response.json
│           │   ├── invalid-key-response.json
│           │   └── city-not-found-response.json
│           └── logback-test.xml
├── examples/
│   ├── OnDemandExample.java
│   ├── PollingExample.java
│   ├── MultipleInstancesExample.java
│   └── ErrorHandlingExample.java
├── docs/
│   ├── 01_requirements_analysis.md
│   ├── 02_architecture_design.md
│   ├── 03_implementation_plan.md
│   ├── 04_api_documentation.md
│   └── 05_user_guide.md
├── pom.xml (или build.gradle)
├── README.md
├── LICENSE
├── .gitignore
└── CHANGELOG.md
```

---

## 2. Фазы разработки

### Phase 1: Инфраструктура и базовые компоненты (2-3 дня)

#### Sprint 1.1: Настройка проекта
- [ ] Создать Maven/Gradle проект
- [ ] Настроить зависимости
- [ ] Настроить Checkstyle, SpotBugs
- [ ] Настроить CI/CD (GitHub Actions)
- [ ] Создать базовую структуру пакетов

#### Sprint 1.2: Модели данных
- [ ] Реализовать WeatherData и вложенные модели
- [ ] Реализовать OpenWeatherMapResponse
- [ ] Реализовать CacheEntry
- [ ] Написать unit-тесты для моделей
- [ ] Добавить equals/hashCode/toString

#### Sprint 1.3: Исключения
- [ ] Создать иерархию исключений
- [ ] Реализовать все кастомные исключения
- [ ] Добавить информативные сообщения
- [ ] Документировать каждое исключение

### Phase 2: Core функциональность (3-4 дня)

#### Sprint 2.1: HTTP клиент
- [ ] Реализовать HttpClientWrapper
- [ ] Реализовать OpenWeatherMapClient
- [ ] Добавить retry механизм
- [ ] Добавить таймауты
- [ ] Написать unit-тесты с WireMock
- [ ] Протестировать различные error cases

#### Sprint 2.2: Cache Service
- [ ] Реализовать LRUCache утилиту
- [ ] Реализовать CacheService
- [ ] Добавить TTL проверки
- [ ] Добавить eviction policy
- [ ] Реализовать thread-safety
- [ ] Написать unit-тесты
- [ ] Протестировать concurrent access

#### Sprint 2.3: Weather Service
- [ ] Реализовать WeatherService
- [ ] Интегрировать с CacheService
- [ ] Интегрировать с OpenWeatherMapClient
- [ ] Добавить валидацию входных данных
- [ ] Реализовать маппинг API response → WeatherData
- [ ] Написать unit-тесты
- [ ] Написать integration тесты

### Phase 3: Advanced функциональность (3-4 дня)

#### Sprint 3.1: Polling Service
- [ ] Реализовать PollingService
- [ ] Настроить ScheduledExecutorService
- [ ] Реализовать graceful shutdown
- [ ] Добавить error handling в polling loop
- [ ] Написать unit-тесты
- [ ] Протестировать с помощью AwaitBility

#### Sprint 3.2: SDK Facade
- [ ] Реализовать WeatherSDK (facade)
- [ ] Интегрировать все сервисы
- [ ] Реализовать координацию режимов работы
- [ ] Добавить публичные методы API
- [ ] Написать unit-тесты
- [ ] Написать integration тесты

#### Sprint 3.3: Factory (Multiton)
- [ ] Реализовать WeatherSDKFactory
- [ ] Реализовать thread-safe registry
- [ ] Добавить методы управления экземплярами
- [ ] Реализовать automatic cleanup
- [ ] Написать unit-тесты
- [ ] Протестировать concurrent access

### Phase 4: Конфигурация и утилиты (1-2 дня)

#### Sprint 4.1: Configuration
- [ ] Реализовать SDKConfig
- [ ] Реализовать ConfigBuilder
- [ ] Добавить validation конфигурации
- [ ] Добавить defaults
- [ ] Написать unit-тесты

#### Sprint 4.2: Utilities
- [ ] Реализовать JsonUtil
- [ ] Реализовать ValidationUtil
- [ ] Добавить helper методы
- [ ] Написать unit-тесты

### Phase 5: Документация и примеры (2-3 дня)

#### Sprint 5.1: API Documentation
- [ ] Добавить Javadoc для всех публичных классов
- [ ] Добавить Javadoc для всех публичных методов
- [ ] Создать API documentation (Javadoc HTML)
- [ ] Написать API reference guide

#### Sprint 5.2: User Guide
- [ ] Написать Getting Started guide
- [ ] Написать Installation instructions
- [ ] Написать Configuration guide
- [ ] Написать Troubleshooting guide
- [ ] Написать Best Practices

#### Sprint 5.3: Examples
- [ ] Создать OnDemandExample
- [ ] Создать PollingExample
- [ ] Создать MultipleInstancesExample
- [ ] Создать ErrorHandlingExample
- [ ] Создать AdvancedUsageExample

#### Sprint 5.4: Project Documentation
- [ ] Написать README.md
- [ ] Написать CONTRIBUTING.md
- [ ] Написать CHANGELOG.md
- [ ] Добавить LICENSE
- [ ] Создать архитектурные диаграммы

### Phase 6: Тестирование и качество (2-3 дня)

#### Sprint 6.1: Unit Tests
- [ ] Достичь 80%+ code coverage
- [ ] Все edge cases покрыты
- [ ] Все error paths протестированы
- [ ] Mock все внешние зависимости

#### Sprint 6.2: Integration Tests
- [ ] End-to-end тесты для on-demand mode
- [ ] End-to-end тесты для polling mode
- [ ] Тесты с реальным API (optional)
- [ ] Performance тесты

#### Sprint 6.3: Quality Checks
- [ ] Запустить Checkstyle
- [ ] Запустить SpotBugs
- [ ] Запустить OWASP Dependency Check
- [ ] Code review
- [ ] Refactoring где необходимо

### Phase 7: Packaging и Release (1-2 дня)

#### Sprint 7.1: Build Configuration
- [ ] Настроить Maven/Gradle для создания JAR
- [ ] Создать fat JAR с зависимостями
- [ ] Создать slim JAR без зависимостей
- [ ] Настроить Maven publishing

#### Sprint 7.2: Release
- [ ] Создать release notes
- [ ] Tag версию в Git
- [ ] Опубликовать в Maven Central / GitHub Packages
- [ ] Создать GitHub Release с artifacts

---

## 3. Детальная реализация компонентов

### 3.1 WeatherSDKFactory

**Файл:** `WeatherSDKFactory.java`

**Задачи:**
1. Создать ConcurrentHashMap для хранения экземпляров
2. Реализовать thread-safe getInstance() с double-checked locking
3. Реализовать removeInstance() с graceful shutdown
4. Добавить вспомогательные методы (hasInstance, getActiveKeys)
5. Добавить логирование

**Псевдокод:**
```java
public class WeatherSDKFactory {
    private static final Map<String, WeatherSDK> instances = new ConcurrentHashMap<>();
    private static final Object lock = new Object();
    
    public static WeatherSDK getInstance(String apiKey, OperationMode mode) {
        validateApiKey(apiKey);
        
        return instances.computeIfAbsent(apiKey, key -> {
            synchronized (lock) {
                // Double-check
                if (instances.containsKey(key)) {
                    return instances.get(key);
                }
                
                // Create new instance
                WeatherSDK sdk = new WeatherSDK(key, mode);
                logger.info("Created new SDK instance for API key: {}", maskApiKey(key));
                return sdk;
            }
        });
    }
    
    public static void removeInstance(String apiKey) {
        WeatherSDK sdk = instances.remove(apiKey);
        if (sdk != null) {
            sdk.shutdown();
            logger.info("Removed SDK instance for API key: {}", maskApiKey(apiKey));
        }
    }
    
    private static void validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ValidationException("API key cannot be null or empty");
        }
    }
    
    private static String maskApiKey(String apiKey) {
        // Show only first 4 and last 4 characters
        if (apiKey.length() <= 8) return "****";
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
```

**Тесты:**
- Создание нового экземпляра
- Возврат существующего экземпляра для того же ключа
- Concurrent создание с одним ключом (thread-safety)
- Удаление экземпляра
- Валидация API ключа

---

### 3.2 WeatherSDK (Facade)

**Файл:** `WeatherSDK.java`

**Задачи:**
1. Инициализация всех сервисов в конструкторе
2. Реализовать getWeather() с делегацией в WeatherService
3. Реализовать refreshWeather() с bypass кэша
4. Реализовать clearCache()
5. Реализовать shutdown() с cleanup всех ресурсов
6. Добавить методы для получения информации о состоянии

**Псевдокод:**
```java
public class WeatherSDK {
    private final String apiKey;
    private final OperationMode mode;
    private final WeatherService weatherService;
    private final CacheService cacheService;
    private final PollingService pollingService;
    private volatile boolean isShutdown = false;
    
    // Package-private constructor (called from Factory)
    WeatherSDK(String apiKey, OperationMode mode) {
        this.apiKey = apiKey;
        this.mode = mode;
        
        // Initialize services
        this.cacheService = new CacheService(
            SDKConfig.DEFAULT_CACHE_SIZE,
            SDKConfig.DEFAULT_CACHE_TTL_MINUTES
        );
        
        OpenWeatherMapClient apiClient = new OpenWeatherMapClient(apiKey);
        this.weatherService = new WeatherService(apiClient, cacheService);
        
        if (mode == OperationMode.POLLING) {
            this.pollingService = new PollingService(
                weatherService,
                cacheService,
                SDKConfig.DEFAULT_POLLING_INTERVAL_MINUTES
            );
            this.pollingService.start();
        } else {
            this.pollingService = null;
        }
        
        logger.info("WeatherSDK initialized in {} mode", mode);
    }
    
    public WeatherData getWeather(String cityName) throws WeatherSDKException {
        checkNotShutdown();
        validateCityName(cityName);
        
        try {
            return weatherService.getWeather(cityName);
        } catch (Exception e) {
            logger.error("Error getting weather for city: {}", cityName, e);
            throw handleException(e);
        }
    }
    
    public WeatherData refreshWeather(String cityName) throws WeatherSDKException {
        checkNotShutdown();
        validateCityName(cityName);
        
        try {
            return weatherService.fetchFreshWeather(cityName);
        } catch (Exception e) {
            logger.error("Error refreshing weather for city: {}", cityName, e);
            throw handleException(e);
        }
    }
    
    public void clearCache() {
        checkNotShutdown();
        cacheService.clear();
        logger.info("Cache cleared");
    }
    
    public CacheInfo getCacheInfo() {
        checkNotShutdown();
        return new CacheInfo(
            cacheService.getCachedCities(),
            cacheService.getSize(),
            SDKConfig.DEFAULT_CACHE_SIZE
        );
    }
    
    public void shutdown() {
        if (isShutdown) {
            return;
        }
        
        logger.info("Shutting down WeatherSDK");
        
        if (pollingService != null) {
            pollingService.stop();
        }
        
        cacheService.clear();
        isShutdown = true;
        
        logger.info("WeatherSDK shutdown complete");
    }
    
    private void checkNotShutdown() {
        if (isShutdown) {
            throw new IllegalStateException("SDK has been shut down");
        }
    }
    
    private void validateCityName(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new ValidationException("City name cannot be null or empty");
        }
        
        if (cityName.length() > 200) {
            throw new ValidationException("City name is too long (max 200 characters)");
        }
    }
    
    private WeatherSDKException handleException(Exception e) {
        if (e instanceof WeatherSDKException) {
            return (WeatherSDKException) e;
        }
        return new WeatherSDKException("Unexpected error: " + e.getMessage(), e);
    }
}
```

**Тесты:**
- Инициализация в on-demand mode
- Инициализация в polling mode
- Получение погоды (успешный случай)
- Получение погоды (город не найден)
- Refresh погоды
- Clear cache
- Shutdown
- Попытка использования после shutdown

---

### 3.3 CacheService

**Файл:** `CacheService.java`

**Задачи:**
1. Реализовать LRU cache на основе LinkedHashMap
2. Добавить TTL проверки
3. Реализовать thread-safe операции
4. Реализовать eviction при превышении размера
5. Добавить метрики (cache hits/misses)

**Псевдокод:**
```java
public class CacheService {
    private final int maxSize;
    private final long ttlMillis;
    private final Map<String, CacheEntry> cache;
    private long cacheHits = 0;
    private long cacheMisses = 0;
    
    public CacheService(int maxSize, long ttlMinutes) {
        this.maxSize = maxSize;
        this.ttlMillis = ttlMinutes * 60 * 1000;
        
        // LRU Cache using LinkedHashMap
        this.cache = Collections.synchronizedMap(
            new LinkedHashMap<String, CacheEntry>(maxSize + 1, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                    boolean shouldRemove = size() > maxSize;
                    if (shouldRemove) {
                        logger.debug("Evicting city from cache: {}", eldest.getKey());
                    }
                    return shouldRemove;
                }
            }
        );
    }
    
    public Optional<WeatherData> get(String cityName) {
        CacheEntry entry = cache.get(cityName.toLowerCase());
        
        if (entry == null) {
            cacheMisses++;
            logger.debug("Cache miss for city: {}", cityName);
            return Optional.empty();
        }
        
        if (entry.isExpired(ttlMillis)) {
            cache.remove(cityName.toLowerCase());
            cacheMisses++;
            logger.debug("Cache entry expired for city: {}", cityName);
            return Optional.empty();
        }
        
        cacheHits++;
        logger.debug("Cache hit for city: {}", cityName);
        return Optional.of(entry.getData());
    }
    
    public void put(String cityName, WeatherData data) {
        CacheEntry entry = new CacheEntry(data, System.currentTimeMillis());
        cache.put(cityName.toLowerCase(), entry);
        logger.debug("Cached weather data for city: {}", cityName);
    }
    
    public boolean isValid(String cityName) {
        CacheEntry entry = cache.get(cityName.toLowerCase());
        return entry != null && !entry.isExpired(ttlMillis);
    }
    
    public void clear() {
        cache.clear();
        logger.info("Cache cleared");
    }
    
    public Set<String> getCachedCities() {
        return new HashSet<>(cache.keySet());
    }
    
    public void evict(String cityName) {
        cache.remove(cityName.toLowerCase());
        logger.debug("Evicted city from cache: {}", cityName);
    }
    
    public int getSize() {
        return cache.size();
    }
    
    public double getCacheHitRate() {
        long total = cacheHits + cacheMisses;
        return total == 0 ? 0.0 : (double) cacheHits / total;
    }
}
```

**Тесты:**
- Put и get
- TTL expiration
- LRU eviction при превышении размера
- Cache hit/miss метрики
- Thread-safety (concurrent access)
- Clear cache
- Get cached cities

---

### 3.4 WeatherService

**Файл:** `WeatherService.java`

**Задачи:**
1. Координация между CacheService и API Client
2. Проверка кэша перед API вызовом
3. Обновление кэша после успешного API вызова
4. Маппинг API response → WeatherData
5. Обработка ошибок

**Псевдокод:**
```java
public class WeatherService {
    private final OpenWeatherMapClient apiClient;
    private final CacheService cacheService;
    
    public WeatherService(OpenWeatherMapClient apiClient, CacheService cacheService) {
        this.apiClient = apiClient;
        this.cacheService = cacheService;
    }
    
    public WeatherData getWeather(String cityName) throws WeatherSDKException {
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
    
    public WeatherData fetchFreshWeather(String cityName) throws WeatherSDKException {
        validateCityName(cityName);
        
        try {
            logger.debug("Fetching fresh weather for city: {}", cityName);
            OpenWeatherMapResponse response = apiClient.getCurrentWeather(cityName);
            WeatherData data = mapToWeatherData(response);
            
            // Update cache
            cacheService.put(cityName, data);
            
            return data;
        } catch (Exception e) {
            logger.error("Error fetching weather for city: {}", cityName, e);
            throw handleException(e);
        }
    }
    
    private WeatherData mapToWeatherData(OpenWeatherMapResponse response) {
        return WeatherData.builder()
            .weather(new Weather(
                response.getWeather().get(0).getMain(),
                response.getWeather().get(0).getDescription()
            ))
            .temperature(new Temperature(
                response.getMain().getTemp(),
                response.getMain().getFeelsLike()
            ))
            .visibility(response.getVisibility())
            .wind(new Wind(response.getWind().getSpeed()))
            .datetime(response.getDt())
            .sys(new Sys(
                response.getSys().getSunrise(),
                response.getSys().getSunset()
            ))
            .timezone(response.getTimezone())
            .name(response.getName())
            .build();
    }
    
    private void validateCityName(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new ValidationException("City name cannot be null or empty");
        }
    }
    
    private WeatherSDKException handleException(Exception e) {
        // Convert API exceptions to SDK exceptions
        if (e instanceof WeatherSDKException) {
            return (WeatherSDKException) e;
        }
        return new ApiException("Error communicating with weather API", e);
    }
}
```

**Тесты:**
- Get weather (cache hit)
- Get weather (cache miss, API call)
- Fetch fresh weather
- Mapping API response to WeatherData
- Error handling (API errors)
- Validation

---

### 3.5 PollingService

**Файл:** `PollingService.java`

**Задачи:**
1. Запуск ScheduledExecutorService
2. Периодическое обновление всех городов из кэша
3. Graceful shutdown
4. Error handling (не прерывать polling при ошибках)
5. Логирование

**Псевдокод:**
```java
public class PollingService {
    private final WeatherService weatherService;
    private final CacheService cacheService;
    private final long pollingIntervalMinutes;
    private ScheduledExecutorService scheduler;
    private volatile boolean running = false;
    
    public PollingService(
        WeatherService weatherService,
        CacheService cacheService,
        long pollingIntervalMinutes
    ) {
        this.weatherService = weatherService;
        this.cacheService = cacheService;
        this.pollingIntervalMinutes = pollingIntervalMinutes;
    }
    
    public void start() {
        if (running) {
            logger.warn("Polling service is already running");
            return;
        }
        
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "weather-polling-thread");
            thread.setDaemon(true);
            return thread;
        });
        
        scheduler.scheduleAtFixedRate(
            this::updateAllCities,
            pollingIntervalMinutes, // Initial delay
            pollingIntervalMinutes,
            TimeUnit.MINUTES
        );
        
        running = true;
        logger.info("Polling service started with interval: {} minutes", pollingIntervalMinutes);
    }
    
    public void stop() {
        if (!running) {
            return;
        }
        
        logger.info("Stopping polling service");
        running = false;
        
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        logger.info("Polling service stopped");
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public void setPollingInterval(long intervalMinutes) {
        if (running) {
            throw new IllegalStateException("Cannot change interval while polling is running");
        }
        // Would need to restart scheduler with new interval
    }
    
    private void updateAllCities() {
        Set<String> cities = cacheService.getCachedCities();
        
        if (cities.isEmpty()) {
            logger.debug("No cities to update");
            return;
        }
        
        logger.info("Updating weather for {} cities", cities.size());
        
        for (String city : cities) {
            try {
                weatherService.fetchFreshWeather(city);
                logger.debug("Updated weather for city: {}", city);
            } catch (Exception e) {
                // Don't stop polling on error
                logger.error("Error updating weather for city: {}", city, e);
            }
        }
        
        logger.info("Completed updating all cities");
    }
}
```

**Тесты:**
- Start polling service
- Stop polling service
- Update all cities
- Error handling (один город с ошибкой не прерывает обновление других)
- Graceful shutdown
- Тест с AwaitBility для async behavior

---

### 3.6 OpenWeatherMapClient

**Файл:** `OpenWeatherMapClient.java`

**Задачи:**
1. HTTP запросы к OpenWeatherMap API
2. Построение URL с параметрами
3. Парсинг JSON ответа
4. Обработка HTTP ошибок
5. Retry механизм
6. Таймауты

**Псевдокод:**
```java
public class OpenWeatherMapClient {
    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5";
    private static final String CURRENT_WEATHER_ENDPOINT = "/weather";
    
    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;
    private final int maxRetries;
    
    public OpenWeatherMapClient(String apiKey) {
        this.apiKey = apiKey;
        this.maxRetries = 3;
        this.gson = new Gson();
        
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }
    
    public OpenWeatherMapResponse getCurrentWeather(String cityName) throws WeatherSDKException {
        String url = buildUrl(cityName);
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.debug("Fetching weather for city: {} (attempt {}/{})", cityName, attempt, maxRetries);
                String responseBody = executeRequest(url);
                return parseResponse(responseBody);
            } catch (WeatherSDKException e) {
                // Don't retry on client errors (4xx)
                if (e instanceof InvalidApiKeyException || 
                    e instanceof CityNotFoundException ||
                    e instanceof ValidationException) {
                    throw e;
                }
                
                // Retry on server errors (5xx) and network errors
                if (attempt == maxRetries) {
                    throw e;
                }
                
                logger.warn("Request failed, retrying... (attempt {}/{})", attempt, maxRetries);
                sleep(1000 * attempt); // Exponential backoff
            }
        }
        
        throw new ApiUnavailableException("Failed after " + maxRetries + " attempts");
    }
    
    private String buildUrl(String cityName) {
        try {
            String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());
            return String.format("%s%s?q=%s&appid=%s&units=metric",
                API_BASE_URL, CURRENT_WEATHER_ENDPOINT, encodedCity, apiKey);
        } catch (UnsupportedEncodingException e) {
            throw new ValidationException("Invalid city name encoding", e);
        }
    }
    
    private String executeRequest(String url) throws WeatherSDKException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(
                request, 
                HttpResponse.BodyHandlers.ofString()
            );
            
            handleStatusCode(response.statusCode(), response.body());
            return response.body();
            
        } catch (IOException e) {
            throw new ApiUnavailableException("Network error: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiUnavailableException("Request interrupted", e);
        }
    }
    
    private void handleStatusCode(int statusCode, String body) throws WeatherSDKException {
        if (statusCode == 200) {
            return; // OK
        }
        
        switch (statusCode) {
            case 401:
                throw new InvalidApiKeyException("Invalid API key");
            case 404:
                throw new CityNotFoundException("City not found");
            case 429:
                throw new RateLimitException("API rate limit exceeded");
            case 500:
            case 502:
            case 503:
            case 504:
                throw new ApiUnavailableException("API server error: " + statusCode);
            default:
                throw new ApiException("API error: " + statusCode + " - " + body);
        }
    }
    
    private OpenWeatherMapResponse parseResponse(String json) throws WeatherSDKException {
        try {
            return gson.fromJson(json, OpenWeatherMapResponse.class);
        } catch (JsonSyntaxException e) {
            throw new ApiException("Failed to parse API response", e);
        }
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

**Тесты:**
- Успешный запрос (200 OK)
- Invalid API key (401)
- City not found (404)
- Rate limit (429)
- Server error (5xx) с retry
- Network timeout
- JSON parsing
- URL encoding

---

## 4. Конфигурация Maven (pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.kameleoon</groupId>
    <artifactId>weather-sdk</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Weather SDK</name>
    <description>SDK for accessing OpenWeatherMap API</description>
    <url>https://github.com/kameleoon/weather-sdk</url>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        
        <gson.version>2.10.1</gson.version>
        <slf4j.version>2.0.9</slf4j.version>
        <logback.version>1.4.11</logback.version>
        
        <junit.version>5.10.0</junit.version>
        <mockito.version>5.5.0</mockito.version>
        <wiremock.version>3.0.1</wiremock.version>
        <assertj.version>3.24.2</assertj.version>
        <awaitility.version>4.2.0</awaitility.version>
    </properties>

    <dependencies>
        <!-- JSON Processing -->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>${gson.version}</version>
        </dependency>

        <!-- Logging -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>${slf4j.version}</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>${logback.version}</version>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>${mockito.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <version>${mockito.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.wiremock</groupId>
            <artifactId>wiremock</artifactId>
            <version>${wiremock.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <version>${assertj.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.awaitility</groupId>
            <artifactId>awaitility</artifactId>
            <version>${awaitility.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                </configuration>
            </plugin>

            <!-- Surefire Plugin (Unit Tests) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
            </plugin>

            <!-- JaCoCo (Code Coverage) -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.10</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>check</id>
                        <goals>
                            <goal>check</goal>
                        </goals>
                        <configuration>
                            <rules>
                                <rule>
                                    <element>BUNDLE</element>
                                    <limits>
                                        <limit>
                                            <counter>INSTRUCTION</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.80</minimum>
                                        </limit>
                                    </limits>
                                </rule>
                            </rules>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- Checkstyle -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-checkstyle-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <configLocation>google_checks.xml</configLocation>
                    <consoleOutput>true</consoleOutput>
                    <failsOnError>true</failsOnError>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- SpotBugs -->
            <plugin>
                <groupId>com.github.spotbugs</groupId>
                <artifactId>spotbugs-maven-plugin</artifactId>
                <version>4.7.3.5</version>
                <configuration>
                    <effort>Max</effort>
                    <threshold>Low</threshold>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- Javadoc -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>3.5.0</version>
                <configuration>
                    <show>public</show>
                    <nohelp>true</nohelp>
                </configuration>
                <executions>
                    <execution>
                        <id>attach-javadocs</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- Source JAR -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <version>3.3.0</version>
                <executions>
                    <execution>
                        <id>attach-sources</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- Shade Plugin (Fat JAR) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <shadedArtifactAttached>true</shadedArtifactAttached>
                            <shadedClassifierName>all</shadedClassifierName>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 5. Примеры использования (Examples)

### 5.1 OnDemandExample.java

```java
package com.kameleoon.weather.examples;

import com.kameleoon.weather.WeatherSDK;
import com.kameleoon.weather.WeatherSDKFactory;
import com.kameleoon.weather.config.OperationMode;
import com.kameleoon.weather.model.WeatherData;
import com.kameleoon.weather.exception.WeatherSDKException;

public class OnDemandExample {
    public static void main(String[] args) {
        // Get SDK instance in ON_DEMAND mode
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, OperationMode.ON_DEMAND);
        
        try {
            // Get weather for London
            System.out.println("Fetching weather for London...");
            WeatherData weather = sdk.getWeather("London");
            
            printWeather(weather);
            
            // Get weather for London again (should use cache)
            System.out.println("\nFetching weather for London again (from cache)...");
            WeatherData weatherCached = sdk.getWeather("London");
            
            printWeather(weatherCached);
            
            // Force refresh
            System.out.println("\nForce refreshing weather for London...");
            WeatherData weatherRefreshed = sdk.refreshWeather("London");
            
            printWeather(weatherRefreshed);
            
        } catch (WeatherSDKException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            // Clean up
            sdk.shutdown();
            WeatherSDKFactory.removeInstance(apiKey);
        }
    }
    
    private static void printWeather(WeatherData weather) {
        System.out.println("City: " + weather.getName());
        System.out.println("Weather: " + weather.getWeather().getMain() + 
                         " - " + weather.getWeather().getDescription());
        System.out.println("Temperature: " + weather.getTemperature().getTemp() + "°C");
        System.out.println("Feels like: " + weather.getTemperature().getFeelsLike() + "°C");
        System.out.println("Wind speed: " + weather.getWind().getSpeed() + " m/s");
        System.out.println("Visibility: " + weather.getVisibility() + " m");
    }
}
```

### 5.2 PollingExample.java

```java
package com.kameleoon.weather.examples;

import com.kameleoon.weather.WeatherSDK;
import com.kameleoon.weather.WeatherSDKFactory;
import com.kameleoon.weather.config.OperationMode;
import com.kameleoon.weather.model.WeatherData;

public class PollingExample {
    public static void main(String[] args) throws InterruptedException {
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, OperationMode.POLLING);
        
        try {
            // First request - will fetch from API
            System.out.println("First request for Paris:");
            WeatherData paris = sdk.getWeather("Paris");
            System.out.println("Temperature: " + paris.getTemperature().getTemp() + "°C");
            
            // Immediate second request - will use cache (zero latency!)
            long start = System.currentTimeMillis();
            WeatherData parisCached = sdk.getWeather("Paris");
            long duration = System.currentTimeMillis() - start;
            System.out.println("\nSecond request for Paris (from cache):");
            System.out.println("Temperature: " + parisCached.getTemperature().getTemp() + "°C");
            System.out.println("Response time: " + duration + "ms");
            
            // Add more cities
            sdk.getWeather("London");
            sdk.getWeather("Berlin");
            sdk.getWeather("Tokyo");
            
            System.out.println("\nCache info: " + sdk.getCacheInfo());
            
            // Wait for polling to update (in real scenario, polling interval is 10 min)
            System.out.println("\nPolling service will update all cities automatically...");
            System.out.println("(In production, this happens every 10 minutes)");
            
            // Keep running to see polling in action
            Thread.sleep(5000);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sdk.shutdown();
            WeatherSDKFactory.removeInstance(apiKey);
        }
    }
}
```

---

## 6. Тестовая стратегия

### 6.1 Unit Tests

**Покрытие:** 80%+ code coverage

**Тестируемые компоненты:**
- Все классы сервисов
- Все утилиты
- Все модели
- Factory

**Инструменты:**
- JUnit 5
- Mockito
- AssertJ

### 6.2 Integration Tests

**Сценарии:**
- End-to-end тест с WireMock (mock API)
- On-demand mode полный flow
- Polling mode полный flow
- Multi-instance scenario
- Error handling scenarios

**Инструменты:**
- JUnit 5
- WireMock
- AwaitBility (для async tests)

### 6.3 Performance Tests

**Метрики:**
- Response time (on-demand)
- Response time (polling, cached)
- Memory usage
- Thread safety под нагрузкой

---

## 7. CI/CD Pipeline

### 7.1 GitHub Actions Workflow

```yaml
name: CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
    
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Build with Maven
      run: mvn clean install
    
    - name: Run tests
      run: mvn test
    
    - name: Generate coverage report
      run: mvn jacoco:report
    
    - name: Run Checkstyle
      run: mvn checkstyle:check
    
    - name: Run SpotBugs
      run: mvn spotbugs:check
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
```

---

## 8. Timeline и Milestones

### Milestone 1: Foundation (Week 1)
- ✅ Project setup
- ✅ Models
- ✅ Exceptions
- ✅ Basic structure

### Milestone 2: Core Features (Week 2)
- ✅ HTTP Client
- ✅ Cache Service
- ✅ Weather Service
- ✅ Unit tests

### Milestone 3: Advanced Features (Week 2-3)
- ✅ Polling Service
- ✅ SDK Facade
- ✅ Factory
- ✅ Integration tests

### Milestone 4: Polish (Week 3)
- ✅ Documentation
- ✅ Examples
- ✅ Quality checks
- ✅ Performance optimization

### Milestone 5: Release (Week 3-4)
- ✅ Final testing
- ✅ Release preparation
- ✅ Publishing

**Total estimated time: 3-4 weeks**

---

## 9. Дополнительные рекомендации

### 9.1 Best Practices

1. **Code Style**
   - Следовать Google Java Style Guide
   - Использовать Checkstyle для автоматической проверки
   - Code review перед merge

2. **Testing**
   - TDD подход где возможно
   - Минимум 80% code coverage
   - Тестировать edge cases

3. **Documentation**
   - Javadoc для всех публичных API
   - README с примерами
   - Architecture documentation

4. **Git Workflow**
   - Feature branches
   - Pull requests с review
   - Semantic versioning

### 9.2 Потенциальные улучшения (Future)

1. **v1.1:**
   - Batch API requests
   - Custom cache strategies
   - Metrics/monitoring API

2. **v1.2:**
   - Support для других weather APIs
   - Circuit breaker pattern
   - Advanced retry strategies

3. **v2.0:**
   - Reactive API (CompletableFuture)
   - Spring Boot integration
   - Kubernetes-ready

---

**План готов к выполнению! ✅**

