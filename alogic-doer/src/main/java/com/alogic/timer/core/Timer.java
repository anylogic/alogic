package com.alogic.timer.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.alogic.timer.matcher.Crontab;
import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Factory;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 定时器
 * 
 * @author duanyy
 * @since 1.6.3.37
 * 
 * @version 1.6.3.38 [duanyy 20150812] <br>
 * - 增加集群功能 <br>
 * 
 * @version 1.6.4.16 [duanyy 20151110] <br>
 * - 根据sonar建议优化代码 <br>
 * 
 * @version 1.6.5.36 [duanyy 20160729] <br>
 * - 增加stop接口，优化清理工作 <br>
 */
public interface Timer extends Configurable,XMLConfigurable,Reportable {
	/**
	 * 定时器状态
	 * @author duanyy
	 *
	 */
	public enum State {
		Init,Running,Paused,Stopping,Stopped,Idle
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
	 * @param committer 任务提交者
	 */
	public void schedule(DoerCommitter committer);
	
	/**
	 * 暂停定时器
	 */	
	public void pause();
	
	/**
	 * 恢复定时器状态为Running
	 */	
	public void resume();
	
	/**
	 * 停止
	 */
	public void stop();
	
	/**
	 * 预测1个月内下一次调度时间
	 * <br>
	 * 预测需要耗费资源，慎用
	 * @return 下一次调度时间
	 */
	public Date forecastNextDate();	
	
	/**
	 * 是否可以清除
	 * @return true|false
	 */
	public boolean isTimeToClear();
	
	/**
	 * 创建新的任务
	 * @return 任务
	 */
	public Task newTask();
		
	/**
	 * Abstract
	 * @author duanyy
	 * @since 1.6.3.37
	 */
	abstract public static class Abstract implements Timer{		
		/**
		 * logger of log4j
		 */
		protected static final Logger logger = LogManager.getLogger(Timer.class);
		
		/**
		 * 上次调度时间
		 */
		protected Date lastDate = new Date();
		
		/**
		 * 任务执行者
		 */
		protected Doer doer = null;
		
		/**
		 * 匹配器
		 */
		protected Matcher matcher = null;		
		
		/**
		 * 上下文持有者
		 */
		protected ContextHolder ctxHolder = null;
		
		/**
		 * 状态
		 */
		protected State state = State.Running;
		
		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);
		}			
		
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				xml.setAttribute("id", getId());
				xml.setAttribute("state", getState().name());
				
				if (lastDate != null){
					xml.setAttribute("lastDate", String.valueOf(lastDate.getTime()));
				}
				
				String detail = xml.getAttribute("detail");
				
				if (detail == null || !detail.equals("false")){
					Document doc = xml.getOwnerDocument();
					if (ctxHolder != null){
						Element _ctx = doc.createElement("context");
						ctxHolder.report(_ctx);
						xml.appendChild(_ctx);
					}
					
					if (doer != null){
						Element _task = doc.createElement("doer");
						doer.report(_task);
						xml.appendChild(_task);
					}
					
					if (matcher != null){
						Element _matcher = doc.createElement("matcher");
						matcher.report(_matcher);
						xml.appendChild(_matcher);
					}
				}
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
				json.put("id", getId());
				json.put("state", getState().name());
				
				boolean detail = JsonTools.getBoolean(json, "detail", true);
				
				if (lastDate != null){
					json.put("lastDate",lastDate.getTime());
				}
				
