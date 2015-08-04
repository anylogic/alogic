package com.alogic.timer;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 调度者
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public interface Scheduler extends XMLConfigurable,Reportable,Runnable {
	/**
	 * 根据环境变量配置
	 * 
	 * @param p 环境变量
	 * @throws BaseException
	 */
	public void configure(Properties p) throws BaseException;
	
	/**
	 * 获取所管理的Timer列表
	 * @return Timer列表
	 */
	public Timer [] getTimers();
	
	/**
	 * 获取指定ID的timer
	 * @param id ID
	 * @return Timer
	 */
	public Timer get(String id);
	
	/**
	 * 将指定的Timer加入调度列表
	 * @param timer 定时器
	 */
	public void schedule(Timer timer);
	
	/**
	 * 按照指定的匹配规则调度指定的任务
	 * @param id 定时器的ID
	 * @param matcher 匹配器
	 * @param task 任务
	 */
	public void schedule(String id,Matcher matcher,Task task);
	
	/**
	 * 删除指定ID的timer
	 * @param id ID
	 */
	public void remove(String id);
}
