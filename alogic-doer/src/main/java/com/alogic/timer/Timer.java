package com.alogic.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 定时器
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public interface Timer extends XMLConfigurable,Reportable {
	/**
	 * 定时器状态
	 * @author duanyy
	 *
	 */
	public enum State {
		Running,Paused
	}
	/**
	 * 获取定时器ID
	 * @return ID
	 */
	public String getId();
	
	/**
	 * 获取定时器状态
	 * <br>
	 * 定时器状态包括：Running|Paused 
	 * 
	 * @return state
	 */	
	public State getState();
	
	/**
	 * 调度
	 */
	public void schedule();
	
	/**
	 * 暂停定时器
	 */	
	public void pause();
	
	/**
	 * 恢复定时器状态为Running
	 */	
	public void resume();
	
	/**
	 * 预测1个月内下一次调度时间
	 * <br>
	 * 预测需要耗费资源，慎用
	 * @return 下一次调度时间
	 */
	public Date forecastNextDate();	
	
	/**
	 * 有效期起始时间
	 * <br>
	 * 定时器必须在有效期之内才会被调度，可以通过config中fromDate变量进行设置，缺省情况下，取当前时间。
	 * 
	 * @return 有效期起始时间
	 */
	public Date fromDate();
	
	/**
	 * 有效期结束时间
	 * <br>
	 * 定时器必须在有效期之内才会被调度，可以通过config中toDate变量进行设置，缺省情况下，取当前时间之后的50年。
	 * 
	 * @return 有效期结束时间
	 */
	public Date toDate();	
	
	/**
	 * 根据环境变量配置
	 * 
	 * @param p 环境变量
	 * @throws BaseException
	 */
	public void configure(Properties p) throws BaseException;
	
	/**
	 * Abstract
	 * @author duanyy
	 * @since 1.6.3.37
	 */
	abstract public static class Abstract implements Timer{		
		/**
		 * 上次调度时间
		 */
		protected Date lastDate = null;
		
		/**
		 * 待调度的任务
		 */
		protected Task task = null;
		
		/**
		 * 匹配器
		 */
		protected Matcher matcher = null;		
		
		/**
		 * 状态
		 */
		protected State state = State.Running;

		/**
		 * 上下文
		 */
		protected DefaultProperties ctx = new DefaultProperties();
		
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				xml.setAttribute("id", getId());
				xml.setAttribute("state", getState().name());
				
				if (lastDate != null){
					xml.setAttribute("lastDate", String.valueOf(lastDate.getTime()));
				}
				
				Document doc = xml.getOwnerDocument();
				if (ctx != null){
					Element _ctx = doc.createElement("context");
					ctx.toXML(_ctx);
					xml.appendChild(_ctx);
				}
				
				if (task != null){
					Element _task = doc.createElement("task");
					task.report(_task);
					xml.appendChild(_task);
				}
				
				if (matcher != null){
					Element _matcher = doc.createElement("matcher");
					matcher.report(_matcher);
					xml.appendChild(_matcher);
				}
				
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
				json.put("id", getId());
				json.put("state", getState().name());
				
				if (lastDate != null){
					json.put("lastDate",lastDate.getTime());
				}
				
				if (ctx != null){
					Map<String,Object> _ctx = new HashMap<String,Object>();
					ctx.toJson(_ctx);
					json.put("context", _ctx);
				}
				
				if (task != null){
					Map<String,Object> _task = new HashMap<String,Object>();
					task.report(_task);
					json.put("task", _task);
				}
				
				if (matcher != null){
					Map<String,Object> _matcher = new HashMap<String,Object>();
					matcher.report(_matcher);
					json.put("matcher", _matcher);
				}
			}
		}

		public State getState() {
			return state;
		}

		public void schedule() {
			synchronized(this){
				if (getState() != State.Running){
					//当前定时器没有定义为Running
					return ;
				}

				if (task != null && matcher != null){
					Date now = new Date();
					Date fromDate = fromDate();
					Date toDate = toDate();
					if (fromDate != null && toDate != null && (now.before(fromDate()) || now.after(toDate()))){
						//必须在定时器的有效期之内才能调度
						return ;
					}
					
					if (task.getState() != Task.State.Idle){
						//不是空闲状态
						return ;
					}
					boolean match = matcher.match(lastDate,now,ctx);
					if (match){
						lastDate = now;
						//to do this task
					}
				}
			}
		}

		public void pause() {
			state = State.Paused;
		}

		public void resume() {
			state = State.Running;
		}

		public Date forecastNextDate() {
			long current = System.currentTimeMillis();
			Date __last = lastDate;
			Date __now = null;
			int step = 1000*60;
			int count = 60*24*31;
			
			DefaultProperties ctx = new DefaultProperties();
			for (; count > 0 ; current += step,count--){
				__now = new Date(current);
				if (matcher.match(__last, __now, ctx)){
					return __now;
				}
			}
			return null;
		}		
	}
	
	/**
	 * 简单实现
	 * @author duanyy
	 *
	 */
	public static class Simple extends Abstract{
		/**
		 * 定时器ID
		 */
		protected String id;
		
		public Simple(String _id,Matcher _matcher,Task _task){
			id = _id;
			matcher = _matcher;
			task = _task;
		}		
		
		public void configure(Element _e, Properties _properties)
				throws BaseException {
			// Can not be configured with xml
		}

		public String getId() {
			return id;
		}
		public void configure(Properties p) throws BaseException {
			// nothing to do
		}

		public Date fromDate() {
			return null;
		}

		public Date toDate() {
			return null;
		}
	}
	
	/**
	 * 基于XML配置的实现
	 * 
	 * @author duanyy
	 * @since 1.6.3.37
	 */
	public static class XML extends Abstract{
		/**
		 * 定时器ID
		 */
		protected String id;

		/**
		 * 名称
		 */
		protected String name;
		
		/**
		 * 说明
		 */
		protected String note;
		
		/**
		 * 有效期起始时间
		 * <br>
		 * 定时器必须在有效期之内才会被调度，可以通过config中fromDate变量进行设置，缺省情况下，取当前时间。
		 */
		protected Date fromDate = new Date();
		/**
		 * 有效期结束时间
		 * <br>
		 * 定时器必须在有效期之内才会被调度，可以通过config中toDate变量进行设置，缺省情况下，取当前时间之后的50年。
		 */
		protected Date toDate = new Date(System.currentTimeMillis() + 50 * 365 * 24 * 60 * 60 * 1000);				
		
		public String getId() {
			return id;
		}

		public void configure(Properties p) throws BaseException {
			id = PropertiesConstants.getString(p,"id","",true);
			if (id == null || id.length() <= 0){
				id = newTimerId();
			}
			
			name = PropertiesConstants.getString(p,"name","",true);
			note = PropertiesConstants.getString(p,"note","",true);
		}

		public void report(Element xml) {
			super.report(xml);
			
			if (xml != null){
				xml.setAttribute("name", name);
				xml.setAttribute("note", note);
				
				if (fromDate != null){
					xml.setAttribute("fromDate", String.valueOf(fromDate.getTime()));
				}
				
				if (toDate != null){
					xml.setAttribute("toDate", String.valueOf(toDate.getTime()));
				}
			}
		}

		public void report(Map<String, Object> json) {
			super.report(json);
			
			if (json != null){
				json.put("name", name);
				json.put("note", note);
				
				if (fromDate != null){
					json.put("fromDate", fromDate.getTime());
				}
				
				if (toDate != null){
					json.put("toDate", toDate.getTime());
				}
			}
		}		
		
		@Override
		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);	
		}
		
		private static volatile int seed = 10001;
		
		synchronized protected String newTimerId(){
			return "timer" + String.valueOf(seed ++);
		}

		public Date fromDate() {
			return fromDate;
		}

		public Date toDate() {
			return toDate;
		}
		
	}
}
