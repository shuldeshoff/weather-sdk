# Weather SDK - API Reference

**Автор:** Шульдешов Юрий Леонидович  
**Telegram:** @shuldeshoff  
**Дата:** 10 ноября 2025  
**Версия:** 1.0

---

## 1. Публичный API

### 1.1 WeatherSDKFactory

Фабрика для создания и управления экземплярами SDK.

#### Методы

##### getInstance

```java
public static WeatherSDK getInstance(String apiKey, OperationMode mode)
```

**Описание:** Получить или создать экземпляр SDK для указанного API ключа.

**Параметры:**
- `apiKey` (String) - API ключ OpenWeatherMap. **Обязательный**.
- `mode` (OperationMode) - Режим работы SDK (ON_DEMAND или POLLING). **Обязательный**.

**Возвращает:** `WeatherSDK` - Экземпляр SDK

**Исключения:**
- `ValidationException` - если API ключ null или пустой
- `ConfigurationException` - если невозможно создать SDK

**Пример:**
```java
String apiKey = "your_api_key_here";
WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, OperationMode.ON_DEMAND);
```

**Особенности:**
- Если экземпляр с данным API ключом уже существует, возвращается существующий
- Thread-safe
- Singleton pattern для каждого уникального API ключа

---

##### removeInstance

```java
public static void removeInstance(String apiKey)
```

**Описание:** Удалить экземпляр SDK для указанного API ключа.

**Параметры:**
- `apiKey` (String) - API ключ для удаления

**Эффекты:**
- Вызывает `shutdown()` на SDK перед удалением
- Освобождает все ресурсы
- Останавливает polling service (если активен)

**Пример:**
```java
WeatherSDKFactory.removeInstance("your_api_key_here");
```

---

##### hasInstance

```java
public static boolean hasInstance(String apiKey)
```

**Описание:** Проверить, существует ли экземпляр SDK для данного API ключа.

**Параметры:**
- `apiKey` (String) - API ключ для проверки

**Возвращает:** `boolean` - true если экземпляр существует

**Пример:**
```java
if (WeatherSDKFactory.hasInstance(apiKey)) {
    System.out.println("Instance exists");
}
```

---

##### getActiveKeys

```java
public static Set<String> getActiveKeys()
```

**Описание:** Получить множество всех активных API ключей.

**Возвращает:** `Set<String>` - Множество активных API ключей

**Пример:**
```java
Set<String> keys = WeatherSDKFactory.getActiveKeys();
System.out.println("Active instances: " + keys.size());
```

---

### 1.2 WeatherSDK

Главный класс SDK для работы с погодными данными.

#### Методы

##### getWeather

```java
public WeatherData getWeather(String cityName) throws WeatherSDKException
```

**Описание:** Получить данные о погоде для указанного города.

**Параметры:**
- `cityName` (String) - Название города. **Обязательный**.

**Возвращает:** `WeatherData` - Данные о погоде

**Исключения:**
- `ValidationException` - если название города некорректно
- `CityNotFoundException` - если город не найден
- `InvalidApiKeyException` - если API ключ недействителен
- `RateLimitException` - если превышен лимит запросов
- `ApiUnavailableException` - если API недоступен
- `WeatherSDKException` - для других ошибок

**Поведение:**
- В режиме ON_DEMAND: проверяет кэш, затем делает запрос к API если нужно
- В режиме POLLING: возвращает данные из кэша (обновляемого автоматически)
- Данные кэшируются на 10 минут
- Возвращается информация о первом найденном городе

**Пример:**
```java
try {
    WeatherData weather = sdk.getWeather("London");
    System.out.println("Temperature: " + weather.getTemperature().getTemp());
} catch (CityNotFoundException e) {
    System.err.println("City not found: " + e.getMessage());
} catch (WeatherSDKException e) {
    System.err.println("Error: " + e.getMessage());
}
```

---

##### refreshWeather

```java
public WeatherData refreshWeather(String cityName) throws WeatherSDKException
```

**Описание:** Принудительно обновить данные о погоде, игнорируя кэш.

**Параметры:**
- `cityName` (String) - Название города. **Обязательный**.

**Возвращает:** `WeatherData` - Свежие данные о погоде

**Исключения:** Те же что и `getWeather()`

**Поведение:**
- Всегда делает запрос к API
- Игнорирует кэш
- Обновляет кэш после успешного запроса

**Пример:**
```java
WeatherData freshWeather = sdk.refreshWeather("London");
```

---

##### clearCache

```java
public void clearCache()
```

**Описание:** Очистить весь кэш погодных данных.

