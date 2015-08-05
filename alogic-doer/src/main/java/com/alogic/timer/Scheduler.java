package com.alogic.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;

/**
 * 调度者
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public interface Scheduler extends Timer,Runnable {
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
	 * 设置任务提交者
	 * @param committer 任务提交者
	 */
	public void setTaskCommitter(TaskCommitter committer);
	
	/**
	 * 删除指定ID的timer
	 * @param id ID
	 */
	public void remove(String id);
	
	/**
	 * 开始调度
	 */
	public void start();
	
	/**
	 * 停止调度
	 */
	public void stop();
	
	/**
	 * Abstract
	 * @author duanyy
	 *
	 */
	abstract public static class Abstract implements Scheduler{
		protected static final Logger logger = LogManager.getLogger(Timer.class);
		protected State state = State.Running;
		protected TaskCommitter comitter = null;
		protected long interval = 1000;
		
		public void setTaskCommitter(TaskCommitter _committer){
			comitter = _committer;
		}
		
		@Override
		public void configure(Properties p) throws BaseException {
			interval = PropertiesConstants.getLong(p,"interval",interval,true);
		}

		@Override
		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				
				Timer[] timers = getTimers();
				if (timers.length > 0){
					Document doc = xml.getOwnerDocument();
					for (int i = 0 ; i < timers.length ; i ++){
						Timer t = timers[i];
						
						Element _timer = doc.createElement("timer");
						t.report(_timer);
						
						xml.appendChild(_timer);
					}
				}
				
				if (comitter != null){
					Document doc = xml.getOwnerDocument();
					Element _committer = doc.createElement("committer");
					comitter.report(_committer);
					xml.appendChild(_committer);
				}
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
				
				Timer[] timers = getTimers();
				if (timers.length > 0){
					List<Object> _timer = new ArrayList<Object>();
					
					for (int i = 0 ; i < timers.length ; i ++){
						Timer t = timers[i];
						
						Map<String,Object> _map = new HashMap<String,Object>();
						t.report(_map);
						
						_timer.add(_map);
					}
					
					json.put("timer", _timer);
				}
				
				if (comitter != null){
					Map<String,Object> _comitter = new HashMap<String,Object>();
					comitter.report(_comitter);
					json.put("comitter", _comitter);
				}
			}
		}

		public void schedule(String id, Matcher matcher, Task task) {
			schedule(new Timer.Simple(id, matcher, task));
		}

		public void schedule(String id, Matcher matcher, Runnable runnable) {
			schedule(new Timer.Simple(id, matcher, runnable));
		}

		public String getId() {
			return "root";
		}

		public State getState() {
			return state;
		}

		public void pause() {
			state = State.Paused;
		}

		public void resume() {
			state = State.Running;
		}
		
		public void schedule(TaskCommitter committer) {
			// do noting
		}
		
		public void start() {
			exec.scheduleAtFixedRate(this, 0,1000, TimeUnit.MILLISECONDS);
		}		
		
		public void stop(){
			exec.shutdown();
		}
		
		@Override
		public Date forecastNextDate() {
			return new Date();
		}
		
		protected ScheduledThreadPoolExecutor exec = new  ScheduledThreadPoolExecutor(5);
		
		public boolean isTimeToClear(){return false;}
	}
	
	/**
	 * 简单实现
	 * @author duanyy
	 *
	 */
	public static class Simple extends Abstract{
		protected Hashtable<String,Timer> timers = new Hashtable<String,Timer>();
		
		@Override
		public Timer[] getTimers() {
			return timers.values().toArray(new Timer[0]);
		}

		@Override
		public Timer get(String id) {
			return timers.get(id);
		}

		@Override
		public void schedule(Timer timer) {
			timers.put(timer.getId(), timer);
		}

		@Override
		public void remove(String id) {
			timers.remove(id);
		}
		
		@Override
		public void run() {
			try {
				Iterator<Timer> iter = timers.values().iterator();
				List<String> toBeClear = new ArrayList<String>();
				
				while (iter.hasNext()){
					Timer timer = iter.next();
					timer.schedule(comitter);
					if (timer.isTimeToClear()){
						toBeClear.add(timer.getId());
					}
				}
				
				for (String id:toBeClear){
					timers.remove(id);
				}
			}catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
}
