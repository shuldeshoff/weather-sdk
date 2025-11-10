/**
 * Weather SDK для интеграции с OpenWeatherMap API.
 * 
 * <p>Этот пакет содержит главные компоненты SDK:
 * <ul>
 *   <li>{@link com.kameleoon.weather.WeatherSDK} - основной фасад для работы с API
 *   <li>{@link com.kameleoon.weather.WeatherSDKFactory} - фабрика для управления множественными экземплярами
 * </ul>
 *
 * <h2>Быстрый старт</h2>
 * <pre>{@code
 * // 1. Создать конфигурацию
 * SDKConfig config = SDKConfig.builder("your-api-key")
 *     .operationMode(OperationMode.ON_DEMAND)
 *     .build();
 * 
 * // 2. Создать SDK
 * WeatherSDK sdk = new WeatherSDK(config);
 * 
 * // 3. Получить погоду
 * WeatherData weather = sdk.getWeather("London");
 * System.out.println(weather.temperature().temp());
 * 
 * // 4. Cleanup
 * sdk.shutdown();
 * }</pre>
 *
 * <h2>Основные возможности</h2>
 * <ul>
 *   <li>Два режима работы: ON_DEMAND и POLLING
 *   <li>Интеллектуальное кэширование (LRU + TTL)
 *   <li>Автоматические retry с exponential backoff
 *   <li>Thread-safe операции
 *   <li>Comprehensive error handling
 * </ul>
 *
 * @see com.kameleoon.weather.WeatherSDK
 * @see com.kameleoon.weather.config.SDKConfig
 * @see com.kameleoon.weather.model.WeatherData
 * 
 * @author Yury Shuldeshov
 * @version 1.0.0
 * @since 1.0.0
 */
package com.kameleoon.weather;

