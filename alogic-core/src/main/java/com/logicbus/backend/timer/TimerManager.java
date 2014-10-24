package com.logicbus.backend.timer;

import java.util.Date;
import java.util.Vector;
import java.lang.InterruptedException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.backend.timer.matcher.Interval;

public class TimerManager {
	private TimerManager(){
		start();
	}
	static protected TimerManager instance = null;
	protected Vector<Timer> timers = new Vector<Timer>();
	public Timer [] getTimers(){
		synchronized (lock){
			return timers.toArray(new Timer[0]);
		}
	}
	public Timer getTimer(String _id){
		synchronized (lock){
			Timer __found = null;
			for (int i = 0 ; i < timers.size() ; i ++){
				__found = timers.get(i);
				if (__found.id().equals(_id)){
					break;
				}
			}
			return __found;
		}
	}
	/**
	 * 获取唯一实例
	 * @return 唯一实例
	 */
	static synchronized public TimerManager get(){
		if (instance == null){
			instance = new TimerManager();
		}
		return instance;
	}
	
	protected ClassLoader classLoader = null;

	static synchronized public TimerManager get(String className,ClassLoader cl){
		if (instance == null){
			try {
				instance = (TimerManager)cl.loadClass(className).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				try {
					instance = (TimerManager)cl.loadClass("com.logicbus.backend.timer.TimerManager").newInstance();
				} catch (Exception ex) {
					instance = new TimerManager();
				}
			}
			instance.classLoader = cl;
		}
		return instance;
	}	
	
	/**
	 * 清除当前所有的定时器配置
	 */
	public void clear(){
		synchronized (lock){
			timers.clear();
		}
	}
	
	/**
	 * 调度
	 * @param _timer 定时器
	 */
	public void schedule(Timer _timer){
		synchronized (lock){
			StringBuffer msg = new StringBuffer();
			msg.append("Timer added(id=");
			msg.append(_timer.getTimerHealth().id);
			msg.append(";name=");
			msg.append(_timer.getTimerHealth().name);
			msg.append(";note=");
			msg.append(_timer.getTimerHealth().note);
			msg.append(")");
			logger.info(msg);
			timers.add(_timer);
		}
	}
	
	protected TimerLogListener logListener = null;
	
	/**
	 * 设置日志监听器
	 * @param _logListener
	 */
	public void putLogListener(TimerLogListener _logListener){
		logListener = _logListener;
	}
	
	/**
	 * 调度
	 * @param _matcher 日期迭代器
	 * @param _task 调度任务
	 * @param _config 配置信息
	 */
	public void schedule(Matcher _matcher,Task _task,Properties _config){
		schedule(new Timer(_matcher,_task,_config,logListener));
	}

	/**
	 * 调度
	 * 
	 * <p>从_config中获取Matcher的类名和Task的类名从而生成Matcher和Task的实例.</p>
	 * <p>参数为：</p>
	 * - matcher:Matcher的类名,如果Matcher类在com.logicbus.backend.timer.matcher包中，则无需全路径.
	 * - task:Task的类名，需要全路径
	 * 
	 * @param _config 配置信息
	 */
	public void schedule(Properties _config){
		Matcher matcher = newMatcher(_config.GetValue("matcher",""),_config);
		if (matcher == null){
			return ;
		}
		Task task = newTask(_config.GetValue("task",""),_config);
		if (task == null){
			return ;
		}
		
		schedule(matcher,task,_config);
	}
	
	/**
	 * 调度
	 * 
	 * <p>从XML文档中读取信息进行调度，可同时调度一到多个Timer</p>
	 * <p>一个典型的定时器XML文档如下：</p>
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * <timers logListener="com.logicbus.backend.timer.DefaultLogListener">
	 * 		<timer name="timer1" matcher="Crontab" crontab="30 08 01 * *" task="taskName"/>
	 * 		<timer name="timer2" matcher="Interval" interval="3000000" task="taskName2"/>
	 * </timers>
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * @param xmlDoc
	 */
	public void schedule(Element xmlDoc){
		if (xmlDoc == null){
			return ;
		}
		
		Element __root = xmlDoc;
		XmlElementProperties __rootConfig = new XmlElementProperties(__root,Settings.get());
		putLogListener(newLogListener(__rootConfig.GetValue("logListener",""),__rootConfig));
		
		NodeList __children = __root.getChildNodes();
		for (int i = 0 ; i < __children.getLength() ; i ++){
			Node __node = __children.item(i);
			if (__node.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			Element __e = (Element)__node;
			if (__e.getNodeName().equals("timer")){
				XmlElementProperties __config = new XmlElementProperties(__e,__rootConfig);
				schedule(__config);
			}
		}
	}
	
	protected TimerLogListener newLogListener(String _class,Properties _config){
		try {
			return (TimerLogListener) Class.forName(_class).newInstance();
		} catch (Exception ex){
			ex.printStackTrace();
			return new DefaultLogListener();
		}		
	}
	protected Matcher newMatcher(String _class,Properties _config){
		String __className = _class;
		if (_class.indexOf(".") < 0){
			__className = "com.logicbus.backend.timer.matcher." + _class;
		}
		Matcher instance = null;
		try {
			instance = (Matcher) classLoader.loadClass(__className).newInstance();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return instance;
	}
	
	protected Task newTask(String _class,Properties _config){
		Task instance = null;
		try {
			instance = (Task) classLoader.loadClass(_class).newInstance();
		} catch (Exception ex){
			ex.printStackTrace();
		}		
		return instance;
	}
	
	private boolean toStop = false;
	private WorkerThread workerThread = new WorkerThread();
	/**
	 * 停止调度队列（下一次循环开始）
	 */
	public void stop(){
		toStop = true;
		workerThread.interrupt();
	}
	
	/**
	 * 开始调度队列
	 */
	public void start(){
		if (workerThread.isAlive()) return ;			
		toStop = false;
		workerThread.setDaemon(false);
		workerThread.start();
	}
	
	protected static Logger logger = LogManager.getLogger(TimerManager.class);
	
	/**
	 * 后台工作进程
	 * 
	 * <br>对所有的后台进程进行调度
	 * @author duanyy
	 *
	 */
	class WorkerThread extends Thread{
		public void run(){
			logger.info("Timer worker thread start...");
			while (!toStop && !interrupted()){
				synchronized(lock){
					for (int i = 0 ; i < timers.size() ; i ++){
						Timer timer = timers.get(i);
						timer.schedule();
					}
				}
				
				synchronized(lock){
					for (int i = timers.size() - 1 ; i >= 0 ; i --){
						Timer timer = timers.get(i);
						if (timer.isTimeToClear()){
							timers.remove(i);
						}
					}
				}				
				
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					break;
				}
			}
			logger.info("Timer worker thread end.");
		}
	}
	private Object lock = new Object();
	
	public static void main(String[] args) {
		TimerManager tm = TimerManager.get();
		
		Matcher iterator = new Interval(10000);
		
		Task task = new Task(){
			public Object createContext(Properties _config) {
				return new Object();
			}
			public void run(Object _context, Properties _config,TaskListener _task) {
				System.out.println(new Date() + ",i am running");
			}
		};
		
		tm.schedule(iterator,task,null);		
	}
}
