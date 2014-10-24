package com.logicbus.backend.timer;

/**
 * 日志监听器
 * @author duanyy
 *
 */
public interface TimerLogListener {
	/**
	 * 有日志到来
	 * @param timer 定时器
	 * @param log 日志
	 */
	public void logArrived(Timer timer,TimerLog log);
	public void logFlush();
}
