/**
 * Примеры использования Weather SDK.
 * 
 * <p>Этот пакет содержит полные рабочие примеры для различных сценариев:
 *
 * <h2>Базовое использование</h2>
 * <ul>
 *   <li>{@link com.kameleoon.weather.examples.WeatherSDKExample} - основные возможности SDK
 *     <ul>
 *       <li>ON_DEMAND режим</li>
 *       <li>POLLING режим</li>
 *       <li>Работа с кэшем</li>
 *     </ul>
 * </ul>
 *
 * <h2>Продвинутое использование</h2>
 * <ul>
 *   <li>{@link com.kameleoon.weather.examples.MultipleInstancesExample} - управление множественными экземплярами
 *     <ul>
 *       <li>WeatherSDKFactory (Multiton pattern)</li>
 *       <li>Разные API ключи</li>
 *       <li>Concurrent доступ</li>
 *     </ul>
 *   <li>{@link com.kameleoon.weather.examples.ErrorHandlingExample} - обработка ошибок
 *     <ul>
 *       <li>Все типы исключений</li>
 *       <li>Retry логика</li>
 *       <li>Graceful degradation</li>
 *       <li>Fallback стратегии</li>
 *     </ul>
 *   <li>{@link com.kameleoon.weather.examples.AdvancedUsageExample} - расширенные сценарии
 *     <ul>
 *       <li>Caching strategies</li>
 *       <li>Concurrent requests</li>
 *       <li>Hybrid mode (polling + on-demand)</li>
 *       <li>Monitoring и метрики</li>
 *     </ul>
 * </ul>
 *
 * <h2>Запуск примеров</h2>
 * <pre>{@code
 * # Установите API ключ
 * export OPENWEATHERMAP_API_KEY=your-api-key-here
 * 
 * # Скомпилируйте проект
 * mvn clean package
 * 
 * # Запустите пример
 * java -cp target/weather-sdk-1.0.0.jar \
 *      com.kameleoon.weather.examples.WeatherSDKExample
 * }</pre>
 *
 * <h2>Требования</h2>
 * <ul>
 *   <li>Валидный OpenWeatherMap API ключ</li>
 *   <li>Интернет соединение</li>
 *   <li>Java 17+</li>
 * </ul>
 *
 * @see com.kameleoon.weather.examples.WeatherSDKExample
 * @see com.kameleoon.weather.examples.MultipleInstancesExample
 * @see com.kameleoon.weather.examples.ErrorHandlingExample
 * @see com.kameleoon.weather.examples.AdvancedUsageExample
 * 
 * @author Yury Shuldeshov
 * @version 1.0.0
 * @since 1.0.0
 */
package com.kameleoon.weather.examples;

