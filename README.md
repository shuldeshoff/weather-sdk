# Weather SDK для OpenWeatherMap API

[![Java](https://img.shields.io/badge/Java-11%2B-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Proprietary-blue.svg)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Design%20Phase-yellow.svg)]()

Профессиональный Java SDK для простой и эффективной интеграции с OpenWeatherMap API.

---

## 📋 Содержание

- [О проекте](#о-проекте)
- [Возможности](#возможности)
- [Быстрый старт](#быстрый-старт)
- [Документация](#документация)
- [Примеры](#примеры)
- [Разработка](#разработка)
- [Тестирование](#тестирование)
- [Roadmap](#roadmap)
- [Контакты](#контакты)

---

## О проекте

Weather SDK - это полнофункциональный SDK, разработанный для упрощения интеграции с OpenWeatherMap API. SDK предоставляет два режима работы, интеллектуальное кэширование и комплексную обработку ошибок.

### Ключевые преимущества

- ✅ **Простота использования** - интеграция за 3 строки кода
- ✅ **Высокая производительность** - интеллектуальное кэширование с LRU
- ✅ **Гибкость** - два режима работы (on-demand и polling)
- ✅ **Надежность** - retry механизмы и обработка ошибок
- ✅ **Production-ready** - высокие стандарты качества кода

---

## Возможности

### 🚀 Два режима работы

**On-Demand Mode**
- Данные запрашиваются только при необходимости
- Экономия API calls и ресурсов
- Идеально для нечастых запросов

**Polling Mode**
- Автоматическое обновление каждые 10 минут
- Zero-latency ответы (всегда из кэша)
- Идеально для real-time приложений

### 💾 Интеллектуальное кэширование

- LRU Cache с автоматическим eviction
- TTL: 10 минут актуальности
- Capacity: до 10 городов
- Thread-safe

### 🔑 Multiton Pattern

- Поддержка нескольких API ключей
- Один экземпляр на ключ (нет дубликатов)
- Управление жизненным циклом
- Автоматическая очистка

### 🛡️ Надежность

- Retry с exponential backoff
- Таймауты на все операции
- Детальные исключения
- Graceful error handling

---

## Быстрый старт

### Требования

- Java 11 или выше
- Maven 3.6+ или Gradle 6+
- OpenWeatherMap API ключ ([получить здесь](https://openweathermap.org/api))

### Установка

#### Maven

```xml
<dependency>
    <groupId>com.kameleoon</groupId>
    <artifactId>weather-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Gradle

```gradle
implementation 'com.kameleoon:weather-sdk:1.0.0'
```

### Первый запрос (60 секунд)

```java
import com.kameleoon.weather.WeatherSDK;
import com.kameleoon.weather.WeatherSDKFactory;
import com.kameleoon.weather.config.OperationMode;
import com.kameleoon.weather.model.WeatherData;

public class QuickStart {
    public static void main(String[] args) {
        // 1. Получить SDK
        String apiKey = "your_api_key_here";
        WeatherSDK sdk = WeatherSDKFactory.getInstance(
            apiKey, 
            OperationMode.ON_DEMAND
        );
        
        try {
            // 2. Запросить погоду
            WeatherData weather = sdk.getWeather("London");
            
            // 3. Использовать данные
            System.out.println("City: " + weather.getName());
            System.out.println("Temperature: " + weather.getTemperature().getTemp() + "°C");
            System.out.println("Weather: " + weather.getWeather().getMain());
            
        } catch (WeatherSDKException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            // 4. Очистка
            sdk.shutdown();
            WeatherSDKFactory.removeInstance(apiKey);
        }
    }
}
```

### Готово! 🎉

---

## Документация

### Доступная документация

| Документ | Описание |
|---------|----------|
| [Executive Summary](docs/00_executive_summary.md) | Обзор проекта и ключевые возможности |
| [Requirements Analysis](docs/01_requirements_analysis.md) | Детальный анализ требований |
| [Architecture Design](docs/02_architecture_design.md) | Архитектура и дизайн-решения |
| [Implementation Plan](docs/03_implementation_plan.md) | Подробный план реализации |
| [API Reference](docs/04_api_reference.md) | Полная справка по API |

### Быстрые ссылки

- 📖 [Полный API Reference](docs/04_api_reference.md)
- 🏗️ [Архитектура](docs/02_architecture_design.md)
- 💡 [Примеры использования](#примеры)
- 🧪 [Тестирование](#тестирование)

---

## Примеры

### On-Demand режим

```java
WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, OperationMode.ON_DEMAND);

try {
    // Первый запрос - из API (~500-1000ms)
    WeatherData weather = sdk.getWeather("London");
    
    // Второй запрос в течение 10 минут - из кэша (~1ms)
    WeatherData cached = sdk.getWeather("London");
    
} finally {
    sdk.shutdown();
    WeatherSDKFactory.removeInstance(apiKey);
}
```

### Polling режим

```java
WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, OperationMode.POLLING);

try {
    // Первый запрос - из API
    WeatherData paris = sdk.getWeather("Paris");
    
    // Все последующие - мгновенно из кэша (< 10ms)
    WeatherData cached = sdk.getWeather("Paris");
    
    // SDK автоматически обновляет данные каждые 10 минут
    
} finally {
    sdk.shutdown();
    WeatherSDKFactory.removeInstance(apiKey);
}
```

### Обработка ошибок

```java
try {
    WeatherData weather = sdk.getWeather("London");
    
} catch (InvalidApiKeyException e) {
    System.err.println("Invalid API key");
} catch (CityNotFoundException e) {
    System.err.println("City not found");
} catch (RateLimitException e) {
    System.err.println("Rate limit exceeded");
} catch (ApiUnavailableException e) {
    System.err.println("API unavailable");
} catch (WeatherSDKException e) {
    System.err.println("Unexpected error: " + e.getMessage());
}
```

### Несколько API ключей

```java
// Создать несколько независимых экземпляров
WeatherSDK sdk1 = WeatherSDKFactory.getInstance(apiKey1, OperationMode.ON_DEMAND);
WeatherSDK sdk2 = WeatherSDKFactory.getInstance(apiKey2, OperationMode.POLLING);

// Использовать независимо
WeatherData weather1 = sdk1.getWeather("London");
WeatherData weather2 = sdk2.getWeather("Paris");

// Попытка создать дубликат вернет существующий
WeatherSDK duplicate = WeatherSDKFactory.getInstance(apiKey1, OperationMode.POLLING);
System.out.println(sdk1 == duplicate); // true

// Cleanup
WeatherSDKFactory.removeInstance(apiKey1);
WeatherSDKFactory.removeInstance(apiKey2);
```

Больше примеров: [examples/](examples/)

---

## Разработка

### Структура проекта

```
weather-sdk/
├── src/
│   ├── main/java/           # Исходный код
│   │   └── com/kameleoon/weather/
│   │       ├── WeatherSDK.java
│   │       ├── WeatherSDKFactory.java
│   │       ├── service/     # Бизнес-логика
│   │       ├── client/      # API интеграция
│   │       ├── model/       # Модели данных
│   │       └── exception/   # Исключения
│   └── test/java/           # Тесты
├── examples/                # Примеры
├── docs/                    # Документация
└── pom.xml                  # Maven config
```

### Сборка проекта

```bash
# Клонировать репозиторий
git clone https://github.com/kameleoon/weather-sdk.git
cd weather-sdk

# Собрать проект
mvn clean install

# Запустить тесты
mvn test

# Генерация Javadoc
mvn javadoc:javadoc

# Создать JAR
mvn package
```

### Запуск примеров

```bash
# Установить API ключ
export OPENWEATHER_API_KEY=your_api_key

# Запустить пример
mvn exec:java -Dexec.mainClass="com.kameleoon.weather.examples.OnDemandExample"
```

---

## Тестирование

### Запуск тестов

```bash
# Все тесты
mvn test

# Только unit тесты
mvn test -Dtest=*Test

# Только integration тесты
mvn test -Dtest=*IntegrationTest

# С coverage report
mvn clean test jacoco:report
```

### Coverage

```bash
# Генерация отчета
mvn jacoco:report

# Открыть отчет
open target/site/jacoco/index.html
```

### Quality Checks

```bash
# Checkstyle
mvn checkstyle:check

# SpotBugs
mvn spotbugs:check

# Все проверки
mvn clean verify
```

---

## Технологии

### Core

- **Java**: 11+
- **Gson**: 2.10.1 (JSON)
- **SLF4J**: 2.0.9 (Logging)
- **HttpClient**: Java built-in

### Testing

- **JUnit**: 5.10.0
- **Mockito**: 5.5.0
- **WireMock**: 3.0.1
- **AssertJ**: 3.24.2
- **AwaitBility**: 4.2.0

### Build & Quality

- **Maven**: 3.6+
- **JaCoCo**: 0.8.10 (Coverage)
- **Checkstyle**: 3.3.0
- **SpotBugs**: 4.7.3.5

---

## Roadmap

### ✅ Phase 1: Analysis & Design (Completed)
- [x] Requirements analysis
- [x] Architecture design
- [x] Implementation plan
- [x] API documentation

### 🔄 Phase 2: Foundation (Week 1)
- [ ] Project setup
- [ ] Models and exceptions
- [ ] Basic structure

### 📋 Phase 3: Core Features (Week 2)
- [ ] HTTP Client
- [ ] Cache Service
- [ ] Weather Service

### 📋 Phase 4: Advanced Features (Week 2-3)
- [ ] Polling Service
- [ ] SDK Facade
- [ ] Factory (Multiton)

### 📋 Phase 5: Polish (Week 3)
- [ ] Complete documentation
- [ ] Examples
- [ ] Quality checks

### 📋 Phase 6: Release (Week 3-4)
- [ ] Final testing
- [ ] Packaging
- [ ] Publishing

### Future (v1.1+)
- [ ] Batch API requests
- [ ] Custom cache strategies
- [ ] Metrics API
- [ ] Spring Boot integration

---

## Best Practices

### Управление ресурсами

```java
// ✅ ПРАВИЛЬНО
WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, mode);
try {
    // use SDK
} finally {
    sdk.shutdown();
    WeatherSDKFactory.removeInstance(apiKey);
}

// ❌ НЕПРАВИЛЬНО - утечка ресурсов
WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, mode);
sdk.getWeather("London");
// забыли shutdown!
```

### Выбор режима

```java
// ✅ ON_DEMAND для редких запросов (< 6/hour)
if (requestsPerHour < 6) {
    mode = OperationMode.ON_DEMAND;
}

// ✅ POLLING для частых запросов (>= 6/hour)
if (requestsPerHour >= 6) {
    mode = OperationMode.POLLING;
}
```

### Thread Safety

```java
// ✅ SDK полностью thread-safe
WeatherSDK sdk = WeatherSDKFactory.getInstance(apiKey, mode);

ExecutorService executor = Executors.newFixedThreadPool(10);
for (String city : cities) {
    executor.submit(() -> {
        WeatherData weather = sdk.getWeather(city);
        // process weather
    });
}
```

---

## Ограничения

### OpenWeatherMap API Limits

**Free Tier:**
- 60 calls/minute
- 1,000,000 calls/month

**Рекомендации:**
- POLLING mode с 10 городами = 6 calls/hour
- Оставляет запас для on-demand запросов

### SDK Limits

| Параметр | Ограничение |
|----------|-------------|
| Макс. городов в кэше | 10 |
| TTL кэша | 10 минут |
| Polling interval | 10 минут |

---

## FAQ

**Q: Можно ли изменить размер кэша?**  
A: В версии 1.0 - нет, фиксировано 10 городов. Планируется в v1.1.

**Q: Можно ли изменить TTL кэша?**  
A: В версии 1.0 - нет, фиксировано 10 минут. Планируется в v1.1.

**Q: Поддерживаются ли другие погодные API?**  
A: В версии 1.0 - только OpenWeatherMap. Другие API планируются в v1.2.

**Q: SDK thread-safe?**  
A: Да, полностью thread-safe.

**Q: Можно ли использовать в Spring Boot?**  
A: Да, но автоконфигурация планируется в v2.0.

---

## Лицензия

Proprietary © 2025 Kameleoon

---

## Авторы

Разработано для Kameleoon в рамках тестового задания.

---

## Контакты

- **Issues**: [GitHub Issues](https://github.com/kameleoon/weather-sdk/issues)
- **Documentation**: [docs/](docs/)
- **Examples**: [examples/](examples/)

---

**Статус проекта**: 🟡 Design Phase Complete  
**Следующий этап**: Implementation  
**Ожидаемый релиз**: 3-4 недели

---

Сделано с ❤️ для Kameleoon

