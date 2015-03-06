package com.alogic.doer.core;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.alogic.doer.core.TaskReport.TaskState;
import com.anysoft.util.BaseException;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;


/**
 * 任务处理者
 * 
 * @author duanyy
 * @since 1.6.3.4
 */
public interface TaskDoer extends TaskDispatcher,Runnable{
	/**
	 * 开始任务处理线程
	 */
	public void start();
	
	/**
	 * 中断任务处理线程（需等待当前处理完成）
	 */
	public void stop();
	
	/**
	 * 是否已经中断
	 * @return true | false
	 */
	public boolean isStopped();
	
	/**
	 * 设置任务队列
	 * @param queue 队列实例
	 */
	public void setTaskQueue(TaskQueue queue);
	
	/**
	 * 获取当前的任务队列
	 * @return 队列实例
	 */
	public TaskQueue getQueue();
	
	/**
	 * 虚基类
	 * @author duanyy
	 * @since 1.6.3.4
	 */
	abstract public static class Abstract implements TaskDoer{
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LogManager.getLogger(TaskDoer.class);
		
		/**
		 * 线程句柄
		 */
		protected Thread thread = null;
		protected boolean stopped = false;
		protected long timeout = 10000;
		protected long interval = 1000;
		protected TaskQueue queue = null;
		
		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			timeout = PropertiesConstants.getLong(p, "timeout", timeout);
			interval = PropertiesConstants.getLong(p, "interval", interval);
			
			onConfigure(_e,new XmlElementProperties(_e,_properties));
		}

		/**
		 * 处理configure事件
		 * @param _e XML配置节点
		 * @param p 变量集
		 */
		public abstract void onConfigure(Element _e,Properties p);
		
		public void setTaskQueue(TaskQueue _queue){
			queue = _queue;
		}
		
		/**
		 * 获取当前的任务队列
		 * @return 队列实例
		 */
		public TaskQueue getQueue(){return queue;}
		
		public void start() {
			thread = new Thread(this);
			thread.start();
		}

		public void stop() {
			stopped = true;
		}
		
		public boolean isStopped(){
			return stopped;
		}
		
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				xml.setAttribute("timeout", String.valueOf(timeout));
				xml.setAttribute("interval", String.valueOf(interval));
				xml.setAttribute("stopped", Boolean.toString(stopped));
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json, "module", getClass().getName());
				JsonTools.setLong(json,"timeout",timeout);
				JsonTools.setLong(json,"interval",interval);
				JsonTools.setBoolean(json, "stopped", stopped);
			}
		}
		
		public void run(){
			stopped = false;
			while (!stopped){
				try {
					TaskQueue queue = getQueue();
					queue.askForTask(this, timeout);
					Thread.sleep(interval);
				}catch (Exception ex){
					
				}
			}
		}
	}
	
	/**
	 * 空的任务处理者实现
	 * 
	 * @author duanyy
	 * @since 1.6.3.4
	 */
	public static class Null extends Abstract {

		public void dispatch(Task task) throws BaseException {
			TaskQueue queue = getQueue();
			
			queue.reportTaskState(task.id(), TaskState.Running, 0);
			logger.info("\tid\t:" + task.id());
			logger.info("\tqueue\t:" + task.queue());
			logger.info("\tparameters\t:" + task.getParameters().toString());
			
			for (int i = 0; i < 100 ;i ++){
				queue.reportTaskState(task.id(), TaskState.Running, i*10);
				
				try {
					Thread.sleep(2000);
				}catch (Exception ex){
					
				}
			}
			
			queue.reportTaskState(task.id(), TaskState.Done, 10000);
		}

		@Override
		public void onConfigure(Element _e, Properties p) {
			// nothing to do
		}
		
	}
}