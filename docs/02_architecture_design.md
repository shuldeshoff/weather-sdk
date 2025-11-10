# Архитектура Weather SDK на Java

## Дата: 10 ноября 2025
## Версия: 1.0

---

## 1. Общая архитектура

### 1.1 Архитектурный стиль

**Выбран:** Layered Architecture (Слоистая архитектура)

```
┌─────────────────────────────────────────────┐
│         Client Application Layer            │
│   (Приложения, использующие SDK)            │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         Public API Layer                    │
│   - WeatherSDK (Facade)                     │
│   - WeatherSDKFactory                       │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         Service Layer                       │
│   - WeatherService                          │
│   - CacheService                            │
│   - PollingService                          │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         Integration Layer                   │
│   - OpenWeatherMapClient                    │
│   - HttpClient wrapper                      │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         Data Layer                          │
│   - Models (DTO, Entities)                  │
│   - Cache Storage                           │
└─────────────────────────────────────────────┘
```

### 1.2 Принципы проектирования

1. **SOLID Principles**
   - Single Responsibility: каждый класс - одна ответственность
   - Open/Closed: открыт для расширения, закрыт для модификации
   - Liskov Substitution: возможность замены реализаций
   - Interface Segregation: специфичные интерфейсы
   - Dependency Inversion: зависимость от абстракций

2. **Clean Architecture**
   - Независимость от фреймворков
   - Тестируемость
   - Независимость от UI
   - Независимость от БД
   - Независимость от внешних сервисов

3. **Design Patterns**
   - Singleton/Multiton (управление экземплярами)
   - Facade (упрощение API)
   - Strategy (режимы работы)
   - Builder (создание объектов)
   - Observer (polling updates)

---

## 2. Структура компонентов

### 2.1 Диаграмма классов (UML)

```
┌─────────────────────────────────────────────┐
│        <<Singleton Registry>>               │
│        WeatherSDKFactory                    │
├─────────────────────────────────────────────┤
│ - instances: Map<String, WeatherSDK>        │
├─────────────────────────────────────────────┤
│ + getInstance(apiKey, mode): WeatherSDK     │
│ + removeInstance(apiKey): void              │
│ + hasInstance(apiKey): boolean              │
└─────────────────────────────────────────────┘
                    │
                    │ creates
                    ↓
┌─────────────────────────────────────────────┐
│           <<Facade>>                        │
│           WeatherSDK                        │
├─────────────────────────────────────────────┤
│ - apiKey: String                            │
│ - mode: OperationMode                       │
│ - weatherService: WeatherService            │
│ - cacheService: CacheService                │
│ - pollingService: PollingService            │
├─────────────────────────────────────────────┤
│ + getWeather(cityName): WeatherData         │
│ + refreshWeather(cityName): WeatherData     │
│ + clearCache(): void                        │
│ + shutdown(): void                          │
└─────────────────────────────────────────────┘
         │           │           │
         │           │           │
    ┌────┘      ┌────┘      └────┐
    ↓           ↓                 ↓
┌─────────┐ ┌──────────┐ ┌───────────────┐
│Weather  │ │ Cache    │ │  Polling      │
│Service  │ │ Service  │ │  Service      │
└─────────┘ └──────────┘ └───────────────┘
    │
    ↓
┌──────────────────────┐
│ OpenWeatherMapClient │
└──────────────────────┘
```

### 2.2 Описание ключевых компонентов

#### 2.2.1 WeatherSDKFactory

**Ответственность:** Управление экземплярами SDK

**Методы:**
```java
public class WeatherSDKFactory {
    // Получить или создать экземпляр SDK
    public static WeatherSDK getInstance(String apiKey, OperationMode mode)
    
    // Удалить экземпляр SDK
    public static void removeInstance(String apiKey)
    
    // Проверить существование экземпляра
    public static boolean hasInstance(String apiKey)
    
    // Получить все активные ключи
    public static Set<String> getActiveKeys()
}
```

**Особенности:**
- Thread-safe реализация
- Использует ConcurrentHashMap для хранения
- Автоматически вызывает shutdown при удалении

#### 2.2.2 WeatherSDK (Facade)

**Ответственность:** Главный интерфейс для клиентов

**Методы:**
```java
public class WeatherSDK {
    // Получить погоду для города
    public WeatherData getWeather(String cityName) throws WeatherSDKException
    
    // Принудительно обновить данные
    public WeatherData refreshWeather(String cityName) throws WeatherSDKException
    
    // Очистить весь кэш
    public void clearCache()
    
    // Получить информацию о кэше
    public CacheInfo getCacheInfo()
    
    // Завершить работу SDK
    public void shutdown()
}
```

