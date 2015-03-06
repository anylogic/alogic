package com.alogic.doer.core;

import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 任务中心
 * 
 * @author duanyy
 * @since 1.6.3.4
 * 
 */
public interface TaskCenter extends TaskDispatcher{
	
	/**
	 * 通过ID和队列ID查找任务，并生成TaskReport
	 * 
	 * @param id 任务ID
	 * @param queue 队列ID
	 * @return task report
	 */
	public TaskReport getTaskReport(String id,String queue);
	
	/**
	 * 空的任务中心实现
	 * @author duanyy
	 * @since 1.6.3.4
	 */
	public static class Null implements TaskCenter{
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LogManager.getLogger(TaskCenter.class);
		
		
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

		public void configure(Element _e, Properties _properties)
				throws BaseException {
			// do nothing
		}

		public void dispatch(Task task) {
			logger.error("A task is submitted,but i do not know how to do");
		}

		public TaskReport getTaskReport(String id, String queue) {
			return null;
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
		protected static final Logger logger = LogManager.getLogger(TaskCenter.class);
		
		/**
		 * TaskCenter的实例
		 */
		protected static TaskCenter theCenter = null;
		
		/**
		 * a object to lock
		 */
		protected static Object lock = new Object();
				
		public static TaskCenter get(){
			if (theCenter != null){
				return theCenter;
			}
			
			synchronized (lock){
				Settings p = Settings.get();
				String configFile = p.GetValue("doer.master", 
						"java:///com/alogic/doer/core/doer.xml#com.alogic.doer.core.TaskCenter");

				String secondaryFile = p.GetValue("doer.secondary", 
						"java:///com/alogic/doer/core/doer.xml#com.alogic.doer.core.TaskCenter");
				
				ResourceFactory rm = Settings.getResourceFactory();
				InputStream in = null;
				try {
					in = rm.load(configFile,secondaryFile, null);
					Document doc = XmlTools.loadFromInputStream(in);
					if (doc != null){
						Element root = doc.getDocumentElement();
						
						TheFactory factory = new TheFactory();
						
						theCenter = factory.newInstance(root, p, "module",Null.class.getName());
					}
				} catch (Exception ex){
					logger.error("Error occurs when load xml file,source=" + configFile, ex);
				}finally {
					IOTools.closeStream(in);
				}
				return theCenter;
			}
		}
	}
}
