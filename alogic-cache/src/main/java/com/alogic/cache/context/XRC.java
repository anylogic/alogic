package com.alogic.cache.context;

import com.alogic.cache.core.CacheStore;
import com.alogic.cache.local.SlottedCacheStore;
import com.anysoft.context.XMLResource;


/**
 * 基于XMLResource的Context
 * 
 * @author duanyy
 * @since 1.6.3.3
 * 
 * @deprecated
 */
public class XRC extends XMLResource<CacheStore>{

	@Override
	public String getObjectName() {
		return "cache";
	}

	@Override
	public String getDefaultClass() {
		return SlottedCacheStore.class.getName();
	}

	@Override
	public String getDefaultXrc() {
		return "java:///com/alogic/cache/context/cache.default.xml#com.alogic.cache.context.XRC";
	}

}
