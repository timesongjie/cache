package com.bbkmobile.iqoo.cache.ehcache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.ehcache.Element;

import com.bbkmobile.iqoo.cache.Cache;
import com.bbkmobile.iqoo.cache.CacheException;

public class EhcacheCache<String, Object> implements Cache<String, Object> {

	public net.sf.ehcache.Cache cache;

	public EhcacheCache(net.sf.ehcache.Cache cache) {
		this.cache = cache;
	}

	@Override
	public Object get(String key) throws CacheException {
		Element element = cache.get(key);
		return (Object) (element == null ? null : element.getObjectValue());
	}

	@Override
	public Object put(String key, Object value) throws CacheException {
		Element element = new Element(key, value);
		cache.put(element);
		return value;
	}

	@Override
	public Object remove(String key) throws CacheException {
		Object obj = (Object) cache.get(key).getObjectValue();
		cache.remove(key);
		return obj;
	}

	@Override
	public void clear() throws CacheException {
		cache.removeAll();
	}

	@Override
	public int size() {
		return cache.getSize();
	}

	@Override
	public Set<String> keys() {
		List<String> keys = cache.getKeys();
		Set<String> set = new HashSet<String>();
		for(String key:keys){
			set.add(key);
		}
		return set;
	}

	@Override
	public Collection<Object> values() {
		List<String> keys = cache.getKeys();
		List<Object> values = new ArrayList<Object>(keys.size());
		for(String key : keys){
			values.add((Object) cache.get(key).getObjectValue());
		}
		return values;
	}

	@Override
	public void removeAll() {
		if(cache != null){
			cache.removeAll();
		}
	}

}