**Особенности:**
- Скрывает сложность внутренней реализации
- Единая точка входа для всех операций
- Координирует работу сервисов

#### 2.2.3 WeatherService

**Ответственность:** Бизнес-логика получения погоды

**Методы:**
```java
public class WeatherService {
    // Получить погоду (с учетом кэша)
    public WeatherData getWeather(String cityName)
    
    // Получить погоду (игнорируя кэш)
    public WeatherData fetchFreshWeather(String cityName)
    
    // Валидация имени города
    private void validateCityName(String cityName)
    
    // Преобразование ответа API в WeatherData
    private WeatherData mapToWeatherData(ApiResponse response)
}
```

**Зависимости:**
- OpenWeatherMapClient (API клиент)
- CacheService (кэширование)

#### 2.2.4 CacheService

**Ответственность:** Управление кэшированием данных

**Методы:**
```java
public class CacheService {
    // Получить из кэша
    public Optional<WeatherData> get(String cityName)
    
    // Сохранить в кэш
    public void put(String cityName, WeatherData data)
    
    // Проверить актуальность
    public boolean isValid(String cityName)
    
    // Очистить кэш
    public void clear()
    
    // Получить все закэшированные города
    public Set<String> getCachedCities()
    
    // Удалить конкретный город
    public void evict(String cityName)
}
```

**Реализация:**
- LinkedHashMap с LRU eviction
- TTL проверка на каждое обращение
- Thread-safe операции

#### 2.2.5 PollingService

**Ответственность:** Периодическое обновление данных

**Методы:**
```java
public class PollingService {
    // Запустить polling
    public void start()
    
    // Остановить polling
    public void stop()
    
    // Проверить статус
    public boolean isRunning()
    
    // Установить интервал опроса
    public void setPollingInterval(long intervalMs)
    
    // Внутренний метод обновления
    private void updateAllCities()
}
```

**Реализация:**
- ScheduledExecutorService для периодических задач
- Обновляет все города из кэша
- Graceful shutdown

#### 2.2.6 OpenWeatherMapClient

**Ответственность:** HTTP взаимодействие с API

**Методы:**
```java
public class OpenWeatherMapClient {
    // Запрос погоды по имени города
    public ApiResponse getCurrentWeather(String cityName)
    
    // Построение URL
    private String buildUrl(String cityName)
    
    // Выполнение HTTP запроса
    private String executeRequest(String url)
    
    // Парсинг JSON ответа
    private ApiResponse parseResponse(String json)
    
    // Обработка ошибок API
    private void handleApiError(int statusCode, String body)
}
```

**Особенности:**
- Использует Java HttpClient (встроенный с Java 11)
- Retry механизм для transient errors
- Таймауты на запросы

---

## 3. Модель данных

### 3.1 Классы данных (DTOs)

#### WeatherData (SDK Response)

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
    
    // Getters, constructors, toString, equals, hashCode
}
```

#### Weather

```java
public class Weather {
    private String main;
    private String description;
}
```

#### Temperature

```java
public class Temperature {
    private Double temp;
    private Double feelsLike;
}
```

#### Wind

```java
public class Wind {
    private Double speed;
}
```

#### Sys

```java
public class Sys {
    private Long sunrise;
    private Long sunset;
}
```

### 3.2 Внутренние структуры

#### CacheEntry

```java
class CacheEntry {
    private WeatherData data;
    private long timestamp;
    private long accessCount;
    
    public boolean isExpired(long ttlMillis) {
        return System.currentTimeMillis() - timestamp > ttlMillis;
    }
}
```

#### ApiResponse (от OpenWeatherMap)

```java
class ApiResponse {
    // Полная структура ответа API
    // Мапится в WeatherData
}
```

---

## 4. Обработка ошибок

### 4.1 Иерархия исключений

```
Exception
    └── RuntimeException
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

### 4.2 Обработка ошибок по слоям

**Public API Layer:**
- Валидация входных параметров
- Преобразование внутренних исключений в публичные
- Логирование

**Service Layer:**
- Бизнес-логика обработки ошибок
- Retry логика
- Fallback на кэш при ошибках API

**Integration Layer:**
- HTTP ошибки → специфичные исключения
- Парсинг ошибок API
- Таймауты и network errors

---

## 5. Конфигурация

### 5.1 Параметры SDK

```java
public class SDKConfig {
    // API Configuration
    private String apiKey;
    private OperationMode mode;
    
    // Cache Configuration
    private int cacheMaxSize = 10;
    private long cacheTtlMinutes = 10;
    
    // Polling Configuration
    private long pollingIntervalMinutes = 10;
    
    // HTTP Configuration
    private int connectionTimeout = 10000; // ms
    private int readTimeout = 10000; // ms
    private int maxRetries = 3;
    
    // API Configuration
    private String apiBaseUrl = "https://api.openweathermap.org/data/2.5";
    private String apiVersion = "2.5";
}
```

