/**
 * Утилиты и вспомогательные классы Weather SDK.
 * 
 * <p>Содержит переиспользуемые компоненты:
 * <ul>
 *   <li>{@link com.kameleoon.weather.util.LRUCache} - LRU (Least Recently Used) cache implementation
 * </ul>
 *
 * <h2>LRUCache</h2>
 * <p>Thread-safe реализация LRU кэша с ограниченной емкостью.
 * При достижении максимального размера удаляет наименее недавно использованные элементы.
 *
 * <h3>Особенности</h3>
 * <ul>
 *   <li>O(1) для get/put операций
 *   <li>Thread-safe через synchronized
 *   <li>Автоматическое eviction
 *   <li>Generic типизация
 * </ul>
 *
 * <h3>Пример использования</h3>
 * <pre>{@code
 * LRUCache<String, String> cache = new LRUCache<>(100);
 * 
 * cache.put("key1", "value1");
 * String value = cache.get("key1");  // returns "value1"
 * 
 * cache.remove("key1");
 * cache.clear();
 * }</pre>
 *
 * @see com.kameleoon.weather.util.LRUCache
 * 
 * @author Yury Shuldeshov
 * @version 1.0.0
 * @since 1.0.0
 */
package com.kameleoon.weather.util;

