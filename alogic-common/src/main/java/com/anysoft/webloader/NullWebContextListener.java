package com.anysoft.webloader;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 空的ServletContextListener,用于测试
 * 
 * @author duanyy
 *
 * @since 1.0.0
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class NullWebContextListener implements ServletContextListener {
	protected static Logger logger = LoggerFactory.getLogger(NullWebContextListener.class);
	
	public void contextDestroyed(ServletContextEvent e) {

	}

	public void contextInitialized(ServletContextEvent e) {
		ServletContext sc = e.getServletContext();
		logger.info("Testing.....");
		Enumeration<String> names = sc.getInitParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String value = sc.getInitParameter(name);
			logger.info(name + "=" + value);
		}
		logger.info("Test end.");
	}

}