### 5.2 Builder Pattern для конфигурации

```java
WeatherSDK sdk = WeatherSDKFactory.getInstance()
    .withApiKey("your_api_key")
    .withMode(OperationMode.POLLING)
    .withCacheSize(15)
    .withCacheTtl(Duration.ofMinutes(5))
    .withPollingInterval(Duration.ofMinutes(5))
    .build();
```

---

## 6. Многопоточность

### 6.1 Thread Safety

**Требования:**
- SDK должен быть thread-safe
- Множественные клиенты могут использовать SDK одновременно
- Polling service работает в отдельном потоке

**Решения:**

1. **CacheService**
   - Используется Collections.synchronizedMap()
   - Или ConcurrentHashMap
   - Атомарные операции для LRU

2. **PollingService**
   - ScheduledExecutorService с single thread
   - Synchronized доступ к списку городов
   - Graceful shutdown с awaitTermination

3. **WeatherSDKFactory**
   - ConcurrentHashMap для хранения экземпляров
   - Double-checked locking при создании

4. **HttpClient**
   - Java HttpClient thread-safe по умолчанию
   - Connection pooling

### 6.2 Диаграмма потоков (Polling Mode)

```
Main Thread                 Polling Thread
     │                            │
     │ getWeather()               │
     ├──────────────────┐         │
     │  check cache     │         │
     │  return if valid │         │
     └──────────────────┘         │
     │                            │
     │                       ┌────┴────┐
     │                       │ Timer   │
     │                       │ triggers│
     │                       └────┬────┘
     │                            │
     │                       ┌────▼──────────┐
     │                       │ updateAll()   │
     │    ◄──────────────────┤ - fetch API   │
     │    cache updated      │ - update cache│
     │                       └───────────────┘
     │                            │
```

---

## 7. Диаграммы последовательности

### 7.1 Создание SDK (Multiton Pattern)

```
Client          Factory         WeatherSDK      Services
  │                │                │              │
  │ getInstance()  │                │              │
  ├───────────────>│                │              │
  │                │ check map      │              │
  │                ├──────────┐     │              │
  │                │          │     │              │
  │                │<─────────┘     │              │
  │                │                │              │
  │                │ new WeatherSDK()│             │
  │                ├───────────────>│              │
  │                │                │ init services│
  │                │                ├─────────────>│
  │                │                │              │
  │                │ store in map   │              │
  │                ├──────────┐     │              │
  │                │<─────────┘     │              │
  │                │                │              │
  │<───────────────┤                │              │
  │ return SDK     │                │              │
```

### 7.2 Получение погоды (On-Demand)

```
Client    WeatherSDK  WeatherService  CacheService  APIClient
  │           │            │               │           │
  │getWeather()│           │               │           │
  ├──────────>│            │               │           │
  │           │ getWeather()               │           │
  │           ├───────────>│               │           │
  │           │            │ get(city)     │           │
  │           │            ├──────────────>│           │
  │           │            │               │           │
  │           │            │ check TTL     │           │
  │           │            │<──────────────┤           │
  │           │            │ expired/miss  │           │
  │           │            │               │           │
  │           │            │ fetchWeather()│           │
  │           │            ├──────────────────────────>│
  │           │            │               │ HTTP GET  │
  │           │            │               │           │
  │           │            │               │ response  │
  │           │            │<──────────────────────────┤
  │           │            │               │           │
  │           │            │ put(city, data)           │
  │           │            ├──────────────>│           │
  │           │            │               │           │
  │           │<───────────┤               │           │
  │           │ WeatherData                │           │
  │<──────────┤            │               │           │
```

### 7.3 Polling Mode Update

```
PollingService  CacheService  WeatherService  APIClient
      │              │              │             │
      │ timer tick   │              │             │
      ├──────────┐   │              │             │
      │          │   │              │             │
      │<─────────┘   │              │             │
      │              │              │             │
      │ getCities()  │              │             │
      ├─────────────>│              │             │
      │              │              │             │
      │ [city1,...]  │              │             │
      │<─────────────┤              │             │
      │              │              │             │
      │ for each city│              │             │
      ├──────────────┼─────────────>│             │
      │              │              │ fetch API   │
      │              │              ├────────────>│
      │              │              │             │
      │              │              │ response    │
      │              │              │<────────────┤
      │              │ update cache │             │
      │              │<─────────────┤             │
      │              │              │             │
```

---

## 8. Технологии и библиотеки

### 8.1 Core Dependencies

