package com.anysoft.webloader;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * 空的ServletContextListener,用于测试
 * 
 * @author duanyy
 *
 * @since 1.0.0
 */
public class NullWebContextListener implements ServletContextListener {
	protected static Logger logger = LogManager.getLogger(NullWebContextListener.class);
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
