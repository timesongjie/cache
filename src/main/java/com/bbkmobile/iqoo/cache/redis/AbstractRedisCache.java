/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbkmobile.iqoo.cache.redis;
import java.util.Collection;
import java.util.Set;

import com.bbkmobile.iqoo.cache.CacheException;



/**
 *
 * @author lqzhai
 */
public abstract class AbstractRedisCache<K, V>{
    /**
     * Returns the Cached value stored under the specified {@code key} or
     * {@code null} if there is no Cache entry for that {@code key}.
     *
     * @param key the key that the value was previous added with
     * @return the cached object or {@code null} if there is no entry for the specified {@code key}
     * @throws CacheException if there is a problem accessing the underlying cache system
     */
    public abstract V get(K key) throws CacheException;

    /**
     * Adds a Cache entry.
     *
     * @param key   the key used to identify the object being stored.
     * @param value the value to be stored in the cache.
     * @return the previous value associated with the given {@code key} or {@code null} if there was previous value
     * @throws CacheException if there is a problem accessing the underlying cache system
     */
    public abstract V put(K key, V value) throws CacheException;

    /**
     * Remove the cache entry corresponding to the specified key.
     *
     * @param key the key of the entry to be removed.
     * @return the previous value associated with the given {@code key} or {@code null} if there was previous value
     * @throws CacheException if there is a problem accessing the underlying cache system
     */
    public abstract V remove(K key) throws CacheException;

    /**
     * Clear all entries from the cache.
     *
     * @throws CacheException if there is a problem accessing the underlying cache system
     */
    public abstract void clear() throws CacheException;

    /**
     * Returns the number of entries in the cache.
     *
     * @return the number of entries in the cache.
     */
    public abstract int size();

    /**
     * Returns a view of all the keys for entries contained in this cache.
     *
     * @return a view of all the keys for entries contained in this cache.
     */
    public abstract Set<K> keys();

    /**
     * Returns a view of all of the values contained in this cache.
     *
     * @return a view of all of the values contained in this cache.
     */
    public abstract Collection<V> values();    
}
