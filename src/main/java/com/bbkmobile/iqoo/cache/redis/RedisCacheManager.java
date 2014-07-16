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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedisPool;

import com.bbkmobile.iqoo.cache.Cache;
import com.bbkmobile.iqoo.cache.CacheException;
import com.bbkmobile.iqoo.cache.CacheManager;

/**
 * Shiro {@code CacheManager} implementation utilizing the Ehcache framework for
 * all cache functionality.
 * <p/>
 * This class can {@link #setCacheManager(net.sf.ehcache.CacheManager) accept} a
 * manually configured
 * {@link net.sf.ehcache.CacheManager net.sf.ehcache.CacheManager} instance, or
 * an {@code ehcache.xml} path location can be specified instead and one will be
 * constructed. If neither are specified, Shiro's failsafe
 * <code><a href="./ehcache.xml">ehcache.xml</a>} file will be used by default.
 * <p/>
 * This implementation requires EhCache 1.2 and above. Make sure EhCache 1.1 or
 * earlier is not in the classpath or it will not work.
 * <p/>
 * Please see the <a href="http://ehcache.sf.net" target="_top">Ehcache
 * website</a> for their documentation.
 *
 * @see <a href="http://ehcache.sf.net" target="_top">The Ehcache website</a>
 * @since 0.2
 */
public class RedisCacheManager implements CacheManager{

    /**
     * This class's private log instance.
     */
    private static final Logger log = LoggerFactory.getLogger(RedisCacheManager.class);
    /**
     * Indicates if the CacheManager instance was implicitly/automatically
     * created by this instance, indicating that it should be automatically
     * cleaned up as well on shutdown.
     */
    private boolean cacheManagerImplicitlyCreated = false;
    /**
     * file location of the ehcache CacheManager config file.
     */
    private String cacheManagerConfigFile;
    private boolean sharded = false;
    private JedisPool jedisPool;
    private ShardedJedisPool shardedJedisPool;
    
    private String host;
    private String password;
    /**
     * Default no argument constructor
     */
    public RedisCacheManager() {
    }

    /**
     * Returns the resource location of the config file used to initialize a new
     * EhCache CacheManager instance. The string can be any resource path
     * supported by the
     * {@link org.apache.shiro.io.ResourceUtils#getInputStreamForPath(String)}
     * call.
     * <p/>
     * This property is ignored if the CacheManager instance is injected
     * directly - that is, it is only used to lazily create a CacheManager if
     * one is not already provided.
     *
     * @return the resource location of the config file used to initialize the
     * wrapped EhCache CacheManager instance.
     */
    public String getCacheManagerConfigFile() {
        return this.cacheManagerConfigFile;
    }

    /**
     * Sets the resource location of the config file used to initialize the
     * wrapped EhCache CacheManager instance. The string can be any resource
     * path supported by the
     * {@link org.apache.shiro.io.ResourceUtils#getInputStreamForPath(String)}
     * call.
     * <p/>
     * This property is ignored if the CacheManager instance is injected
     * directly - that is, it is only used to lazily create a CacheManager if
     * one is not already provided.
     *
     * @param classpathLocation resource location of the config file used to
     * create the wrapped EhCache CacheManager instance.
     */
    public void setCacheManagerConfigFile(String classpathLocation) {
        this.cacheManagerConfigFile = classpathLocation;
    }

    /**
     * Loads an existing EhCache from the cache manager, or starts a new cache
     * if one is not found.
     *
     * @param name the name of the cache to load/create.
     */
    @Override
    public final <K, V> Cache<K, V> getCache(String name) throws CacheException {

        if (log.isTraceEnabled()) {
            log.trace("Acquiring EhCache instance named [" + name + "]");
        }


        try {
            if (this.isSharded() && null != shardedJedisPool) {
                return new RedisShardedCache<K, V>(name, shardedJedisPool);
            } else {
                return new RedisCache<K, V>(name, jedisPool);
            }

        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public final <K, V> Cache<K, V> getCache(String name, Class<K> keyType, Class<V> valueType) throws CacheException {

        if (log.isTraceEnabled()) {
            log.trace("Acquiring EhCache instance named [" + name + "]");
        }


        try {
            if (this.isSharded() && null != shardedJedisPool) {
                return new RedisShardedCache<K, V>(name, shardedJedisPool);
            } else {
                return new RedisCache<K, V>(name, jedisPool, keyType, valueType);
            }
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    /**
     * Initializes this instance.
     * <p/>
     * If a {@link #setCacheManager CacheManager} has been explicitly set (e.g.
     * via Dependency Injection or programatically) prior to calling this
     * method, this method does nothing.
     * <p/>
     * However, if no {@code CacheManager} has been set, the default Ehcache
     * singleton will be initialized, where Ehcache will look for an
     * {@code ehcache.xml} file at the root of the classpath. If one is not
     * found, Ehcache will use its own failsafe configuration file.
     * <p/>
     * Because Shiro cannot use the failsafe defaults (fail-safe expunges cached
     * objects after 2 minutes, something not desirable for Shiro sessions),
     * this class manages an internal default configuration for this case.
     *
     * @throws org.apache.shiro.cache.CacheException if there are any
     * CacheExceptions thrown by EhCache.
     * @see net.sf.ehcache.CacheManager#create
     */
    public final void init() throws CacheException {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(200);
        config.setTestOnBorrow(true);
        if(host == null || host.trim().length() == 0){
        	host = "127.0.0.1";//TODO
        }
        password = null;//TODO
        int timeout = -1;
        if (isSharded()) {
            String[] hosts = host.split(";");
            List<JedisShardInfo> jedisShardInfos = new ArrayList<JedisShardInfo>();
            JedisShardInfo jsi = null;
            for(String h:hosts){
                String[] tmp = h.split(":");
                String ip = tmp[0];
                int port = Integer.parseInt(tmp[1]);
                jsi = new JedisShardInfo(ip, port);
                jedisShardInfos.add(jsi);
            }
            shardedJedisPool = new ShardedJedisPool(config, jedisShardInfos);
        } else {
            //String host = "172.16.10.160";
            String port = null;
            if (null != password && !"".equals(password)) {
                jedisPool = new JedisPool(config, host, Protocol.DEFAULT_PORT, 100000, password);
            }
            jedisPool = new JedisPool(config, host, Protocol.DEFAULT_PORT, 100000);

        }
    }

    /**
     * Shuts-down the wrapped Ehcache CacheManager <b>only if implicitly
     * created</b>.
     * <p/>
     * If another component injected a non-null CacheManager into this instace
     * before calling {@link #init() init}, this instance expects that same
     * component to also destroy the CacheManager instance, and it will not
     * attempt to do so.
     */
    public void destroy() {
        if (null != this.jedisPool) {
            this.jedisPool.destroy();
        }

        if (null != this.shardedJedisPool) {
            this.shardedJedisPool.destroy();
        }
    }

    /**
     * @return the sharded
     */
    public boolean isSharded() {
        return sharded;
    }

    /**
     * @param sharded the sharded to set
     */
    public void setSharded(boolean sharded) {
        this.sharded = sharded;
    }

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
