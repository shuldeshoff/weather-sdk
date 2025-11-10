/**
 * Классы конфигурации для Weather SDK.
 * 
 * <p>Пакет содержит все необходимые компоненты для настройки SDK:
 * <ul>
 *   <li>{@link com.kameleoon.weather.config.SDKConfig} - основная конфигурация с Builder pattern
 *   <li>{@link com.kameleoon.weather.config.OperationMode} - режимы работы SDK (ON_DEMAND, POLLING)
 * </ul>
 *
 * <h2>Пример использования</h2>
 * <pre>{@code
 * SDKConfig config = SDKConfig.builder("api-key")
 *     .operationMode(OperationMode.ON_DEMAND)
 *     .cacheMaxSize(100)
 *     .cacheTtlMinutes(10)
 *     .maxRetries(3)
 *     .build();
 * }</pre>
 *
 * @see com.kameleoon.weather.config.SDKConfig
 * @see com.kameleoon.weather.config.OperationMode
 * 
 * @author Yury Shuldeshov
 * @version 1.0.0
 * @since 1.0.0
 */
package com.kameleoon.weather.config;

