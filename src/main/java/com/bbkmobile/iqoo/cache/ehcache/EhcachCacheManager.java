package com.bbkmobile.iqoo.cache.ehcache;

import com.bbkmobile.iqoo.cache.Cache;
import com.bbkmobile.iqoo.cache.CacheException;
import com.bbkmobile.iqoo.cache.CacheManager;

public class EhcachCacheManager implements CacheManager {


	private net.sf.ehcache.CacheManager manager;
	
	public EhcachCacheManager(String path){
		 manager = net.sf.ehcache.CacheManager.create(path);
	}
	
	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		net.sf.ehcache.Cache cache = manager.getCache(name);
		return new EhcacheCache(cache);
	}

	public  net.sf.ehcache.CacheManager getInstance(){
		return manager;
	}
	
	public void destory(){
		if(this.manager != null){
			this.manager.getInstance().shutdown();
		}
	}
}