**Эффекты:**
- Удаляет все закэшированные данные
- Следующий запрос для любого города будет обращаться к API

**Пример:**
```java
sdk.clearCache();
```

---

##### getCacheInfo

```java
public CacheInfo getCacheInfo()
```

**Описание:** Получить информацию о текущем состоянии кэша.

**Возвращает:** `CacheInfo` - Информация о кэше

**Пример:**
```java
CacheInfo info = sdk.getCacheInfo();
System.out.println("Cached cities: " + info.getCachedCities());
System.out.println("Cache size: " + info.getCurrentSize() + "/" + info.getMaxSize());
```

---

##### shutdown

```java
public void shutdown()
```

**Описание:** Корректно завершить работу SDK.

**Эффекты:**
- Останавливает polling service (если активен)
- Очищает кэш
- Освобождает ресурсы
- После вызова SDK нельзя использовать

**Пример:**
```java
sdk.shutdown();
```

**Примечание:** Автоматически вызывается при `WeatherSDKFactory.removeInstance()`

---

## 2. Модели данных

### 2.1 WeatherData

Основная модель данных о погоде.

```java
public class WeatherData {
    private Weather weather;
    private Temperature temperature;
    private Integer visibility;
    private Wind wind;
    private Long datetime;
    private Sys sys;
    private Integer timezone;
    private String name;
    
    // Getters
    public Weather getWeather()
    public Temperature getTemperature()
    public Integer getVisibility()
    public Wind getWind()
    public Long getDatetime()
    public Sys getSys()
    public Integer getTimezone()
    public String getName()
}
```

**Поля:**

| Поле | Тип | Описание | Единицы |
|------|-----|----------|---------|
| weather | Weather | Основные погодные условия | - |
| temperature | Temperature | Температурные данные | Celsius (metric) |
| visibility | Integer | Видимость | Метры |
| wind | Wind | Данные о ветре | м/с (metric) |
| datetime | Long | Время измерения | Unix timestamp (UTC) |
| sys | Sys | Системные данные (восход/закат) | Unix timestamp (UTC) |
| timezone | Integer | Часовой пояс | Секунды от UTC |
| name | String | Название города | - |

**JSON Пример:**
```json
{
  "weather": {
    "main": "Clouds",
    "description": "scattered clouds"
  },
  "temperature": {
    "temp": 15.5,
    "feels_like": 14.2
  },
  "visibility": 10000,
  "wind": {
    "speed": 3.5
  },
  "datetime": 1699632000,
  "sys": {
    "sunrise": 1699594800,
    "sunset": 1699632000
  },
  "timezone": 3600,
  "name": "London"
}
```

---

### 2.2 Weather

Погодные условия.

```java
public class Weather {
    private String main;
    private String description;
    
    public String getMain()
    public String getDescription()
}
```

**Поля:**

| Поле | Тип | Описание | Примеры |
|------|-----|----------|---------|
| main | String | Группа погодных условий | "Clear", "Clouds", "Rain", "Snow" |
| description | String | Детальное описание | "clear sky", "scattered clouds" |

---

### 2.3 Temperature

Температурные данные.

```java
public class Temperature {
    private Double temp;
    private Double feelsLike;
    
    public Double getTemp()
    public Double getFeelsLike()
}
```

**Поля:**

| Поле | Тип | Описание | Единицы |
|------|-----|----------|---------|
| temp | Double | Фактическая температура | °C |
| feelsLike | Double | Ощущаемая температура | °C |

---

### 2.4 Wind

Данные о ветре.

```java
public class Wind {
    private Double speed;
    
    public Double getSpeed()
}
```

**Поля:**

| Поле | Тип | Описание | Единицы |
|------|-----|----------|---------|
| speed | Double | Скорость ветра | м/с |

---

### 2.5 Sys

Системные данные.

```java
public class Sys {
    private Long sunrise;
    private Long sunset;
    
    public Long getSunrise()
    public Long getSunset()
}
```

**Поля:**

| Поле | Тип | Описание | Единицы |
|------|-----|----------|---------|
| sunrise | Long | Время восхода солнца | Unix timestamp (UTC) |
| sunset | Long | Время заката солнца | Unix timestamp (UTC) |

---

### 2.6 CacheInfo

Информация о состоянии кэша.

```java
public class CacheInfo {
    private Set<String> cachedCities;
    private int currentSize;
    private int maxSize;
    
    public Set<String> getCachedCities()
    public int getCurrentSize()
    public int getMaxSize()
}
```

**Поля:**

