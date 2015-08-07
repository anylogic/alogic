package com.alogic.timer.core;

import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 任务执行者
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public interface Doer extends Configurable,XMLConfigurable,Runnable,Reportable{
	
	/**
	 * 状态
	 * 
	 * @author duanyy
	 *
	 */
	public enum State{
		/**
		 * 空闲
		 */
		Idle,
		/**
		 * 工作中
		 */
		Working
	}
	
	/**
	 * 获取Doer状态
	 * @return
	 */
	public State getState();
	
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
	 * 设置当前的任务
	 * @param task 任务
	 */
	public void setCurrentTask(Task task);
	
	/**
	 * 获取当前的任务
	 * @return 当前任务
	 */
	public Task getCurrentTask();
	
	/**
	 * 设置任务状态监听器
	 * @param listener 监听器
	 */
	public void setTaskStateListener(TaskStateListener listener);
	
	/**
	 * 执行
	 * @param task 待执行的任务
	 */
	public void execute(Task task);
	
	/**
	 * 完成任务
	 */
	public void complete();
	
	/**
	 * Abstract
	 * @author duanyy
	 *
	 */
	abstract public static class Abstract implements Doer {
		protected static final Logger logger = LogManager.getLogger(Doer.class);
		/**
		 * 任务状态
		 */
		protected State state = State.Idle;
		
		/**
		 * 上下文持有者
		 */
		private ContextHolder ctxHolder = null;
		
		/**
		 * 当前current
		 */
		private Task current = null;
		
		/**
		 * 状态监听器
		 */
		private TaskStateListener stateListener = null;
		
		public void setContextHolder(ContextHolder holder) {
			ctxHolder = holder;
		}

		public ContextHolder getContextHolder() {
			return ctxHolder;
		}

		public void setCurrentTask(Task task) {
			current = task;
		}

		public Task getCurrentTask() {
			return current;
		}

		public void setTaskStateListener(TaskStateListener listener) {
			stateListener = listener;
		}
		
		public void reportState(Task.State state,int percent){
			if (stateListener != null){
				stateListener.reportState(current, state, percent);
			}
		}
		
		public void run(){
			Task task = getCurrentTask();
			try {
				if (task == null){
					logger.error("Can not execute because the task is null.");
				}else{
					state = State.Working;
					execute(task);
				}
			}catch (Throwable t){
				logger.fatal("Exception when executing the task:" + task.id());
			}finally{
				complete();
			}
		}
		
		public void complete(){
			state = State.Idle;
		}
		
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				xml.setAttribute("state", state.name());
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
				json.put("state", state.name());
			}
		}

		public State getState() {
			return state;
		}

		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);			
		}		
		
		public void configure(Properties p) throws BaseException {
			// nothing to do
		}
	}

	/**
	 * Runnable包裹器
	 * 
	 * @author duanyy
	 *
	 */
	public static class Wrapper extends Abstract{
		protected Runnable real = null;
		
		public Wrapper(Runnable runnable){
			real = runnable;
		}
		
		public void configure(Properties p) throws BaseException {
			// nothing to do
		}

		public void execute(Task task) {
			if (real != null){
				real.run();
			}
		}
	}
	
	/**
	 * Quiet实现
	 * 
	 * <p>仅触发定时，具体逻辑有TaskComitter确定。
	 */
	public static class Quiet extends Abstract{

		public void execute(Task task) {

		}
	}
}
