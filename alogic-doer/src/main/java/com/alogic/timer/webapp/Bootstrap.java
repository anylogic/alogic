package com.alogic.timer.webapp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alogic.timer.core.Scheduler;
import com.alogic.timer.core.SchedulerFactory;

/**
 * 定时器模块的Web引导程序
 * 
 * @author duanyy
 * @since 1.6.3.37
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class Bootstrap implements ServletContextListener {
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	
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