| Поле | Тип | Описание |
|------|-----|----------|
| cachedCities | Set<String> | Множество названий закэшированных городов |
| currentSize | int | Текущий размер кэша |
| maxSize | int | Максимальный размер кэша (10) |

---

## 3. Конфигурация

### 3.1 OperationMode (Enum)

Режим работы SDK.

```java
public enum OperationMode {
    ON_DEMAND,  // Обновление только по запросу
    POLLING     // Автоматическое периодическое обновление
}
```

**Значения:**

| Режим | Описание | Use Case |
|-------|----------|----------|
| ON_DEMAND | SDK обновляет данные только когда клиент вызывает `getWeather()` | Нечастые запросы, экономия ресурсов |
| POLLING | SDK автоматически обновляет данные каждые 10 минут для всех закэшированных городов | Частые запросы, zero-latency response |

**Пример:**
```java
// On-Demand режим
WeatherSDK sdk1 = WeatherSDKFactory.getInstance(apiKey, OperationMode.ON_DEMAND);

// Polling режим
WeatherSDK sdk2 = WeatherSDKFactory.getInstance(apiKey, OperationMode.POLLING);
```

---

### 3.2 SDKConfig

Конфигурация SDK (внутренний класс, значения по умолчанию).

| Параметр | Значение по умолчанию | Описание |
|----------|----------------------|----------|
| CACHE_MAX_SIZE | 10 | Максимальное количество городов в кэше |
| CACHE_TTL_MINUTES | 10 | Time-to-live для кэша (минуты) |
| POLLING_INTERVAL_MINUTES | 10 | Интервал опроса в polling режиме |
| CONNECTION_TIMEOUT_MS | 10000 | HTTP connection timeout |
| READ_TIMEOUT_MS | 10000 | HTTP read timeout |
| MAX_RETRIES | 3 | Максимальное количество повторных попыток |
| API_BASE_URL | https://api.openweathermap.org/data/2.5 | Base URL API |

---

## 4. Исключения

### 4.1 Иерархия исключений

```
RuntimeException
    └── WeatherSDKException (base)
            ├── ApiException
            │   ├── InvalidApiKeyException
            │   ├── RateLimitException
            │   ├── CityNotFoundException
            │   └── ApiUnavailableException
            ├── CacheException
            ├── ValidationException
            └── ConfigurationException
```

### 4.2 WeatherSDKException

Базовое исключение SDK.

```java
public class WeatherSDKException extends RuntimeException {
    public WeatherSDKException(String message)
    public WeatherSDKException(String message, Throwable cause)
}
```

**Описание:** Родительский класс для всех исключений SDK.

---

### 4.3 InvalidApiKeyException

```java
public class InvalidApiKeyException extends ApiException
```

**Когда выбрасывается:**
- API ключ недействителен
- API ключ не активирован
- HTTP 401 ответ от API

**Пример обработки:**
```java
try {
    WeatherData weather = sdk.getWeather("London");
} catch (InvalidApiKeyException e) {
    System.err.println("Please check your API key: " + e.getMessage());
}
```

---

### 4.4 CityNotFoundException

```java
public class CityNotFoundException extends ApiException
```

**Когда выбрасывается:**
- Город не найден в базе OpenWeatherMap
- HTTP 404 ответ от API

**Пример обработки:**
```java
try {
    WeatherData weather = sdk.getWeather("NonExistentCity123");
} catch (CityNotFoundException e) {
    System.err.println("City not found: " + e.getMessage());
    // Предложить альтернативы пользователю
}
```

---

### 4.5 RateLimitException

```java
public class RateLimitException extends ApiException
```

**Когда выбрасывается:**
- Превышен лимит запросов к API
- HTTP 429 ответ от API

**Пример обработки:**
```java
try {
    WeatherData weather = sdk.getWeather("London");
} catch (RateLimitException e) {
    System.err.println("Rate limit exceeded. Please wait.");
    // Подождать перед повторной попыткой
}
```

---

### 4.6 ApiUnavailableException

```java
public class ApiUnavailableException extends ApiException
```

**Когда выбрасывается:**
- API сервер недоступен
- Network timeout
- HTTP 5xx ответы от API

**Пример обработки:**
```java
try {
    WeatherData weather = sdk.getWeather("London");
} catch (ApiUnavailableException e) {
    System.err.println("API temporarily unavailable: " + e.getMessage());
    // Использовать fallback или retry позже
}
```

---

### 4.7 ValidationException

```java
public class ValidationException extends WeatherSDKException
```

