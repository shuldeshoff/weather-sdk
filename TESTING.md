# Тестирование Weather SDK

Этот документ описывает различные типы тестов в проекте и инструкции по их запуску.

## 📋 Содержание

- [Unit тесты](#unit-тесты)
- [Integration тесты](#integration-тесты)
- [Performance Benchmarks](#performance-benchmarks)
- [Code Coverage](#code-coverage)
- [Continuous Integration](#continuous-integration)

---

## Unit тесты

### Описание

Unit тесты покрывают отдельные компоненты SDK в изоляции. Используют моки (Mockito, WireMock) для имитации внешних зависимостей.

### Статистика

- **Всего тестов**: 110
- **Coverage**: 90%+
- **Фреймворки**: JUnit 5, Mockito, WireMock, AssertJ

### Запуск

```bash
# Запустить все unit тесты
mvn test

# Запустить конкретный тестовый класс
mvn test -Dtest=WeatherSDKTest

# Запустить конкретный тест
mvn test -Dtest=WeatherSDKTest#shouldGetWeatherInOnDemandMode

# Запустить с подробным выводом
mvn test -Dtest=WeatherSDKTest -DforkCount=0
```

### Структура тестов

```
src/test/java/com/kameleoon/weather/
├── model/                    # Тесты моделей данных
│   ├── WeatherTest.java
│   ├── TemperatureTest.java
│   └── CacheEntryTest.java
├── util/                     # Тесты утилит
│   └── LRUCacheTest.java
├── service/                  # Тесты сервисов
│   ├── CacheServiceTest.java
│   └── LocationRegistryTest.java
├── client/                   # Тесты HTTP клиента
│   └── OpenWeatherMapClientTest.java
├── config/                   # Тесты конфигурации
│   └── SDKConfigTest.java
├── WeatherSDKTest.java       # Тесты главного фасада
└── WeatherSDKFactoryTest.java # Тесты фабрики
```

### Особенности

- **WireMock**: Используется для мокирования HTTP ответов от OpenWeatherMap API
- **Thread-safety тесты**: Проверка concurrent доступа к кэшу и SDK
- **Awaitility**: Для тестирования асинхронных операций в POLLING режиме

---

## Integration тесты

### Описание

Integration тесты работают с реальным OpenWeatherMap API. Они требуют валидный API ключ и выполняют реальные HTTP запросы.

⚠️ **Внимание**: Эти тесты считаются в ваш API rate limit!

### Требования

1. **API ключ**: Получите на [OpenWeatherMap](https://openweathermap.org/api)
2. **Environment variable**: Установите `OPENWEATHERMAP_API_KEY`

### Настройка

```bash
# Linux/macOS
export OPENWEATHERMAP_API_KEY=your-real-api-key-here

# Windows (CMD)
set OPENWEATHERMAP_API_KEY=your-real-api-key-here

# Windows (PowerShell)
$env:OPENWEATHERMAP_API_KEY="your-real-api-key-here"
```

### Запуск

```bash
# Запустить только integration тесты
mvn test -Dgroups=integration

# Или конкретный класс
mvn test -Dtest=RealApiIntegrationTest

# С подробным выводом
mvn test -Dtest=RealApiIntegrationTest -DforkCount=0
```

### Пропуск integration тестов

Если API ключ не установлен, integration тесты будут автоматически пропущены (skipped).

```bash
# Запустить все тесты кроме integration
mvn test -DexcludedGroups=integration
```

### Покрываемые сценарии

- ✅ Успешное получение погоды для существующих городов
- ✅ Обработка несуществующих городов
- ✅ Работа кэша при повторных запросах
- ✅ Множественные города
- ✅ POLLING режим
- ✅ Невалидный API ключ
- ✅ Города с пробелами и спецсимволами
- ✅ Очистка кэша
- ✅ Соблюдение TTL кэша

---

## Performance Benchmarks

### Описание

Benchmarks измеряют производительность критических компонентов SDK с использованием JMH (Java Microbenchmark Harness).

### Компоненты

- **CacheBenchmark**: Производительность кэша
  - Put операции
  - Get операции (hit/miss)
  - LRU eviction
  - Concurrent доступ

### Компиляция

```bash
# Скомпилировать benchmark классы
mvn clean test-compile
```

### Запуск

```bash
# Запустить все benchmarks
mvn exec:java -Dexec.mainClass="com.kameleoon.weather.benchmark.CacheBenchmark"

# Или напрямую через Java
java -cp target/test-classes:target/classes:~/.m2/repository/org/openjdk/jmh/jmh-core/1.37/jmh-core-1.37.jar \
     com.kameleoon.weather.benchmark.CacheBenchmark

# С custom параметрами JMH
java -jar target/benchmarks.jar -wi 5 -i 10 -f 1
```

### Параметры JMH

- `-wi N`: Warmup iterations (по умолчанию: 3)
- `-i N`: Measurement iterations (по умолчанию: 5)
- `-f N`: Number of forks (по умолчанию: 1)
- `-t N`: Number of threads (по умолчанию: 1)
- `-rf json`: Output format (json/csv/text)

### Интерпретация результатов

```
Benchmark                                   Mode  Cnt    Score    Error  Units
CacheBenchmark.benchmarkCacheServicePut     avgt    5  245.123 ± 12.456  ns/op
CacheBenchmark.benchmarkCacheServiceGetHit  avgt    5   87.456 ±  4.321  ns/op
CacheBenchmark.benchmarkCacheServiceGetMiss avgt    5  112.789 ±  5.678  ns/op
```

- **Mode**: avgt = average time (среднее время выполнения)
- **Score**: Среднее время в наносекундах
- **Error**: Margin of error (погрешность)

---

## Code Coverage

### Генерация отчета

```bash
# Запустить тесты с coverage
mvn clean test

# Сгенерировать HTML отчет
mvn jacoco:report

# Открыть отчет
open target/site/jacoco/index.html  # macOS
xdg-open target/site/jacoco/index.html  # Linux
start target/site/jacoco/index.html  # Windows
```

### Требования к coverage

- **Минимум**: 80% (enforced в pom.xml)
- **Цель**: 90%+
- **Текущий**: ~92%

### Проверка coverage

```bash
# Проверить соответствие минимуму
mvn clean verify

# Если coverage < 80%, build упадет
```

### Coverage по компонентам

| Компонент | Coverage | Статус |
|-----------|----------|--------|
| Models (Records) | 100% | ✅ |
| Services | 95% | ✅ |
| Client | 90% | ✅ |
| SDK Facade | 92% | ✅ |
| Factory | 88% | ✅ |
| Utilities | 94% | ✅ |
| Exceptions | 100% | ✅ |

---

## Continuous Integration

### GitHub Actions

Все тесты автоматически запускаются на GitHub Actions при каждом push/PR.

### Workflow

```yaml
# .github/workflows/ci.yml
- Java 17, 21
- Platforms: Ubuntu, macOS, Windows
- Тесты: Unit (110)
- Coverage: JaCoCo → Codecov
- Quality: Checkstyle, SpotBugs
- Security: OWASP Dependency Check
```

### Статус badges

Добавьте в README:

```markdown
[![Build Status](https://github.com/shuldeshoff/weather-sdk/workflows/CI/badge.svg)](https://github.com/shuldeshoff/weather-sdk/actions)
[![Coverage](https://codecov.io/gh/shuldeshoff/weather-sdk/branch/main/graph/badge.svg)](https://codecov.io/gh/shuldeshoff/weather-sdk)
```

---

## Troubleshooting

### Проблема: Тесты зависают

```bash
# Отключить parallel execution
mvn test -DforkCount=1 -DreuseForks=false
```

### Проблема: WireMock port conflicts

```bash
# Изменить порт в тестах или освободить порт 8089
lsof -ti:8089 | xargs kill -9  # Linux/macOS
netstat -ano | findstr :8089   # Windows
```

### Проблема: Integration тесты падают

1. Проверьте API ключ: `echo $OPENWEATHERMAP_API_KEY`
2. Проверьте интернет соединение
3. Проверьте rate limits на OpenWeatherMap
4. Используйте `-DexcludedGroups=integration` чтобы пропустить

### Проблема: Out of Memory при benchmarks

```bash
# Увеличьте heap size
export MAVEN_OPTS="-Xmx2g"
mvn test-compile exec:java -Dexec.mainClass="..."
```

---

## Best Practices

### Для разработчиков

1. **Запускайте тесты локально** перед каждым commit
   ```bash
   mvn clean verify
   ```

2. **Проверяйте coverage** для новых классов
   ```bash
   mvn jacoco:report && open target/site/jacoco/index.html
   ```

3. **Пишите тесты first** (TDD подход)
   - Сначала failing test
   - Потом минимальная реализация
   - Затем рефакторинг

4. **Изолируйте тесты**
   - Каждый тест независим
   - Используйте `@BeforeEach` для setup
   - Не полагайтесь на порядок выполнения

5. **Используйте meaningful names**
   ```java
   @Test
   @DisplayName("Should throw CityNotFoundException when city does not exist")
   void shouldThrowCityNotFoundExceptionWhenCityDoesNotExist() { }
   ```

### Для CI/CD

1. **Fail fast**: Если unit тесты падают, не запускайте integration
2. **Cache dependencies**: Кэшируйте Maven dependencies
3. **Parallel execution**: Запускайте на разных OS параллельно
4. **Artifacts**: Сохраняйте test reports и coverage

---

## Дополнительные ресурсы

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [WireMock Documentation](https://wiremock.org/docs/)
- [JMH Samples](https://github.com/openjdk/jmh/tree/master/jmh-samples)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)

---

**Автор:** Шульдешов Юрий Леонидович  
**Контакт:** [@shuldeshoff](https://t.me/shuldeshoff)  
**Repository:** [github.com/shuldeshoff/weather-sdk](https://github.com/shuldeshoff/weather-sdk)

