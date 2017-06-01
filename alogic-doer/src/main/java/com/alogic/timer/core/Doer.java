package com.alogic.timer.core;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.alogic.timer.core.Task.State;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 任务执行者
 * 
 * @author duanyy
 * @since 1.6.3.37
 * 
 * @version 1.6.4.16 [duanyy 20151110] <br>
 * - 根据sonar建议优化代码 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.9.2 [20170525 duanyy] <br>
 * - 增加事件属性，以便和事件处理体系进行对接 <br>
 * 
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 改造TaskCenter模型，以便提供分布式任务处理支持; <br>
 */
public interface Doer extends Configurable,XMLConfigurable,Reportable{
	
	/**
	 * 获取队列id
	 * @return 队列id
	 */
	public String getQueue();
	
	/**
	 * 设置上下文持有者
	 * @param holder 持有者
	 */
	public void setContextHolder(ContextHolder holder);
	
	/**
	 * 获取上下文持有者
	 * @return holder
	 */
	public ContextHolder getContextHolder();
	
	/**
	 * 设置任务状态监听器
	 * @param listener 监听器
	 */
	public void setTaskStateListener(TaskStateListener listener);
	
	/**
	 * 执行
	 * @param task 待执行的任务
	 */
	public void run(Task task);
		
	/**
	 * Abstract
	 * @author duanyy
	 *
	 */
	abstract public static class Abstract implements Doer,TaskStateListener {
		/**
		 * a logger of log4j
		 */
		protected static final Logger LOG = LoggerFactory.getLogger(Doer.class);
		
		/**
		 * 上下文持有者
		 */
		private ContextHolder ctxHolder = null;
		
		/**
		 * 状态监听器
		 */
		private TaskStateListener stateListener = null;
		
		/**
		 * 队列
		 */
		private String queue = "default";
		
		@Override
		public String getQueue(){
			return queue;
		}
		
		@Override
		public void setContextHolder(ContextHolder holder) {
			ctxHolder = holder;
		}

		@Override
		public ContextHolder getContextHolder() {
			return ctxHolder;
		}

		@Override
		public void setTaskStateListener(TaskStateListener listener) {
			stateListener = listener;
		}
		
		@Override
		public void run(Task task){
			try {
				if (task == null){
					LOG.error("Can not execute because the task is null.");
				}else{
					execute(task);
				}
			}catch (Exception t){
				LOG.error("Exception when executing the task:" + task.id());
			}
		}

		abstract protected void execute(Task task);
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
			}
		}

		@Override
		public void configure(Element _e, Properties _properties){
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);			
		}		
		
		@Override
		public void configure(Properties p){
			queue = PropertiesConstants.getString(p,"queue",queue);
		}
		
		@Override
		public void onRunning(String id, State state, int percent,String note) {
			if (stateListener != null){
				stateListener.onRunning(id, state, percent,note);
			}
		}

		@Override
		public void onQueued(String id, State state, int percent,String note) {
			if (stateListener != null){
				stateListener.onQueued(id, state, percent,note);
			}
		}

		@Override
		public void onPolled(String id, State state, int percent,String note) {
			if (stateListener != null){
				stateListener.onPolled(id, state, percent,note);
			}
		}

		@Override
		public void onStart(String id, State state, int percent,String note) {
			if (stateListener != null){
				stateListener.onStart(id, state, percent,note);
			}
		}

		@Override
		public void onFinish(String id, State state, int percent,String note) {
			if (stateListener != null){
				stateListener.onFinish(id, state, percent,note);
			}
		}
	}
	
	/**
	 * Quiet实现
	 * 
	 * <p>仅触发定时，具体逻辑有TaskComitter确定。
	 */
	public static class Quiet extends Abstract{
		@Override
		protected void execute(Task task) {
			// nothing to do
		}

	}
	
	public static class Wrapper extends Abstract{
		protected Runnable runnable = null;
		
		public Wrapper(Runnable run){
			this.runnable = run;
		}
		
		@Override
		protected void execute(Task task) {
			if (this.runnable != null){
				this.runnable.run();
			}
		}
	}
}