**Когда выбрасывается:**
- Некорректные входные параметры
- Пустое название города
- Недопустимые символы в названии

**Пример обработки:**
```java
try {
    WeatherData weather = sdk.getWeather("");
} catch (ValidationException e) {
    System.err.println("Invalid input: " + e.getMessage());
}
```

---

## 5. Примеры использования

### 5.1 Базовое использование (On-Demand)

```java
import com.kameleoon.weather.WeatherSDK;
import com.kameleoon.weather.WeatherSDKFactory;
import com.kameleoon.weather.config.OperationMode;
import com.kameleoon.weather.model.WeatherData;

public class BasicExample {
    public static void main(String[] args) {
        String apiKey = "your_api_key_here";
        WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, OperationMode.ON_DEMAND);
        
        try {
            WeatherData weather = sdk.getWeather("London");
            System.out.println("Temperature in " + weather.getName() + ": " + 
                             weather.getTemperature().getTemp() + "°C");
        } catch (WeatherSDKException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            sdk.shutdown();
            WeatherSDKFactory.removeInstance(apiKey);
        }
    }
}
```

---

### 5.2 Polling режим

```java
public class PollingExample {
    public static void main(String[] args) {
        String apiKey = "your_api_key_here";
        WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, OperationMode.POLLING);
        
        try {
            // Первый запрос - получит из API и начнет polling
            WeatherData weather1 = sdk.getWeather("Paris");
            
            // Последующие запросы - мгновенно из кэша
            WeatherData weather2 = sdk.getWeather("Paris"); // < 10ms
            
            // Добавляем больше городов для polling
            sdk.getWeather("London");
            sdk.getWeather("Berlin");
            
            // Все эти города будут автоматически обновляться каждые 10 минут
            
        } catch (WeatherSDKException e) {
            e.printStackTrace();
        } finally {
            sdk.shutdown();
            WeatherSDKFactory.removeInstance(apiKey);
        }
    }
}
```

---

### 5.3 Работа с несколькими API ключами

```java
public class MultipleInstancesExample {
    public static void main(String[] args) {
        String apiKey1 = "key_for_project_A";
        String apiKey2 = "key_for_project_B";
        
        // Создаем два независимых экземпляра
        WeatherSDK sdk1 = WeatherSDKFactory.getInstance(apiKey1, OperationMode.ON_DEMAND);
        WeatherSDK sdk2 = WeatherSDKFactory.getInstance(apiKey2, OperationMode.POLLING);
        
        try {
            // Каждый SDK работает независимо
            WeatherData weather1 = sdk1.getWeather("London");
            WeatherData weather2 = sdk2.getWeather("Paris");
            
            // Попытка создать дубликат вернет существующий экземпляр
            WeatherSDK sdk1Duplicate = WeatherSDKFactory.getInstance(
                apiKey1, 
                OperationMode.POLLING // режим игнорируется
            );
            System.out.println("Same instance: " + (sdk1 == sdk1Duplicate)); // true
            
        } catch (WeatherSDKException e) {
            e.printStackTrace();
        } finally {
            // Удаляем оба экземпляра
            WeatherSDKFactory.removeInstance(apiKey1);
            WeatherSDKFactory.removeInstance(apiKey2);
        }
    }
}
```

---

### 5.4 Обработка ошибок

```java
public class ErrorHandlingExample {
    public static void main(String[] args) {
        String apiKey = "your_api_key_here";
        WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, OperationMode.ON_DEMAND);
        
        try {
            WeatherData weather = sdk.getWeather("London");
            displayWeather(weather);
            
        } catch (InvalidApiKeyException e) {
            System.err.println("❌ Invalid API key. Please check your configuration.");
            
        } catch (CityNotFoundException e) {
            System.err.println("❌ City not found. Please check the spelling.");
            
        } catch (RateLimitException e) {
            System.err.println("❌ Rate limit exceeded. Please wait before retrying.");
            // Можно реализовать exponential backoff
            
        } catch (ApiUnavailableException e) {
            System.err.println("❌ API is temporarily unavailable. Please try again later.");
            // Можно использовать fallback данные
            
        } catch (ValidationException e) {
            System.err.println("❌ Invalid input: " + e.getMessage());
            
        } catch (WeatherSDKException e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            sdk.shutdown();
            WeatherSDKFactory.removeInstance(apiKey);
        }
    }
    
    private static void displayWeather(WeatherData weather) {
        System.out.println("=== Weather in " + weather.getName() + " ===");
        System.out.println("Conditions: " + weather.getWeather().getMain());
        System.out.println("Description: " + weather.getWeather().getDescription());
        System.out.println("Temperature: " + weather.getTemperature().getTemp() + "°C");
        System.out.println("Feels like: " + weather.getTemperature().getFeelsLike() + "°C");
        System.out.println("Wind speed: " + weather.getWind().getSpeed() + " m/s");
        System.out.println("Visibility: " + weather.getVisibility() + " m");
    }
}
```

