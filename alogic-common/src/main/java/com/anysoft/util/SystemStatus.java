package com.anysoft.util;

/**
 * 系统状态
 * @author duanyy
 *
 */
public class SystemStatus {
	/**
	 * 已申请到的内存中还剩余的内存
	 */
	private long m_free_mem;
	/**
	 * 系统限定JVM所能用的最大内存，可通过-Xmx参数设置
	 */
	private long m_max_mem;
	/**
	 * JVM当前从OS申请到的内存，最大为maxMemory，可通过-Xms参数设置初始值
	 */
	private long m_total_mem;
	/**
	 * 处理器个数
	 */
	private int m_processors;
	/**
	 * JVM所有线程个数
	 */
	private int m_threadCount;
	
	/**
	 * 获取freeMemory
	 * @return freeMemory大小
	 */
	public long getFreeMem(){return m_free_mem;}
	/**
	 * 获取maxMemory
	 * @return maxMemory大小
	 */
	public long getMaxMem(){return m_max_mem;}
	/**
	 * 获取totalMemory
	 * @return totalMemory大小
	 */
	public long getTotalMem(){return m_total_mem;}
	/**
	 * 获取处理器个数
	 * @return 处理器个数
	 */	
	public long getProcessorCount(){return m_processors;}
	/**
	 * 获取线程数
	 * @return 线程数
	 */
	public long getThreadCount(){return m_threadCount;}
	
	public SystemStatus(){
		Runtime runtime = Runtime.getRuntime();
		
		m_free_mem = runtime.freeMemory();
		m_max_mem = runtime.maxMemory();
		m_total_mem = runtime.totalMemory();
		m_processors = runtime.availableProcessors();
		
		m_threadCount = Thread.activeCount();
	}
}
