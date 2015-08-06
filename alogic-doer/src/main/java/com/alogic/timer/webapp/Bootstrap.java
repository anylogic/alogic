package com.alogic.timer.webapp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.alogic.timer.core.Scheduler;
import com.alogic.timer.core.SchedulerFactory;

/**
 * 定时器模块的Web引导程序
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class Bootstrap implements ServletContextListener {
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(Bootstrap.class);
	
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("the scheduler will start..");
		Scheduler scheduler = SchedulerFactory.get();
		scheduler.start();
	}

	public void contextDestroyed(ServletContextEvent sce) {
		Scheduler scheduler = SchedulerFactory.get();
		scheduler.stop();
		logger.info("the scheduler stopped.");
	}

}