---

### 5.5 Управление кэшем

```java
public class CacheManagementExample {
    public static void main(String[] args) {
        String apiKey = "your_api_key_here";
        WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, OperationMode.ON_DEMAND);
        
        try {
            // Добавляем города в кэш
            sdk.getWeather("London");
            sdk.getWeather("Paris");
            sdk.getWeather("Berlin");
            
            // Проверяем состояние кэша
            CacheInfo info = sdk.getCacheInfo();
            System.out.println("Cached cities: " + info.getCachedCities());
            System.out.println("Cache size: " + info.getCurrentSize() + "/" + info.getMaxSize());
            
            // Принудительное обновление (игнорирует кэш)
            WeatherData freshWeather = sdk.refreshWeather("London");
            
            // Очистка кэша
            sdk.clearCache();
            System.out.println("Cache cleared!");
            
        } catch (WeatherSDKException e) {
            e.printStackTrace();
        } finally {
            sdk.shutdown();
            WeatherSDKFactory.removeInstance(apiKey);
        }
    }
}
```

---

## 6. Best Practices

### 6.1 Управление ресурсами

```java
// ✅ ПРАВИЛЬНО: всегда закрывайте SDK
WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, mode);
try {
    // использование SDK
} finally {
    sdk.shutdown();
    WeatherSDKFactory.removeInstance(apiKey);
}

// ❌ НЕПРАВИЛЬНО: утечка ресурсов
WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, mode);
sdk.getWeather("London");
// забыли shutdown - polling продолжает работать!
```

---

### 6.2 Выбор режима

```java
// ✅ ON_DEMAND для редких запросов
if (requestsPerHour < 6) {
    sdk = WeatherSDKFactory.getInstance(apiKey, OperationMode.ON_DEMAND);
}

// ✅ POLLING для частых запросов
if (requestsPerHour >= 6) {
    sdk = WeatherSDKFactory.getInstance(apiKey, OperationMode.POLLING);
}
```

---

### 6.3 Обработка ошибок

```java
// ✅ ПРАВИЛЬНО: обрабатывайте специфичные исключения
try {
    weather = sdk.getWeather(cityName);
} catch (CityNotFoundException e) {
    // Специфичная обработка для city not found
} catch (RateLimitException e) {
    // Специфичная обработка для rate limit
} catch (WeatherSDKException e) {
    // Общая обработка для остальных
}

// ❌ НЕПРАВИЛЬНО: игнорирование ошибок
try {
    weather = sdk.getWeather(cityName);
} catch (Exception e) {
    // Слишком общая обработка
}
```

---

### 6.4 Thread Safety

```java
// ✅ ПРАВИЛЬНО: SDK thread-safe, можно использовать из нескольких потоков
WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, mode);

ExecutorService executor = Executors.newFixedThreadPool(10);
for (String city : cities) {
    executor.submit(() -> {
        try {
            WeatherData weather = sdk.getWeather(city);
            // обработка
        } catch (WeatherSDKException e) {
            // обработка ошибок
        }
    });
}
```

---

## 7. Ограничения и квоты

### 7.1 OpenWeatherMap API Limits

**Free Tier:**
- 60 calls/minute
- 1,000,000 calls/month

**Рекомендации:**
- В POLLING режиме с 10 городами: 6 calls/hour = 144 calls/day
- Оставляет запас для on-demand запросов
- Мониторьте RateLimitException

### 7.2 SDK Limits

| Параметр | Ограничение |
|----------|-------------|
| Максимум городов в кэше | 10 |
| TTL кэша | 10 минут |
| Минимальный интервал polling | 10 минут |
| Максимальная длина имени города | 200 символов |

---

## 8. Дополнительная информация

### 8.1 Версионирование

SDK следует Semantic Versioning (SemVer):
- **MAJOR** - breaking changes в API
- **MINOR** - новая функциональность (обратно совместимая)
- **PATCH** - bug fixes

### 8.2 Логирование

SDK использует SLF4J для логирования. Чтобы увидеть логи:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.11</version>
</dependency>
```

```xml
<!-- logback.xml -->
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="com.kameleoon.weather" level="DEBUG"/>
    
    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

---

**API Reference готов! ✅**

