/**
 * Иерархия исключений Weather SDK.
 * 
 * <p>SDK использует типизированные исключения для различных error случаев:
 *
 * <h2>Иерархия</h2>
 * <pre>
 * RuntimeException
 *      ↓
 * WeatherSDKException (base)
 *      ↓
 * ├── ApiException (base для API errors)
 * │   ├── InvalidApiKeyException (401)
 * │   ├── CityNotFoundException (404)
 * │   ├── RateLimitException (429)
 * │   └── ApiUnavailableException (5xx)
 * ├── ValidationException (invalid input)
 * ├── CacheException (cache errors)
 * └── ConfigurationException (config errors)
 * </pre>
 *
 * <h2>Обработка исключений</h2>
 * <pre>{@code
 * try {
 *     WeatherData weather = sdk.getWeather("London");
 * } catch (InvalidApiKeyException e) {
 *     // API ключ неверный
 * } catch (CityNotFoundException e) {
 *     // Город не найден
 *     String cityName = e.getCityName();
 * } catch (RateLimitException e) {
 *     // Превышен rate limit
 *     // Подождать и повторить
 * } catch (ApiUnavailableException e) {
 *     // API недоступен
 *     // Повторить позже
 * } catch (ValidationException e) {
 *     // Невалидные входные данные
 * } catch (WeatherSDKException e) {
 *     // Общая SDK ошибка
 * }
 * }</pre>
 *
 * <h2>Best Practices</h2>
 * <ul>
 *   <li>Ловите специфичные исключения для разной логики обработки
 *   <li>Используйте retry для {@link com.kameleoon.weather.exception.ApiUnavailableException}
 *   <li>Логируйте все исключения для debugging
 *   <li>Не игнорируйте исключения - обрабатывайте gracefully
 * </ul>
 *
 * @see com.kameleoon.weather.exception.WeatherSDKException
 * @see com.kameleoon.weather.exception.ApiException
 * 
 * @author Yury Shuldeshov
 * @version 1.0.0
 * @since 1.0.0
 */
package com.kameleoon.weather.exception;

