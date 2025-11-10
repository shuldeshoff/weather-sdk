# Weather SDK API Reference

Краткая справочная документация по публичному API Weather SDK.

---

## Основные классы

### WeatherSDK

Главный фасад для работы с SDK.

```java
public class WeatherSDK {
    // Конструктор
    public WeatherSDK(SDKConfig config)
    
    // Получить погоду
    public WeatherData getWeather(String city)
    
    // Polling mode: регистрация локаций
    public void registerLocation(String city)
    public void unregisterLocation(String city)
    public Set<String> getRegisteredLocations()
    public void refreshAll()
    
    // Управление кэшем
    public CacheInfo getCacheInfo()
    public void clearCache()
    
    // Завершение работы
    public void shutdown()
}
```

### SDKConfig

Конфигурация SDK (Builder pattern).

```java
public record SDKConfig(...) {
    // Builder
    public static Builder builder(String apiKey)
    
    public static class Builder {
        public Builder operationMode(OperationMode mode)
        public Builder cacheMaxSize(int size)         // default: 100
        public Builder cacheTtlMinutes(int minutes)   // default: 10
        public Builder pollingIntervalMinutes(int minutes) // default: 5
        public Builder maxRetries(int retries)        // default: 3
        public SDKConfig build()
    }
}
```

### WeatherSDKFactory

Multiton для управления множественными экземплярами.

```java
public class WeatherSDKFactory {
    public static WeatherSDK getInstance(String apiKey, SDKConfig config)
    public static WeatherSDK getInstance(String apiKey)
    public static void shutdown(String apiKey)
    public static void shutdownAll()
    public static int getInstanceCount()
}
```

---

## Data Models (Records)

### WeatherData

```java
public record WeatherData(
    Weather weather,
    Temperature temperature,
    Integer visibility,
    Wind wind,
    Long datetime,
    Sys sys,
    Integer timezone,
    String name
) { }
```

### Temperature

```java
public record Temperature(
    Double temp,        // °C
    Double feelsLike    // °C
) { }
```

### Weather

```java
public record Weather(
    String main,        // "Rain", "Clouds"
    String description  // "light rain"
) { }
```

### Wind

```java
public record Wind(
    Double speed        // m/s
) { }
```

### Sys

```java
public record Sys(
    Long sunrise,       // Unix timestamp
    Long sunset         // Unix timestamp
) { }
```

### CacheInfo

```java
public record CacheInfo(
    Set<String> cachedCities,
    int currentSize,
    int maxSize
) {
    public boolean isFull()
    public boolean isEmpty()
    public double getUtilization()  // 0.0-1.0
}
```

---

## Enums

### OperationMode

```java
public enum OperationMode {
    ON_DEMAND,  // Получение по запросу
    POLLING     // Автоматические обновления
}
```

---

## Exceptions

Все исключения наследуются от `WeatherSDKException`.

```
WeatherSDKException
├── ApiException
│   ├── InvalidApiKeyException (401)
│   ├── CityNotFoundException (404)
│   ├── RateLimitException (429)
│   └── ApiUnavailableException (5xx)
├── ValidationException
├── CacheException
└── ConfigurationException
```

### Обработка

```java
try {
    WeatherData weather = sdk.getWeather("London");
} catch (InvalidApiKeyException e) {
    // Неверный API ключ
} catch (CityNotFoundException e) {
    // Город не найден
    String city = e.getCityName();
} catch (RateLimitException e) {
    // Превышен rate limit - подождать
} catch (ApiUnavailableException e) {
    // API недоступен - повторить позже
} catch (ValidationException e) {
    // Невалидные данные
} catch (WeatherSDKException e) {
    // Общая ошибка SDK
}
```

---

## Примеры использования

### ON_DEMAND режим

```java
SDKConfig config = SDKConfig.builder("api-key")
    .operationMode(OperationMode.ON_DEMAND)
    .cacheMaxSize(100)
    .cacheTtlMinutes(10)
    .build();

WeatherSDK sdk = new WeatherSDK(config);

try {
    WeatherData weather = sdk.getWeather("London");
    System.out.println(weather.temperature().temp() + "°C");
} finally {
    sdk.shutdown();
}
```

### POLLING режим

```java
SDKConfig config = SDKConfig.builder("api-key")
    .operationMode(OperationMode.POLLING)
    .pollingIntervalMinutes(5)
    .build();

WeatherSDK sdk = new WeatherSDK(config);

try {
    sdk.registerLocation("London");
    sdk.registerLocation("Paris");
    
    Thread.sleep(2000); // Дождаться первого обновления
    
    WeatherData weather = sdk.getWeather("London");
    System.out.println(weather.temperature().temp() + "°C");
} finally {
    sdk.shutdown();
}
```

### Множественные API ключи

```java
SDKConfig config1 = SDKConfig.builder("key1").build();
SDKConfig config2 = SDKConfig.builder("key2").build();

WeatherSDK sdk1 = WeatherSDKFactory.getInstance("key1", config1);
WeatherSDK sdk2 = WeatherSDKFactory.getInstance("key2", config2);

// Использование...

WeatherSDKFactory.shutdownAll();
```

---

## Defaults

| Параметр | Значение по умолчанию |
|----------|----------------------|
| Cache max size | 100 |
| Cache TTL | 10 минут |
| Polling interval | 5 минут |
| Max retries | 3 |
| Operation mode | ON_DEMAND |

---

## API Limits

### OpenWeatherMap Free Plan
- **Вызовов в минуту**: 60
- **Вызовов в месяц**: 1,000,000

### SDK Cache
- **Максимальный размер**: Настраивается (default: 100)
- **TTL**: Настраивается (default: 10 минут)
- **Алгоритм**: LRU (Least Recently Used)

---

## Thread Safety

Все публичные методы SDK являются **thread-safe** и могут безопасно использоваться из множественных потоков.

---

## Logging

SDK использует SLF4J для логирования. Настройте logback.xml для управления уровнями:

```xml
<logger name="com.kameleoon.weather" level="INFO"/>
```

Доступные уровни:
- **DEBUG**: Детальная информация (cache hits/misses, API calls)
- **INFO**: Важные события (SDK start/stop, polling)
- **WARN**: Предупреждения (retries, expired cache)
- **ERROR**: Ошибки (API failures, exceptions)

---

## Best Practices

1. **Всегда вызывайте shutdown()** при завершении работы с SDK
2. **Используйте try-finally** или try-with-resources
3. **Переиспользуйте SDK instance** вместо создания новых
4. **Обрабатывайте специфичные исключения** для разной логики
5. **Мониторьте CacheInfo** для оптимизации размера кэша
6. **В POLLING режиме** используйте достаточный интервал (не менее 5 минут)

---

**Автор:** Шульдешов Юрий Леонидович  
**Telegram:** [@shuldeshoff](https://t.me/shuldeshoff)  
**Версия:** 1.0.0

