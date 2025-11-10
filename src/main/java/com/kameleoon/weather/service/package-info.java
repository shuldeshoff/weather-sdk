/**
 * Сервисные компоненты Weather SDK.
 * 
 * <p>Этот пакет содержит бизнес-логику и оркестрацию:
 * <ul>
 *   <li>{@link com.kameleoon.weather.service.WeatherService} - главный сервис для получения погоды
 *   <li>{@link com.kameleoon.weather.service.CacheService} - управление кэшем
 *   <li>{@link com.kameleoon.weather.service.PollingService} - автоматические обновления
 *   <li>{@link com.kameleoon.weather.service.LocationRegistry} - реестр отслеживаемых локаций
 * </ul>
 *
 * <h2>Архитектура</h2>
 * <pre>
 * WeatherSDK (Facade)
 *      ↓
 * WeatherService (Orchestration)
 *      ↓
 * ┌─────────────┬──────────────┐
 * ↓             ↓              ↓
 * CacheService  OpenWeatherMapClient  PollingService
 * </pre>
 *
 * <h2>Принципы</h2>
 * <ul>
 *   <li>Single Responsibility - каждый сервис имеет одну ответственность
 *   <li>Thread-safe - все сервисы потокобезопасны
 *   <li>Testable - легко тестируемы с использованием моков
 * </ul>
 *
 * @see com.kameleoon.weather.service.WeatherService
 * @see com.kameleoon.weather.service.CacheService
 * @see com.kameleoon.weather.service.PollingService
 * 
 * @author Yury Shuldeshov
 * @version 1.0.0
 * @since 1.0.0
 */
package com.kameleoon.weather.service;

