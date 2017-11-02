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
	 * @return 本行是否存在
	 */
	public boolean exists();
	
	/**
	 * 获取数据的类型
	 * @return 数据的类型
	 */
	public String type();
	
	/**
	 * 设置ttl（相对当前值）
	 * @param time 时间
	 * @param timeUnit 时间单位
	 * @return 本次操作是否成功
	 */
	public boolean ttl(final long time,final TimeUnit timeUnit);
	
	/**
	 * 设置ttl（绝对值）
	 * @param time 时间
	 * @param timeUnit 时间单位
	 * @return 本次操作是否成功
	 */
	public boolean ttlAt(final long time,final TimeUnit timeUnit);
	
	/**
	 * 获取当前的TTL
	 * @return 当前的TTL
	 */
	public long ttl();
	
	/**
	 * 移除TTL
	 * @return 本次操作是否成功
	 */
	public boolean persist();
}