| Библиотека | Версия | Назначение |
|-----------|--------|-----------|
| Java JDK | 17 (LTS) | Основная платформа |
| Gson | 2.10.1 | JSON сериализация |
| SLF4J | 2.0.9 | Logging facade |
| Logback | 1.4.11 | Logging implementation |

### 8.2 Test Dependencies

| Библиотека | Версия | Назначение |
|-----------|--------|-----------|
| JUnit | 5.10.0 | Unit testing framework |
| Mockito | 5.5.0 | Mocking framework |
| WireMock | 3.0.1 | HTTP mocking |
| AssertJ | 3.24.2 | Fluent assertions |
| AwaitBility | 4.2.0 | Async testing |

### 8.3 Build Tools

| Инструмент | Назначение |
|-----------|-----------|
| Maven/Gradle | Сборка и управление зависимостями |
| Maven Shade | Создание fat JAR |
| Jacoco | Code coverage |
| Checkstyle | Code style |
| SpotBugs | Static analysis |

---

## 9. Packaging и Distribution

### 9.1 Структура JAR

```
weather-sdk-1.0.0.jar
├── com/
│   └── kameleoon/
│       └── weather/
│           ├── WeatherSDK.class
│           ├── WeatherSDKFactory.class
│           ├── config/
│           ├── service/
│           ├── client/
│           ├── model/
│           ├── exception/
│           └── util/
├── META-INF/
│   ├── MANIFEST.MF
│   └── maven/
└── logback.xml (optional)
```

### 9.2 Maven Artifact

```xml
<dependency>
    <groupId>com.kameleoon</groupId>
    <artifactId>weather-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 9.3 Distribution Channels

1. **Maven Central** - для публичного использования
2. **GitHub Packages** - для внутреннего использования
3. **JAR файл** - для ручной установки

---

## 10. Расширяемость

### 10.1 Точки расширения

**1. Поддержка других погодных API**
```java
interface WeatherApiClient {
    ApiResponse getCurrentWeather(String cityName);
}

class OpenWeatherMapClient implements WeatherApiClient { }
class WeatherBitClient implements WeatherApiClient { }
```

**2. Различные стратегии кэширования**
```java
interface CacheStrategy {
    Optional<WeatherData> get(String key);
    void put(String key, WeatherData value);
}

class LRUCacheStrategy implements CacheStrategy { }
class TTLCacheStrategy implements CacheStrategy { }
```

**3. Pluggable логирование**
```java
interface Logger {
    void info(String message);
    void error(String message, Throwable t);
}
```

---

## 11. Безопасность

### 11.1 Меры безопасности

1. **API Key Management**
   - Не логировать API ключи
   - Не выводить в toString()
   - Возможность передачи через environment variables

2. **Input Validation**
   - Санитизация имен городов
   - Защита от injection

3. **Error Messages**
   - Не раскрывать внутренние детали
   - Безопасные сообщения для клиентов

4. **Dependencies**
   - Регулярное обновление
   - Проверка на уязвимости (OWASP Dependency Check)

---

## 12. Мониторинг и Логирование

### 12.1 Уровни логирования

| Level | Использование |
|-------|--------------|
| ERROR | Критические ошибки, требующие внимания |
| WARN  | Потенциальные проблемы (rate limit близко, кэш полон) |
| INFO  | Важные события (инициализация, shutdown) |
| DEBUG | Детали работы (cache hit/miss, API calls) |
| TRACE | Подробная диагностика |

### 12.2 Метрики

```java
public class SDKMetrics {
    - totalApiCalls
    - successfulApiCalls
    - failedApiCalls
    - cacheHitRate
    - averageResponseTime
    - activeCities
}
```

---

## 13. Преимущества архитектуры

1. ✅ **Модульность** - легко заменять компоненты
2. ✅ **Тестируемость** - каждый компонент тестируется изолированно
3. ✅ **Расширяемость** - легко добавлять новые функции
4. ✅ **Maintainability** - понятная структура кода
5. ✅ **Performance** - эффективное кэширование и pooling
6. ✅ **Reliability** - обработка ошибок, retry механизмы
7. ✅ **Scalability** - поддержка множества экземпляров

---

## 14. Компромиссы и решения

| Решение | Альтернатива | Обоснование выбора |
|---------|-------------|-------------------|
| LinkedHashMap для LRU | Guava Cache | Нет внешних зависимостей |
| Java HttpClient | OkHttp/Apache HC | Встроенный в JDK 11+ |
| Gson | Jackson | Проще, легче |
| Polling с fixed rate | Smart scheduling | Проще реализация, достаточно для требований |
| Multiton pattern | Полноценный DI | Соответствует требованию |

---

**Архитектура готова к имплементации ✅**

