package com.alogic.cache.xscript;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

/**
 * Namespace
 * @author yyduan
 * @since 1.6.11.59 [20180911 duanyy]
 */
public class NS extends Segment{

	public NS(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("cache",Cache.class);
		registerModule("cache-expire",CacheClear.class);
		registerModule("cache-load",CacheQuery.class);
		registerModule("cache-locate",CacheLocate.class);
		registerModule("cache-hget",CacheHashGet.class);
		registerModule("cache-hgetall",CacheHashGetAll.class);
		registerModule("cache-hset",CacheHashSet.class);
		registerModule("cache-hdel",CacheHashDel.class);
		registerModule("cache-hexist",CacheHashExist.class);
		registerModule("cache-hsize",CacheHashSize.class);
		registerModule("cache-smembers",CacheSetMembers.class);
		registerModule("cache-sexist",CacheSetExist.class);
		registerModule("cache-sadd",CacheSetAdd.class);
		registerModule("cache-sdel",CacheSetDel.class);
		registerModule("cache-ssize",CacheSetSize.class);
	}

}
