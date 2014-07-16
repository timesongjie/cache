/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbkmobile.iqoo.cache.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.bbkmobile.iqoo.cache.Cache;
import com.bbkmobile.iqoo.cache.CacheException;
import com.bbkmobile.iqoo.cache.redis.serializer.JacksonJsonRedisSerializer;
import com.bbkmobile.iqoo.cache.redis.serializer.RedisSerializer;
import com.bbkmobile.iqoo.cache.redis.serializer.SerializationException;
import com.bbkmobile.iqoo.cache.redis.serializer.SimpleSerializer;

/**
 *
 * @author lqzhai
 */
public class RedisShardedCache<K, V> implements Cache<K, V> {

    /**
     * Private internal log instance.
     */
    private static final Logger log = LoggerFactory.getLogger(RedisShardedCache.class);
    private static final byte[] NULL = "nil".getBytes();
    private RedisSerializer<K> keySerializer;
    private RedisSerializer<V> valueSerializer;
    private byte[] cacheName;
//    private ShardedJedis cache;
    private ShardedJedisPool pool;

    public RedisShardedCache(String cacheName, ShardedJedisPool pool) {
        if (pool == null) {
            throw new IllegalArgumentException("Cache argument cannot be null.");
        }
        this.pool = pool;
        this.cacheName = cacheName.getBytes();
        this.keySerializer = new SimpleSerializer<K>();
        this.valueSerializer = new SimpleSerializer<V>();
//        }  
    }

    public RedisShardedCache(String cacheName, ShardedJedisPool pool, Class<K> keyType, Class<V> valueType) {
        if (pool == null) {
            throw new IllegalArgumentException("Cache argument cannot be null.");
        }
        this.pool = pool;
        this.cacheName = cacheName.getBytes();
        this.keySerializer = new JacksonJsonRedisSerializer(keyType);

        this.valueSerializer = new JacksonJsonRedisSerializer(valueType);

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
       ShardedJedis cache = null;
        boolean isGetResource = true;
        try {
           cache = pool.getResource();
            if (key == null) {
                return null;
            } else {
                byte[] val = cache.hget(this.cacheName, this.keySerializer.serialize(key));
                if (null == val || Arrays.equals(NULL, val)) {
                    return null;
                }
                return this.valueSerializer.deserialize(val);
            }
        } catch (JedisConnectionException  t) {
            isGetResource = false;
            pool.returnBrokenResource(cache);
            throw new CacheException(t);
        } catch (SerializationException ex) {
            throw new CacheException(ex);
        } finally {
            if (null != cache && isGetResource) {
                this.pool.returnResource(cache);
                cache = null;
            }
        }
    }

    /**
     * Puts an object into the cache.
     *
     * @param key the key.
     * @param value the value.
     */
    @Override
    public V put(K key, V value) throws CacheException {
        ShardedJedis cache = null;
        boolean isGetResource = true;
        try {
            cache = pool.getResource();
            cache.hset(this.cacheName, this.keySerializer.serialize(key), this.valueSerializer.serialize(value));
            return value;
        }  catch (JedisConnectionException  t) {
            isGetResource = false;
            pool.returnBrokenResource(cache);
            throw new CacheException(t);
        } catch (SerializationException ex) {
            throw new CacheException(ex);
        } finally {
            if (null != cache && isGetResource) {
                this.pool.returnResource(cache);
                cache = null;
            }
        }
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
        ShardedJedis cache = null;
        boolean isGetResource = true;
        try {
            V previous = get(key);
            cache = pool.getResource();

            cache.hdel(this.cacheName, this.keySerializer.serialize(key));
            return previous;
        }  catch (JedisConnectionException  t) {
            isGetResource = false;
            pool.returnBrokenResource(cache);
            throw new CacheException(t);
        } catch (SerializationException ex) {
            throw new CacheException(ex);
        } finally {
            if (null != cache && isGetResource) {
                this.pool.returnResource(cache);
                cache = null;
            }
        }
    }

    /**
     * Removes all elements in the cache, but leaves the cache in a useable
     * state.
     */
    @Override
    public void clear() throws CacheException {
        ShardedJedis cache = null;
        boolean isGetResource = true;
        try {
            cache = pool.getResource();
            cache.del(new String(this.cacheName));
        } catch (JedisConnectionException  t) {
            isGetResource = false;
            pool.returnBrokenResource(cache);
            throw new CacheException(t);
        }  finally {
            if (null != cache && isGetResource) {
                this.pool.returnResource(cache);
                cache = null;
            }
        }
    }

    @Override
    public int size() {
        ShardedJedis cache = null;
        boolean isGetResource = true;
        try {
            cache = pool.getResource();
            return cache.hlen(this.cacheName).intValue();
        }  catch (JedisConnectionException  t) {
            isGetResource = false;
            pool.returnBrokenResource(cache);
				throw new CacheException(t);
        }  finally {
            if (null != cache && isGetResource) {
                this.pool.returnResource(cache);
                cache = null;
            }
        }
    }

    @Override
    public Set<K> keys() {
        ShardedJedis cache = null;
        boolean isGetResource = true;
        try {
            cache = pool.getResource();
            @SuppressWarnings({"unchecked"})
            Set<byte[]> byteKeys = cache.hkeys(this.cacheName);
            if (byteKeys != null && byteKeys.size() >0) {
                Set<K> keys = new LinkedHashSet<K>();
                for (byte[] key : byteKeys) {
                    keys.add(this.keySerializer.deserialize(key));
                }
                return Collections.unmodifiableSet(new LinkedHashSet<K>(keys));
            } else {
                return Collections.emptySet();
            }
        }  catch (JedisConnectionException  t) {
            isGetResource = false;
            pool.returnBrokenResource(cache);
			throw new CacheException(t);
        } catch (SerializationException ex) {
			throw new CacheException(ex);
        } finally {
            if (null != cache && isGetResource) {
                this.pool.returnResource(cache);
                cache = null;
            }
        }
    }

    @Override
    public Collection<V> values() {
        ShardedJedis cache = null;
        boolean isGetResource = true;
        try {
           cache = pool.getResource();
            @SuppressWarnings({"unchecked"})
            Collection<byte[]> byteValues = cache.hvals(this.cacheName);
            if (byteValues != null && byteValues.size() >0) {
                List<V> values = new ArrayList<V>(byteValues.size());
                for (byte[] val : byteValues) {
                    values.add(this.valueSerializer.deserialize(val));
                }
                return Collections.unmodifiableList(values);
            } else {
                return Collections.emptyList();
            }
        } catch (JedisConnectionException  t) {
            isGetResource = false;
            pool.returnBrokenResource(cache);
            throw new CacheException(t);
        } catch (SerializationException ex) {
            throw new CacheException(ex);
        } finally {
            if (null != cache && isGetResource) {
                this.pool.returnResource(cache);
                cache = null;
            }
        }
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
