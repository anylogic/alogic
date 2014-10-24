package com.logicbus.redis.context.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.Watcher;
import com.logicbus.redis.context.RedisPool;
import com.logicbus.redis.context.RedisContext;

public class Inner implements RedisContext {
	
	protected RedisPoolHolder holder = new RedisPoolHolder();
	
	protected final static Logger logger = LogManager.getLogger(Inner.class);
	
	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		if (holder != null){
			holder.configure(_e, _properties);
		}
	}

	
	public void close() throws Exception {
		if (holder != null){
			holder.close();
		}
	}

	
	public RedisPool getPool(String id) {
		return holder != null ? holder.getPool(id) : null;
	}

	
	public void addWatcher(Watcher<RedisPool> watcher) {
		// do nothing
	}

	
	public void removeWatcher(Watcher<RedisPool> watcher) {
		// do nothing
	}
}
