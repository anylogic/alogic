package com.alogic.timer.core;

import java.io.InputStream;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

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
	public void schedule(String id,Matcher matcher,Doer task);
	
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
	public void setTaskCommitter(DoerCommitter committer);
	
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
		protected DoerCommitter comitter = null;
		protected long interval = 1000;
		
		public void setTaskCommitter(DoerCommitter _committer){
			comitter = _committer;
		}
		
		public void configure(Properties p) throws BaseException {
			interval = PropertiesConstants.getLong(p,"interval",interval,true);
		}

		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);
		}

		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				
				Timer[] timers = getTimers();
				if (timers.length > 0){
					Document doc = xml.getOwnerDocument();
					for (int i = 0 ; i < timers.length ; i ++){
						Timer t = timers[i];
						
						Element _timer = doc.createElement("timer");
						_timer.setAttribute("detail", "false");
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

		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
				
				Timer[] timers = getTimers();
				if (timers.length > 0){
					List<Object> _timer = new ArrayList<Object>();
					
					for (int i = 0 ; i < timers.length ; i ++){
						Timer t = timers[i];
						
						Map<String,Object> _map = new HashMap<String,Object>();
						_map.put("detail", "false");
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

		public void schedule(String id, Matcher matcher, Doer task) {
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
		
		public void schedule(DoerCommitter committer) {
			// do noting
		}
		
		public void start() {
			exec.scheduleAtFixedRate(this, 0,1000, TimeUnit.MILLISECONDS);
		}		
		
		public void stop(){
			exec.shutdownNow();
		}
		
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
		
		public Timer[] getTimers() {
			return timers.values().toArray(new Timer[0]);
		}

		public Timer get(String id) {
			return timers.get(id);
		}

		public void schedule(Timer timer) {
			timers.put(timer.getId(), timer);
		}

		public void remove(String id) {
			timers.remove(id);
		}
		
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
		
		public Task newTask() {
			//never used
			return null;
		}
	}
	
	/**
	 * 基于XML配置的实现
	 * 
	 * @author duanyy
	 *
	 */
	public static class XMLed extends Simple{
		/**
		 * 缺省的Timer实现
		 */
		protected String dftTimer = Timer.XMLed.class.getName();
		
		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			
			dftTimer = PropertiesConstants.getString(p,"dftTimerClass",dftTimer,true);
			
			configure(p);
			
			Element _committer = XmlTools.getFirstElementByPath(_e, "committer");
			if (_committer == null){
				comitter = new ThreadPoolTaskCommitter();
				comitter.configure(_e, _properties);
				logger.warn("Can not find committer element,Use default:" + comitter.getClass().getName());
			}else{
				Factory<DoerCommitter> factory = new Factory<DoerCommitter>();
				comitter = factory.newInstance(_committer, p, "module", ThreadPoolTaskCommitter.class.getName());
			}
			
			loadTimerFromElement(_e,p);
		}
		
		protected void loadTimerFromElement(Element root,Properties p){
			loadIncludeFiles(root,p);
			
			NodeList timerList = XmlTools.getNodeListByPath(root, "timer");
			Factory<Timer> factory = new Factory<Timer>();
			
			for (int i = 0 ;i < timerList.getLength(); i ++){
				Node n = timerList.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				Element e = (Element)n;
				
				try {
					Timer timer = factory.newInstance(e, p, "module", dftTimer);
					if (timer != null){
						schedule(timer);
					}
				}catch (Exception ex){
					logger.error("Can not create timer",ex);
				}
			}
		}
		
		protected void loadIncludeFiles(Element root,Properties p){
			NodeList includes = XmlTools.getNodeListByPath(root, "include");
			
			for (int i = 0 ;i < includes.getLength() ; i++){
				Node n = includes.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				Element e = (Element)n;
				String src = e.getAttribute("src");
				if (src == null || src.length() <= 0){
					continue;
				}
				
				Document doc = loadDocument(p.transform(src),null);
				if (doc == null){
					continue;
				}
				
				loadTimerFromElement(doc.getDocumentElement(),p);
			}
		}
		
		/**
		 * 从主/备地址中装入文档
		 * 
		 * @param master 主地址
		 * @param secondary 备用地址
		 * @return XML文档
		 */
		protected static Document loadDocument(String master,String secondary){
			ResourceFactory rm = Settings.getResourceFactory();
			if (null == rm){
				rm = new ResourceFactory();
			}
			
			Document ret = null;
			InputStream in = null;
			try {
				in = rm.load(master,secondary, null);
				ret = XmlTools.loadFromInputStream(in);		
			} catch (Exception ex){
				logger.error("Error occurs when load xml file,source=" + master, ex);
			}finally {
				IOTools.closeStream(in);
			}		
			return ret;
		}
	}

}
