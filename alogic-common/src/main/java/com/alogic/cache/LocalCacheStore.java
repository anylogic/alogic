package com.alogic.cache;

import com.alogic.load.Store;

/**
 * 本地Hash实现的CacheStore
 * 
 * @author yyduan
 * @since 1.6.11.6
 */
public class LocalCacheStore extends Store.HashStore<CacheObject>{

	@Override
	public CacheObject newObject(String id) {
		return new CacheObject.Simple(id);
	}

}
