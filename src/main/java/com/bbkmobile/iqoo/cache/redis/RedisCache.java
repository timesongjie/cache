/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.bbkmobile.iqoo.cache.redis;
import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPool;

import com.bbkmobile.iqoo.cache.Cache;
import com.bbkmobile.iqoo.cache.CacheException;

/**
 * Shiro {@link org.apache.shiro.cache.Cache} implementation that wraps an
 * {@link net.sf.ehcache.Ehcache} instance.
 *
 * @since 0.2
 */
public class RedisCache<K, V> implements Cache<K, V> {

    /**
     * Private internal log instance.
     */
    private static final Logger log = LoggerFactory.getLogger(RedisCache.class);
    private RedisNativeCache<K, V> cache;

    /**
     * Constructs a new EhCache instance with the given cache.
     *
     * @param cache - delegate EhCache instance this Shiro cache instance will
     * wrap.
     */
    public RedisCache(String cacheName, JedisPool cachePool) {
        if (cachePool == null) {
            throw new IllegalArgumentException("Cache argument cannot be null.");
        }
        this.cache = new RedisNativeCache<K, V>(cacheName, cachePool);
    }

    public RedisCache(String cacheName, JedisPool cachePool,Class<K> keyType,Class<V> valueType) {
        if (cachePool == null) {
            throw new IllegalArgumentException("Cache argument cannot be null.");
        }
        this.cache = new RedisNativeCache<K, V>(cacheName, cachePool,keyType,valueType);
    }    
    
    /**
     * Gets a value of an element which matches the given key.
     *
     * @param key the key of the element to return.
     * @return The value placed into the cache with an earlier put, or null if
     * not found or expired
     */
    @Override
    public V get(K key) throws CacheException {
        return cache.get(key);
    }

    /**
     * Puts an object into the cache.
     *
     * @param key the key.
     * @param value the value.
     */
    @Override
    public V put(K key, V value) throws CacheException {
        return cache.put(key, value);
    }

    /**
     * Removes the element which matches the key.
     *
     * <p>If no element matches, nothing is removed and no Exception is
     * thrown.</p>
     *
     * @param key the key of the element to remove
     */
    @Override
    public V remove(K key) throws CacheException {
        return cache.remove(key);
    }

    /**
     * Removes all elements in the cache, but leaves the cache in a useable
     * state.
     */
    @Override
    public void clear() throws CacheException {
        this.cache.clear();
    }

    @Override
    public int size() {
        return this.cache.size();
    }

    @Override
    public Set<K> keys() {
        return this.cache.keys();
    }

    @Override
    public Collection<V> values() {
        return this.cache.values();
    }

    /**
     * Returns the size (in bytes) that this EhCache is using in memory (RAM),
     * or
     * <code>-1</code> if that number is unknown or cannot be calculated.
     *
     * @return the size (in bytes) that this EhCache is using in memory (RAM),
     * or <code>-1</code> if that number is unknown or cannot be calculated.
     */
    public long getMemoryUsage() {
//        try {
//            return cache.hgetAll(this.cacheName)..calculateInMemorySize();
//        } catch (Throwable t) {
//            return -1;
//        }
        return -1;
    }

    /**
     * Returns the size (in bytes) that this EhCache's memory store is using
     * (RAM), or
     * <code>-1</code> if that number is unknown or cannot be calculated.
     *
     * @return the size (in bytes) that this EhCache's memory store is using
     * (RAM), or <code>-1</code> if that number is unknown or cannot be
     * calculated.
     */
    public long getMemoryStoreSize() {
//        try {
//            return cache.getMemoryStoreSize();
//        } catch (Throwable t) {
//            throw new CacheException(t);
//        }

        return -1;
    }

    /**
     * Returns the size (in bytes) that this EhCache's disk store is consuming
     * or
     * <code>-1</code> if that number is unknown or cannot be calculated.
     *
     * @return the size (in bytes) that this EhCache's disk store is consuming
     * or <code>-1</code> if that number is unknown or cannot be calculated.
     */
    public long getDiskStoreSize() {
//        try {
//            return cache.getDiskStoreSize();
//        } catch (Throwable t) {
//            throw new CacheException(t);
//        }
        return -1;
    }

	@Override
	public void removeAll() {
		// TODO Auto-generated method stub
		
	}
}
