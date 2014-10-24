package com.logicbus.kvalue.core;

import java.util.concurrent.TimeUnit;

/**
 * Row
 * @author duanyy
 *
 */
public interface KeyValueRow {
	
	public String key();
	
	/**
	 * 删除
	 */
	public boolean delete();
	
	/**
	 * 是否存在 
	 * @return 
	 */
	public boolean exists();
	
	/**
	 * 获取数据的类型
	 * @return
	 */
	public String type();
	
	/**
	 * 设置ttl（相对当前值）
	 * @param time
	 * @param timeUnit
	 * @return
	 */
	public boolean ttl(final long time,final TimeUnit timeUnit);
	
	/**
	 * 设置ttl（绝对值）
	 * @param time
	 * @param timeUnit
	 * @return
	 */
	public boolean ttlAt(final long time,final TimeUnit timeUnit);
	
	/**
	 * 获取当前的TTL
	 * @param timeUnit
	 * @return
	 */
	public long ttl();
	
	/**
	 * 移除TTL
	 * @return
	 */
	public boolean persist();
}
