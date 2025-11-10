# Руководство по внесению вклада в Weather SDK

Спасибо за интерес к Weather SDK! Мы приветствуем вклад от сообщества.

## 📋 Содержание

- [Кодекс поведения](#кодекс-поведения)
- [С чего начать](#с-чего-начать)
- [Процесс разработки](#процесс-разработки)
- [Стандарты кода](#стандарты-кода)
- [Тестирование](#тестирование)
- [Коммиты и Pull Requests](#коммиты-и-pull-requests)
- [Документация](#документация)

---

## Кодекс поведения

Этот проект придерживается [Contributor Covenant Code of Conduct](https://www.contributor-covenant.org/). 
Участвуя в проекте, вы соглашаетесь соблюдать его условия.

---

## С чего начать

### Требования

Перед началом работы убедитесь, что у вас установлено:

- **Java 17 (LTS)** или выше
- **Maven 3.6+**
- **Git**
- IDE (рекомендуется IntelliJ IDEA или Eclipse)

### Настройка окружения

1. **Fork репозитория**
   ```bash
   # Перейдите на GitHub и сделайте fork
   https://github.com/shuldeshoff/weather-sdk
   ```

2. **Клонируйте ваш fork**
   ```bash
   git clone https://github.com/YOUR_USERNAME/weather-sdk.git
   cd weather-sdk
   ```

3. **Добавьте upstream remote**
   ```bash
   git remote add upstream https://github.com/shuldeshoff/weather-sdk.git
   ```

4. **Проверьте сборку**
   ```bash
   mvn clean install
   mvn test
   ```

Если все тесты прошли успешно, вы готовы к работе!

---

## Процесс разработки

### 1. Создайте issue

Перед началом работы:
- Проверьте существующие [Issues](https://github.com/shuldeshoff/weather-sdk/issues)
- Если подобной задачи нет, создайте новую issue
- Опишите, что вы планируете сделать
- Дождитесь обратной связи от maintainers

### 2. Создайте ветку

```bash
# Синхронизируйте с upstream
git checkout main
git pull upstream main

# Создайте feature branch
git checkout -b feature/my-awesome-feature

# Или bugfix branch
git checkout -b bugfix/issue-123
```

**Именование веток:**
- `feature/` - новая функциональность
- `bugfix/` - исправление ошибок
- `docs/` - изменения в документации
- `refactor/` - рефакторинг без изменения функциональности
- `test/` - добавление или изменение тестов

### 3. Внесите изменения

- Пишите чистый, читаемый код
- Следуйте существующему стилю кода
- Добавляйте комментарии где необходимо
- Пишите тесты для новой функциональности
- Обновляйте документацию

### 4. Тестируйте

```bash
# Запустите все тесты
mvn test

# Проверьте coverage
mvn clean test jacoco:report

# Запустите code quality checks
mvn checkstyle:check
mvn spotbugs:check

# Или все вместе
mvn clean verify
```

### 5. Зафиксируйте изменения

```bash
git add .
git commit -m "feat: add support for weather forecasts"
```

### 6. Отправьте в свой fork

```bash
git push origin feature/my-awesome-feature
```

### 7. Создайте Pull Request

- Перейдите на GitHub
- Нажмите "New Pull Request"
- Заполните шаблон PR
- Опишите, что и зачем вы изменили
- Ссылайтесь на связанные issues

---

## Стандарты кода

### Java Code Style

Мы следуем [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) с небольшими модификациями:

**Форматирование:**
```java
// Отступы: 4 пробела
public class MyClass {
    private String field;
    
    public void myMethod() {
        if (condition) {
            doSomething();
        }
    }
}
```

**Именование:**
- Классы: `PascalCase` (например, `WeatherSDK`)
- Методы: `camelCase` (например, `getWeather()`)
- Константы: `UPPER_SNAKE_CASE` (например, `MAX_RETRIES`)
- Пакеты: `lowercase` (например, `com.kameleoon.weather`)

**Javadoc:**
```java
/**
 * Brief description of the class.
 * More detailed explanation if needed.
 *
 * @author Your Name
 */
public class MyClass {
    
    /**
     * Brief description of the method.
     *
     * @param param Description of parameter
     * @return Description of return value
     * @throws SomeException When and why
     */
    public String myMethod(String param) throws SomeException {
        // Implementation
    }
}
```

### Логирование

Используйте SLF4J:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClass {
    private static final Logger logger = LoggerFactory.getLogger(MyClass.class);
    
    public void myMethod() {
        logger.debug("Debug message with param: {}", param);
        logger.info("Info message");
        logger.warn("Warning message");
        logger.error("Error message", exception);
    }
}
```

**НЕ используйте:**
- `System.out.println()`
- `System.err.println()`
- `e.printStackTrace()`

### Обработка ошибок

```java
// ✅ ПРАВИЛЬНО
try {
    riskyOperation();
} catch (SpecificException e) {
    logger.error("Failed to perform operation", e);
    throw new WeatherSDKException("Meaningful message", e);
}

// ❌ НЕПРАВИЛЬНО
try {
    riskyOperation();
} catch (Exception e) {
    // Empty catch block
}
```

---

## Тестирование

### Unit Tests

Все публичные методы должны иметь unit тесты:

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class MyClassTest {
    
    @Test
    @DisplayName("Should return weather data for valid city")
    void shouldReturnWeatherDataForValidCity() {
        // Given
        MyClass myClass = new MyClass();
        
        // When
        String result = myClass.myMethod("London");
        
        // Then
        assertNotNull(result);
        assertEquals("expected", result);
    }
}
```

### Coverage

- Минимальный coverage: **80%**
- Цель: **90%+**
- Проверяйте coverage: `mvn clean test jacoco:report`
- Отчет: `target/site/jacoco/index.html`

### Integration Tests

Для integration тестов используйте WireMock:

```java
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@ExtendWith(MockitoExtension.class)
class IntegrationTest {
    
    private WireMockServer wireMockServer;
    
    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
    }
    
    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }
}
```

---

## Коммиты и Pull Requests

### Формат коммитов

Мы используем [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Типы:**
- `feat:` - новая функциональность
- `fix:` - исправление ошибки
- `docs:` - изменения в документации
- `style:` - форматирование, отсутствуют изменения в коде
- `refactor:` - рефакторинг кода
- `test:` - добавление или изменение тестов
- `chore:` - обновление build задач, package manager и т.д.

**Примеры:**
```bash
feat(cache): add configurable TTL for cache entries
fix(client): handle timeout exceptions correctly
docs(readme): update installation instructions
test(service): add unit tests for WeatherService
```

### Pull Request Guidelines

**Описание PR должно включать:**

1. **Что изменено**
   - Краткое описание изменений
   - Связанные issues (`Fixes #123`, `Closes #456`)

2. **Зачем изменено**
   - Обоснование необходимости изменений
   - Какую проблему решает

3. **Как протестировано**
   - Какие тесты добавлены
   - Результаты тестирования
   - Скриншоты (если применимо)

4. **Checklist**
   - [ ] Код следует style guidelines
   - [ ] Добавлены unit тесты
   - [ ] Все тесты проходят
   - [ ] Обновлена документация
   - [ ] Обновлен CHANGELOG.md

**Размер PR:**
- Старайтесь делать небольшие, сфокусированные PR
- Один PR = одна функциональность/исправление
- Большие изменения разбивайте на несколько PR

---

## Документация

### Javadoc

Все публичные классы и методы должны иметь Javadoc:

```java
/**
 * Retrieves weather data for the specified city.
 * 
 * <p>This method first checks the cache for recent data.
 * If cache miss or expired, it fetches fresh data from the API.
 *
 * @param cityName The name of the city (must not be null or blank)
 * @return Weather data for the specified city
 * @throws ValidationException if city name is null or blank
 * @throws CityNotFoundException if the city is not found
 * @throws ApiException if there's an API error
 */
public WeatherData getWeather(String cityName) {
    // Implementation
}
```

### README.md

При добавлении новой функциональности, обновите:
- Секцию "Возможности"
- Примеры использования
- API documentation
- FAQ (если применимо)

### Примеры

Для сложной функциональности добавьте example класс:
- Поместите в `src/main/java/com/kameleoon/weather/examples/`
- Дайте понятное имя (например, `NewFeatureExample.java`)
- Добавьте комментарии, объясняющие use case

---

## Дополнительные рекомендации

### Производительность

- Избегайте преждевременной оптимизации
- Если оптимизируете, добавьте бенчмарки
- Проверяйте влияние на существующую производительность

### Безопасность

- Не коммитьте API ключи, пароли, токены
- Используйте `.gitignore` для sensitive files
- При обнаружении уязвимости создайте private security advisory

### Совместимость

- Поддерживайте обратную совместимость
- Breaking changes - только в major версиях
- Помечайте deprecated методы `@Deprecated`

---

## Вопросы?

Если у вас есть вопросы:
- Создайте [Discussion](https://github.com/shuldeshoff/weather-sdk/discussions)
- Спросите в существующей issue
- Свяжитесь с maintainer: [@shuldeshoff](https://t.me/shuldeshoff)

---

## Лицензия

Внося вклад в проект, вы соглашаетесь, что ваш код будет лицензирован 
под той же лицензией, что и проект (см. [LICENSE](LICENSE)).

---

**Спасибо за ваш вклад! 🎉**

Каждый вклад, большой или малый, ценен и помогает сделать Weather SDK лучше.

---

**Автор:** Шульдешов Юрий Леонидович  
**Контакт:** [@shuldeshoff](https://t.me/shuldeshoff)  
**Repository:** [github.com/shuldeshoff/weather-sdk](https://github.com/shuldeshoff/weather-sdk)

