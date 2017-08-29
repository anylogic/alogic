package com.logicbus.redis.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anysoft.util.KeyGen;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.context.RedisPool;
import com.logicbus.redis.context.RedisSource;
import com.logicbus.redis.params.SetParams;
import com.logicbus.redis.toolkit.ScriptTool;
import com.logicbus.redis.toolkit.StringTool;

/**
 * 基于redis的lock实现
 * @author yyduan
 * 
 * @since 1.6.9.9
 * 
 * @version 1.6.9.9 [20170829 duanyy] <br>
 * - 增加基于redis的分布式锁实现 <br>
 */
public class RedisLock implements Lock {
	/**
	 * a logger of log4j
	 */
	private static Logger LOG = LoggerFactory.getLogger(RedisLock.class);
	
	protected String redisKey;
	
	protected String redisValue;
	
	protected int interval = 1000;
	
	protected int lockTTL = 30000;
	
	protected String poolId = "default";
	
	protected static final String unlockScript = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then return redis.call(\"del\",KEYS[1]) else return 0 end";
	
	protected boolean locked = false;
	
	public RedisLock(String lockname,Properties props){
		redisKey = String.format("%s$%s", PropertiesConstants.getString(props, "table", "lock"),lockname);
		redisValue = String.format("%d%s", System.currentTimeMillis(),KeyGen.uuid(5,0,9));
		poolId = PropertiesConstants.getString(props, "pool", poolId);
		lockTTL = PropertiesConstants.getInt(props, "ttl", lockTTL);
		interval = PropertiesConstants.getInt(props,"interval",interval);
	}

	@Override
	public void lock() {
		try {
			lockInterruptibly();
		} catch (InterruptedException e) {
			LOG.error("Lock waiting thread has been interrupted,exit",e);
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		while (!tryLock(0,TimeUnit.MILLISECONDS)){
			Thread.sleep(interval);
		}
	}

	@Override
	public Condition newCondition() {
		return null;
	}

	@Override
	public boolean tryLock() {
		RedisPool pool = RedisSource.get().get(poolId);
		if (pool == null){
			throw new RedisContextException("no_redis","Can not find redis pool:" + poolId);
		}
		
		Client client = pool.getClient();
		try {
			StringTool tool = (StringTool) client.getToolKit(StringTool.class);
			redisValue = String.format("%d%s", System.currentTimeMillis(),KeyGen.uuid(5,0,9));
			SetParams sp = new SetParams();
			sp.ttl(lockTTL, TimeUnit.MILLISECONDS);
			sp.onlySet(true);
			locked = tool.set(redisKey, redisValue,sp);
			return locked;
		}finally{
			pool.recycle(client, false);
		}
	}

	@Override
	public boolean tryLock(long timeout, TimeUnit tu)
			throws InterruptedException {
		return tryLock();
	}

	@Override
	public void unlock() {
		if (locked){
			RedisPool pool = RedisSource.get().get(poolId);
			if (pool == null){
				throw new RedisContextException("no_redis","Can not find redis pool:" + poolId);
			}
			
			Client client = pool.getClient();
			try {
				ScriptTool tool = (ScriptTool) client.getToolKit(ScriptTool.class);
				tool.eval(unlockScript, 1, redisKey,redisValue);
				tool.getIntegerReply();
			}finally{
				pool.recycle(client, false);
			}
		}
	}

}