				if (detail){
					if (ctxHolder != null){
						Map<String,Object> _ctx = new HashMap<String,Object>();
						ctxHolder.report(_ctx);
						json.put("context", _ctx);
					}
					
					if (doer != null){
						Map<String,Object> _task = new HashMap<String,Object>();
						doer.report(_task);
						json.put("doer", _task);
					}
					
					if (matcher != null){
						Map<String,Object> _matcher = new HashMap<String,Object>();
						matcher.report(_matcher);
						json.put("matcher", _matcher);
					}
				}
			}
		}

		/**
		 * 有效期起始时间
		 * <br>
		 * 定时器必须在有效期之内才会被调度，可以通过config中fromDate变量进行设置，缺省情况下，取当前时间。
		 * 
		 * @return 有效期起始时间
		 */
		abstract public Date fromDate();
		
		/**
		 * 有效期结束时间
		 * <br>
		 * 定时器必须在有效期之内才会被调度，可以通过config中toDate变量进行设置，缺省情况下，取当前时间之后的50年。
		 * 
		 * @return 有效期结束时间
		 */
		abstract public Date toDate();			
		
		public State getState() {
			return state;
		}

		public boolean isTimeToClear(){return matcher != null && matcher.isTimeToClear();}
		
		public void schedule(DoerCommitter committer) {
			synchronized (this) {
				if (getState() != State.Running) {
					// 当前定时器没有定义为Running
					logger.warn("The timer is not running:" + getId());
					return;
				}

				if (doer == null) {
					logger.error("The doer is not set:" + getId());
					return;
				}

				if (matcher == null) {
					logger.error("The matcher is not set:" + getId());
					return;
				}

				if (committer == null) {
					logger.error("The committer is not set:" + getId());
					return;
				}

				Date now = new Date();
				Date fromDate = fromDate();
				Date toDate = toDate();
				
				if (fromDate != null && toDate != null
						&& (now.before(fromDate()) || now.after(toDate()))) {
					// 必须在定时器的有效期之内才能调度
					return;
				}
				
				if (doer.getState() != Doer.State.IDLE) {
					// 不是空闲状态
					return;
				}
				
				boolean match = matcher.match(lastDate, now, ctxHolder);
				if (match) {
					lastDate = now;
					//准备执行
					doer.setContextHolder(ctxHolder);
					// to commit this task
					committer.commit(doer, newTask());
				}
			}
		}

		public void pause() {
			state = State.Paused;
		}

		public void resume() {
			state = State.Running;
		}
		
		public void stop(){
			state = State.Stopped;
		}

		public Date forecastNextDate() {
			long current = System.currentTimeMillis();
			Date __last = lastDate;
			Date __now = null;
			int step = 1000*60;
			int count = 60*24*31;
			
			for (; count > 0 ; current += step,count--){
				__now = new Date(current);
				if (matcher.match(__last, __now, ctxHolder)){
					return __now;
				}
			}
			return null;
		}
		
		/**
		 * 生成一个任务id
		 * @return 任务id
		 */
		protected static String newTaskId(){
			return System.currentTimeMillis()+ randomString(6);
		}
		
		/**
		 * 字符表
		 */
		protected static final char[] Chars = {
		      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
		      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
		      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
		      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
		      '8', '9'
		 };
		
		/**
		 * 按照指定宽度生成随机字符串
		 * @param _width 字符串的宽度
		 * @return 随机字符串
		 */
		static protected String randomString(int _width){
			int width = _width <= 0 ? 6 : _width;
			char [] ret = new char[width];
			Random ran = new Random();
			for (int i = 0 ; i < width ; i ++){
				int intValue = ran.nextInt(62) % 62;
				ret[i] = Chars[intValue];
			}
			
			return new String(ret);
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
		
		public Simple(String _id,Matcher _matcher,Doer _doer){
			id = _id;
			matcher = _matcher;
			doer = _doer;
			ctxHolder = new ContextHolder.Default();
		}		
		
		public Simple(String _id,Matcher _matcher,Runnable runnable){
			id = _id;
			matcher = _matcher;
			doer = new Doer.Wrapper(runnable);
			ctxHolder = new ContextHolder.Default();
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

		public Task newTask() {
			return new Task.Default(newTaskId(), getId());
		}
	}
	
	/**
	 * 基于XML配置的实现
	 * 
	 * @author duanyy
	 * @since 1.6.3.37
	 */
	public static class XMLed extends Abstract{
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
		protected Date toDate = new Date(System.currentTimeMillis() + 50 * 365 * 24 * 60 * 60 * 1000L);				
		
		/**
		 * 队列
		 */
		protected String queue;
		
		/**
		 * 调用参数
		 */
		protected DefaultProperties parameters;
		
		public String getId() {
			return id;
		}

		public void configure(Properties p) throws BaseException {
			id = PropertiesConstants.getString(p,"id","",true);
			if (id == null || id.length() <= 0){
				id = newTimerId();
			}
			
			queue = PropertiesConstants.getString(p,"queue",id,true);
			name = PropertiesConstants.getString(p,"name","",true);
			note = PropertiesConstants.getString(p,"note","",true);
		}

		public void report(Element xml) {
			super.report(xml);
			
			if (xml != null){
				xml.setAttribute("name", name);
				xml.setAttribute("note", note);
				xml.setAttribute("queue", queue);
				
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
				json.put("queue", queue);
				
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
			
			Element _matcher = XmlTools.getFirstElementByPath(_e, "matcher");
			if (_matcher == null){
				logger.error("Can not create matcher : " + getId());
			}else{
				Factory<Matcher> factory = new Factory<Matcher>(){
					public String getClassName(String _module) throws BaseException{
						if (_module.indexOf(".") >= 0){
							return _module;
						}
						return "com.alogic.timer.matcher." + _module;
					}
				};
				matcher = factory.newInstance(_matcher, p, "module", Crontab.class.getName());
			}
			
			Element _task = XmlTools.getFirstElementByPath(_e, "doer");
			if (_task == null){
				logger.error("Can not create doer : " + getId());
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
			
			Element _parameters = XmlTools.getFirstElementByPath(_e, "parameters");
			if (_parameters != null){
				loadParameter(_parameters);
			}
		}
		
		private void loadParameter(Element root) {
			NodeList list = XmlTools.getNodeListByPath(root,"parameter");
			if (list.getLength() > 0){
				parameters = new DefaultProperties();
				
				for (int i = 0 ;i < list.getLength() ; i ++){
					Node n = list.item(i);
					if (n.getNodeType() != Node.ELEMENT_NODE){
						continue;
					}
					
					Element e = (Element)n;
					
					String key = e.getAttribute("id");
					String value = e.getAttribute("value");
				
					if (key == null || value == null || key.length() <= 0){
						continue;
					}
					
					parameters.SetValue(key, value.length() <= 0 ? "true":value);
				}
			}
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
		
		public Task newTask() {
			return parameters == null ? new Task.Default(newTaskId(), getId()) 
			: new Task.Default(newTaskId(), getId(),parameters);
		}
	}
}
