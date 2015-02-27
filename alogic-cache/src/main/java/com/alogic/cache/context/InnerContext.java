package com.alogic.cache.context;

import com.alogic.cache.core.CacheStore;
import com.alogic.cache.local.SlottedCacheStore;
import com.anysoft.context.Inner;

/**
 * Source文件中内置的context
 * 
 * @author duanyy
 * @since 1.6.3.3
 * 
 */
public class InnerContext extends Inner<CacheStore> {

	@Override
	public String getObjectName() {
		return "cache";
	}

	@Override
	public String getDefaultClass() {
		return SlottedCacheStore.class.getName();
	}

}
