package com.alogic.timer;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 调度者
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public interface Scheduler extends Configurable,XMLConfigurable,Reportable,Runnable {
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
	 * 按照指定的匹配规则调度指定的Runnable
	 * @param id 定时器的ID
	 * @param matcher 匹配器
	 * @param runnable 任务
	 */
	public void schedule(String id,Matcher matcher,Runnable runnable);
	
	/**
	 * 删除指定ID的timer
	 * @param id ID
	 */
	public void remove(String id);
	
	/**
	 * Abstract
	 * @author duanyy
	 *
	 */
	public static class Abstract implements Scheduler{

		@Override
		public void configure(Properties p) throws BaseException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void configure(Element _e, Properties _properties)
				throws BaseException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void report(Element xml) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void report(Map<String, Object> json) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Timer[] getTimers() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Timer get(String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void schedule(Timer timer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void schedule(String id, Matcher matcher, Task task) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void schedule(String id, Matcher matcher, Runnable runnable) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void remove(String id) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
