package com.alogic.doer.core;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.timer.core.ContextHolder;
import com.alogic.timer.core.Doer;
import com.alogic.timer.core.DoerContext;
import com.alogic.timer.core.Task;
import com.alogic.timer.core.Task.State;
import com.alogic.timer.core.TaskStateListener;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 任务中心
 * 
 * @author duanyy
 * @since 1.6.3.4
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 改造TaskCenter模型，以便提供分布式任务处理支持; <br>
 */
public interface TaskCenter extends TaskDispatcher,TaskStateListener,ContextHolder{
	
	/**
	 * 启动任务中心处理
	 */
	public void start();
	
	/**
	 * 请求任务
	 * <p>
	 * 向队列请求任务，本方法将阻塞直至有任务分发给该dispatcher或者时间超时。
	 * 
	 * @param queue 指定的队列
	 * @param dispatcher 任务处理人
	 * @param timeout 超时时间
	 * 
	 */
	public int askForTask(String queue,TaskDispatcher dispatcher,long timeout);	
	
	/**
	 * 停止任务中心
	 */
	public void stop();
	
	/**
	 * 等待线程执行完毕
	 * @param timeout 超时等待时间
	 */
	public void join(long timeout);	
		
	/**
	 * 空的任务中心实现
	 * @author duanyy
	 * @since 1.6.3.4
	 */
	public static class Null implements TaskCenter{
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LoggerFactory.getLogger(TaskCenter.class);
		
		protected DoerContext ctx = new DoerContext();
		
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
			}
		}
		
		public void configure(Properties p) {

		}
		
		public void configure(Element _e, Properties _properties){
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);			
		}

		@Override
		public void dispatch(String queue,Task task) {
			logger.error("This is a null task center.");
		}

		@Override
		public int askForTask(String queue, TaskDispatcher dispatcher,
				long timeout) {
			logger.error("This is a null task center.");
			return 0;
		}

		@Override
		public void start() {
			logger.error("This is a null task center.");
		}

		@Override
		public void stop() {
			logger.error("This is a null task center.");
		}

		@Override
		public void join(long timeout){
			logger.error("This is a null task center.");
		}

		public DoerContext getContext() {
			return ctx;
		}

		public void saveContext(DoerContext _ctx, Doer task) {
			ctx = _ctx;
		}

		@Override
		public void onRunning(String id, State state, int percent,String note) {
			// nothing to do
		}

		@Override
		public void onQueued(String id, State state, int percent,String note) {
			// nothing to do
		}

		@Override
		public void onPolled(String id, State state, int percent,String note) {
			// nothing to do
		}

		@Override
		public void onStart(String id, State state, int percent,String note) {
			// nothing to do
		}

		@Override
		public void onFinish(String id, State state, int percent,String note) {
			// nothing to do
		}	
	}
	
	/**
	 * 工厂类
	 * @author duanyy
	 * @since 1.6.3.4
	 * 
	 */
	public static class TheFactory extends Factory<TaskCenter>{
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LoggerFactory.getLogger(TaskCenter.class);
		
		/**
		 * TaskCenter的实例
		 */
		protected static TaskCenter theCenter = null;
		
		/**
		 * 客户端
		 */
		protected static TaskDispatcher client = null;
		
		/**
		 * 缺省配置文件位置
		 */
		protected static final String DEFAULT = 
				"java:///com/alogic/doer/core/doer.xml#com.alogic.doer.core.TaskCenter";
	
		/**
		 * 获取全局唯一实例
		 * @return 全局唯一实例
		 */
		public static TaskCenter get(){
			if (theCenter == null){
				synchronized (TheFactory.class){
					if (theCenter == null){
						Settings p = Settings.get();
						String master = p.GetValue("tc.master",DEFAULT);
						String secondary = p.GetValue("tc.secondary",DEFAULT);						
						theCenter = get(master,secondary,p);
					}
				}
			}			
			return theCenter;			
		}
		
		/**
		 * 通过指定的配置文件来创建TaskCenter
		 * @param master 主配置文件
		 * @param secondary 从配置文件
		 * @param p 环境变量
		 * @return 根据配置文件所创建的实例
		 */
		protected static TaskCenter get(String master,String secondary,Properties p){
			ResourceFactory rm = Settings.getResourceFactory();
			InputStream in = null;
			try {
				in = rm.load(master,secondary, null);
				Document doc = XmlTools.loadFromInputStream(in);
				if (doc != null){
					Element root = doc.getDocumentElement();
					TheFactory factory = new TheFactory();					
					return factory.newInstance(root, p, "module",Null.class.getName());
				}
			} catch (Exception ex){
				logger.error("Error occurs when load xml file,source=" + master, ex);
			}finally {
				IOTools.closeStream(in);
			}
			return null;
		}
	}
}
