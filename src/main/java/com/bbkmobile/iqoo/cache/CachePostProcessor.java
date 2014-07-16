package com.bbkmobile.iqoo.cache;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
/**
 * 缓存预加载 beanPostProcessor
 * @author time
 *
 */
public class CachePostProcessor implements BeanPostProcessor{

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if(bean != null && bean instanceof CacheManagerAware){
			CacheManagerAware cache = (CacheManagerAware) bean;
			cache.preLoad();
		}
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

}
