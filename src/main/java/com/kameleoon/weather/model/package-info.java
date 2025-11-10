/**
 * Data models для Weather SDK.
 * 
 * <p>Все модели реализованы как Java 17 Records, обеспечивая:
 * <ul>
 *   <li>Immutability - неизменяемость данных
 *   <li>Thread-safety - потокобезопасность
 *   <li>Automatic equals/hashCode/toString
 *   <li>Compact и читаемый код
 * </ul>
 *
 * <h2>Основные модели</h2>
 * <ul>
 *   <li>{@link com.kameleoon.weather.model.WeatherData} - агрегатор всех погодных данных
 *   <li>{@link com.kameleoon.weather.model.Temperature} - температурные данные
 *   <li>{@link com.kameleoon.weather.model.Weather} - описание погодных условий
 *   <li>{@link com.kameleoon.weather.model.Wind} - данные о ветре
 *   <li>{@link com.kameleoon.weather.model.Sys} - системная информация (восход/закат)
 * </ul>
 *
 * <h2>Служебные модели</h2>
 * <ul>
 *   <li>{@link com.kameleoon.weather.model.CacheEntry} - запись в кэше
 *   <li>{@link com.kameleoon.weather.model.CacheInfo} - информация о состоянии кэша
 * </ul>
 *
 * <h2>Пример использования</h2>
 * <pre>{@code
 * WeatherData weather = sdk.getWeather("London");
 * 
 * // Доступ к данным через record accessors
 * double temp = weather.temperature().temp();
 * String description = weather.weather().description();
 * double windSpeed = weather.wind().speed();
 * 
 * // Records автоматически предоставляют equals/hashCode
 * WeatherData weather2 = sdk.getWeather("London");
 * boolean same = weather.equals(weather2);
 * }</pre>
 *
 * @see com.kameleoon.weather.model.WeatherData
 * @see com.kameleoon.weather.model.Temperature
 * 
 * @author Yury Shuldeshov
 * @version 1.0.0
 * @since 1.0.0
 */
package com.kameleoon.weather.model;

