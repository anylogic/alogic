package com.logicbus.backend.acm;

/**
 * 访问统计
 * 
 * <br>
 * 基于一个会话的访问统计.
 * 
 * @author duanyy
 *
 * @since 1.2.3
 * 
 * @version 1.6.10.7 [20171115 duanyy] <br>
 * - AccessStat中增加被Denied的统计信息 <br>
 */
public class AccessStat {
	/**
	 * 总调用次数
	 */
	protected long timesTotal = 0;
	/**
	 * 最近一分钟调用次数
	 */
	protected int timesOneMin = 0;
	
	/**
	 * 被拒绝的总次数
	 */
	public long deniedTotal = 0;
	
	/**
	 * 最近一分钟被拒绝的总次数
	 */
	public long deniedOneMin = 0;		
	
	/**
	 * 当前接入进程个数
	 */
	protected int thread = 0;
	/**
	 * 时间戳(用于定义最近一分钟)
	 */
	protected long timestamp = 0;
	
	/**
	 * 共享锁的等待线程数
	 */
	protected long waitCnt = 0;
}
