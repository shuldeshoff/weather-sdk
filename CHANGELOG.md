# Changelog

Все значимые изменения в проекте Weather SDK будут документированы в этом файле.

Формат основан на [Keep a Changelog](https://keepachangelog.com/ru/1.0.0/),
и проект придерживается [Semantic Versioning](https://semver.org/lang/ru/).

## [1.0.0] - 2025-11-10

### Добавлено
- **Основная функциональность SDK**
  - `WeatherSDK` - главный Facade для работы с OpenWeatherMap API
  - `WeatherSDKFactory` - Multiton pattern для управления множественными экземплярами
  - `SDKConfig` - конфигурация с Builder pattern
  - Поддержка двух режимов работы: `ON_DEMAND` и `POLLING`

- **Модели данных (Java 17 Records)**
  - `WeatherData` - основная модель погодных данных
  - `Temperature` - температурные данные с валидацией
  - `Weather` - описание погодных условий
  - `Wind` - данные о ветре
  - `Sys` - системная информация (восход/закат)
  - `CacheEntry` - запись кэша с timestamp
  - `CacheInfo` - статистика кэша

- **HTTP Client**
  - `OpenWeatherMapClient` - клиент для взаимодействия с OpenWeatherMap API
  - Retry механизм с exponential backoff
  - Таймауты на все операции
  - Детальная обработка HTTP статус-кодов

- **Интеллектуальное кэширование**
  - `LRUCache` - LRU cache с ограниченной емкостью
  - `CacheService` - сервис управления кэшем
  - Настраиваемый TTL (Time-To-Live)
  - Настраиваемый максимальный размер
  - Thread-safe операции

- **Polling Mode**
  - `PollingService` - автоматическое обновление данных
  - `LocationRegistry` - управление зарегистрированными локациями
  - Настраиваемый интервал polling
  - Graceful shutdown

- **Обработка ошибок**
  - `WeatherSDKException` - базовое исключение SDK
  - `ApiException` - базовое исключение для API ошибок
  - `InvalidApiKeyException` - невалидный API ключ (HTTP 401)
  - `CityNotFoundException` - город не найден (HTTP 404)
  - `RateLimitException` - превышен rate limit (HTTP 429)
  - `ApiUnavailableException` - API недоступен (HTTP 5xx)
  - `ValidationException` - ошибка валидации входных данных
  - `CacheException` - ошибка кэша
  - `ConfigurationException` - ошибка конфигурации

- **Примеры использования**
  - `WeatherSDKExample` - основные возможности SDK
  - `MultipleInstancesExample` - работа с Factory pattern
  - `ErrorHandlingExample` - обработка ошибок и retry logic
  - `AdvancedUsageExample` - расширенные сценарии использования

- **Логирование**
  - Интеграция с SLF4J
  - Параметризованное логирование
  - Различные уровни логирования (DEBUG, INFO, WARN, ERROR)

- **Конфигурация**
  - Настраиваемый размер кэша (по умолчанию: 100)
  - Настраиваемый TTL кэша (по умолчанию: 10 минут)
  - Настраиваемый интервал polling (по умолчанию: 5 минут)
  - Настраиваемое количество retry (по умолчанию: 3)
  - Настраиваемая база URL API (для тестирования)

### Технические детали
- **Java версия**: 17 (LTS)
- **Build tool**: Maven 3.6+
- **Зависимости**:
  - Gson 2.10.1 (JSON parsing)
  - SLF4J 2.0.9 (Logging API)
  - Logback 1.4.11 (Logging implementation)
  - JUnit 5.10.0 (Testing)
  - Mockito 5.5.0 (Mocking)
  - WireMock 3.0.1 (HTTP mocking)

- **Тестирование**:
  - 110 unit тестов
  - 90%+ code coverage
  - Integration тесты с WireMock
  - Thread-safety тесты

- **Качество кода**:
  - JaCoCo для coverage
  - Checkstyle для code style
  - SpotBugs для static analysis

### Архитектурные решения
- **Clean Architecture** - четкое разделение слоев
- **SOLID принципы** - следование лучшим практикам ООП
- **Design Patterns**:
  - Facade (WeatherSDK)
  - Multiton (WeatherSDKFactory)
  - Builder (SDKConfig)
  - Strategy (OperationMode)
  - LRU Cache (кэширование)

### Документация
- Comprehensive README с примерами
- API Reference в /docs
- Javadoc для всех публичных классов
- 4 полных примера использования
- FAQ и Best Practices

---

## [Unreleased]
### Планируется в будущих версиях

#### v1.1.0 (Q1 2026)
- Batch API requests
- Custom cache strategies
- Metrics API
- More granular configuration

#### v1.2.0 (Q2 2026)
- Support for other weather APIs
- Historical weather data
- Weather forecasts

#### v2.0.0 (Q3 2026)
- Spring Boot auto-configuration
- Reactive API support
- WebFlux integration

---

## Формат версий

Проект использует [Semantic Versioning](https://semver.org/):
- **MAJOR** версия - несовместимые изменения API
- **MINOR** версия - новая функциональность с обратной совместимостью
- **PATCH** версия - bug fixes с обратной совместимостью

## Типы изменений
- `Добавлено` - новая функциональность
- `Изменено` - изменения в существующей функциональности
- `Устарело` - функциональность, которая будет удалена
- `Удалено` - удаленная функциональность
- `Исправлено` - исправления ошибок
- `Безопасность` - исправления уязвимостей

---

**Автор:** Шульдешов Юрий Леонидович  
**Контакт:** [@shuldeshoff](https://t.me/shuldeshoff)  
**Repository:** [github.com/shuldeshoff/weather-sdk](https://github.com/shuldeshoff/weather-sdk)

