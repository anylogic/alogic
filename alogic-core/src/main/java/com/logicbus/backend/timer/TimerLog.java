package com.logicbus.backend.timer;

/**
 * 定时器日志
 * @author duanyy
 *
 */
public class TimerLog {
	/**
	 * 创建时间
	 */
	public long createTime;
	/**
	 * 日志类型
	 * 
	 * <br>
	 * 日志类型包括：operation,schedule,process
	 */
	public String type;
	
	/**
	 * 日志说明
	 */
	public String note;
	
	/**
	 * 日志上下文
	 */
	public String context;
}
