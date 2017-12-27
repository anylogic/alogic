package com.alogic.uid.impl;

import com.alogic.uid.IdGenerator;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 推特的Snowflake算法
 * 
 * @author yyduan
 * @since 1.6.11.5
 */
public class Snowflake extends IdGenerator.Abstract {

	/**
	 * 开始时间:2017-12-26
	 */
	private long twepoch = 1514160000000L;

	/**
	 * 序列的位数
	 */
	private long sequenceBits = 10L;

	/**
	 * 序列掩码
	 */
	private long sequenceMask = -1L ^ (-1L << sequenceBits);

	/**
	 * 进程id位数
	 */
	private long pIdBits = 12L;

	/**
	 * 进程id移位
	 */
	private long pIdShift = sequenceBits;

	/**
	 * 时间戳移位
	 */
	private long ttShift = sequenceBits + pIdBits;

	/**
	 * 进程id
	 */
	protected long pId = 0;

	/**
	 * 毫秒内序列
	 */
	private long sequence = 0L;

	/**
	 * 上次生成id的时间
	 */
	private long lastTimestamp = -1L;

	@Override
	public String nextId() {
		return String.format("%d", next());
	}

	@Override
	public long nextLong(){
		return next();
	}
	
	@Override
	public void configure(Properties p) {
		pId = PropertiesConstants.getLong(p, "pid", pId);
		
		sequenceBits = PropertiesConstants.getLong(p, "bits.seq", sequenceBits);
		pIdBits = PropertiesConstants.getLong(p, "bits.pid", pIdBits);
		
		sequenceMask = -1L ^ (-1L << sequenceBits);
		pIdShift = sequenceBits;
		ttShift = sequenceBits + pIdBits;
	}
	
	/**
	 * 获取当前的进程id
	 * @return 进程id
	 */
	protected long getPId(){
		return pId;
	}
	
	public synchronized long next() {
		long timestamp = timeGen();

		// 如果是同一时间生成的，则进行毫秒内序列
		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			// 毫秒内序列溢出
			if (sequence == 0) {
				// 阻塞到下一个毫秒,获得新的时间戳
				timestamp = tilNextMillis(lastTimestamp);
			}
		}
		// 时间戳改变，毫秒内序列重置
		else {
			sequence = 0L;
		}

		// 上次生成ID的时间截
		lastTimestamp = timestamp;

		// 移位并通过或运算拼到一起组成64位的ID
		return ((timestamp - twepoch) << ttShift) | (getPId() << pIdShift) | sequence;
	}

	/**
	 * 阻塞到下一个毫秒，直到获得新的时间戳
	 * 
	 * @param lastTimestamp
	 *            上次生成ID的时间截
	 * @return 当前时间戳
	 */
	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	/**
	 * 返回以毫秒为单位的当前时间
	 * 
	 * @return 当前时间(毫秒)
	 */
	protected long timeGen() {
		return System.currentTimeMillis();
	}
}
