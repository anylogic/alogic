package com.alogic.timer.core;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.timer.core.Scheduler.XMLed;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 调度者工厂
 * 
 * @author duanyy
 *
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class SchedulerFactory extends Factory<Scheduler> {
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LoggerFactory.getLogger(Factory.class);
	
	/**
	 * 缺省的Scheduler实例
	 */
	protected static Scheduler scheduler = null;
	
	/**
	 * a object to lock
	 */
	protected static Object lock = new Object();
	
	public static Scheduler get(){
		if (scheduler != null){
			return scheduler;
		}
		
		synchronized (lock){
			Settings p = Settings.get();
			String configFile = p.GetValue("timer.master", 
					"java:///com/alogic/timer/core/timer.xml#com.alogic.timer.core.Timer");

			String secondaryFile = p.GetValue("timer.secondary", 
					"java:///com/alogic/timer/core/timer.xml#com.alogic.timer.core.Timer");
			
			ResourceFactory rm = Settings.getResourceFactory();
			InputStream in = null;
			try {
				in = rm.load(configFile,secondaryFile, null);
				Document doc = XmlTools.loadFromInputStream(in);
				if (doc != null){
					Element root = doc.getDocumentElement();
					
					SchedulerFactory factory = new SchedulerFactory();
					
					scheduler = factory.newInstance(root, p, "module",XMLed.class.getName());
				}
			} catch (Exception ex){
				logger.error("Error occurs when load xml file,source=" + configFile, ex);
			}finally {
				IOTools.closeStream(in);
			}
			return scheduler;
		}
	}
}
