package com.alogic.doer.core;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.alogic.timer.core.ContextHolder;
import com.alogic.timer.core.Doer;
import com.alogic.timer.core.Task;
import com.alogic.timer.core.Task.State;
import com.alogic.timer.core.TaskStateListener;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;


/**
 * 任务争抢者
 * 
 * @author duanyy
 * @since 1.6.3.4
 */
public interface TaskRobber extends TaskDispatcher,Runnable{
	/**
	 * 开始任务处理线程
	 */
	public void start();
	
	/**
	 * 中断任务处理线程（需等待当前处理完成）
	 */
	public void stop();
	
	/**
	 * 等待线程执行完毕
	 * @param timeout 超时等待时间
	 */
	public void join(long timeout)throws InterruptedException;
	
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
	abstract public static class Abstract implements TaskRobber{
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LogManager.getLogger(TaskRobber.class);
		
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
			configure(p);
		}
		
		public void configure(Properties p) throws BaseException {
			timeout = PropertiesConstants.getLong(p, "timeout", timeout);
			interval = PropertiesConstants.getLong(p, "interval", interval);
		}
		
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
			thread.setDaemon(true);
			thread.start();
		}

		public void stop() {
			stopped = true;
		}
		
		public boolean isStopped(){
			return stopped;
		}
		
		public void join(long timeout) throws InterruptedException{
			thread.join(timeout);
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
	 * 缺省实现
	 * @author duanyy
	 *
	 */
	public static class Default extends Abstract implements TaskStateListener {

		public void dispatch(Task task) throws BaseException {
			if (doer != null){
				doer.setCurrentTask(task);
				doer.setTaskStateListener(this);
				doer.setContextHolder(ctxHolder);
				doer.run();
			}
		}
		
		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);
			
			Element _task = XmlTools.getFirstElementByPath(_e, "doer");
			if (_task == null){
				logger.error("Can not create doer");
			}else{
				Factory<Doer> factory = new Factory<Doer>();
				doer = factory.newInstance(_task, p, "module");
			}
			
			Element _context = XmlTools.getFirstElementByPath(_e, "context");
			if (_context == null){
				ctxHolder = new ContextHolder.Default();
			}else{
				Factory<ContextHolder> factory = new Factory<ContextHolder>();
				ctxHolder = factory.newInstance(_context, p, "module", ContextHolder.Default.class.getName());
			}
		}
		
		/**
		 * 实际执行的doer
		 */
		protected Doer doer = null;

		/**
		 * 上下文持有者
		 */
		protected ContextHolder ctxHolder = null;
		
		public void reportState(Task task, State state, int percent) {
			TaskQueue queue = getQueue();
			if (queue != null){
				queue.reportState(task, state, percent);
			}
		}

		public void reportState(String id, State state, int percent) {
			TaskQueue queue = getQueue();
			if (queue != null){
				queue.reportState(id, state, percent);
			}
		}
	}
}