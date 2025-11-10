/**
 * HTTP клиенты для взаимодействия с внешними API.
 * 
 * <p>Содержит реализацию HTTP клиента для OpenWeatherMap API:
 * <ul>
 *   <li>{@link com.kameleoon.weather.client.OpenWeatherMapClient} - HTTP клиент для OpenWeatherMap
 * </ul>
 *
 * <h2>Возможности</h2>
 * <ul>
 *   <li>Использует Java 11+ HttpClient
 *   <li>Автоматические retry с exponential backoff
 *   <li>Таймауты на все операции
 *   <li>Детальная обработка HTTP статус-кодов
 *   <li>Comprehensive error handling
 * </ul>
 *
 * <h2>Обработка HTTP статусов</h2>
 * <table border="1">
 *   <tr><th>Status</th><th>Exception</th><th>Описание</th></tr>
 *   <tr><td>200</td><td>-</td><td>Успех</td></tr>
 *   <tr><td>401</td><td>InvalidApiKeyException</td><td>Невалидный API ключ</td></tr>
 *   <tr><td>404</td><td>CityNotFoundException</td><td>Город не найден</td></tr>
 *   <tr><td>429</td><td>RateLimitException</td><td>Превышен rate limit</td></tr>
 *   <tr><td>5xx</td><td>ApiUnavailableException</td><td>Server error</td></tr>
 * </table>
 *
 * @see com.kameleoon.weather.client.OpenWeatherMapClient
 * 
 * @author Yury Shuldeshov
 * @version 1.0.0
 * @since 1.0.0
 */
package com.kameleoon.weather.client;

