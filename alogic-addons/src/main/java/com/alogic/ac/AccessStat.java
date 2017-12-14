package com.alogic.ac;

/**
 * 访问统计
 * 
 * 
 * @author yyduan
 * @since 1.6.10.6
 * 
 * @version 1.6.10.7 [20171115 duanyy] <br>
 * - AccessStat中增加被Denied的统计信息 <br>
 */
public class AccessStat {
	
	/**
	 * 总调用次数
	 */
	public long timesTotal = 0;
	
	/**
	 * 最近一分钟调用次数
	 */
	public int timesOneMin = 0;
	
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
	public int thread = 0;
	
	/**
	 * 时间戳(用于定义最近一分钟)
	 */
	public long timestamp = 0;
	
	/**
	 * 共享锁的等待线程数
	 */
	public long waitCnt = 0;
}
