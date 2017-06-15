package com.alogic.doer.webapp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alogic.doer.core.TaskCenter;

/**
 * Bootstrap
 * @author yyduan
 * 
 * @since 1.6.9.3
 */
public class Bootstrap implements ServletContextListener {
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("The task center will start..");
		TaskCenter tc = TaskCenter.TheFactory.get();
		tc.start();
	}

	public void contextDestroyed(ServletContextEvent sce) {
		TaskCenter tc = TaskCenter.TheFactory.get();
		tc.stop();
		logger.info("The task center has stopped.");
	}

}