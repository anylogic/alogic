package com.anysoft.webloader;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.util.DefaultProperties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.SystemProperties;

/**
 * WebContext监听器
 *  
 * <br>
 * 部署在web.xml中，监听应用服务器对WebContext的管理行为。<br>
 *  
 * 可以在web.xml的context-param中配置参数，常见的参数如下：<br>
 * 
 * - updater.auto<br>
 * 是否从服务器自动下载更新库文件，可选值为true|false，缺省值为false.
 * 如果定义为true,在每次WebContext初始化时，将调用updater.metadata.master和updater.metadata.secondary参数所指向的资源，
 * 检查库文件是否有更新.如果有更新，则自动下载到updater.home所指向的本地目录．<br>
 * 
 * - updater.home<br>
 * 库文件的本地缓存目录，缺省值为${local.home}/libs．其中local.home需要在JRE环境变量或web.xml的context-param中定义．<br>
 * 
 * - updater.metadata.master<br>
 * 更新检查服务资源URL,缺省值为${master.home}/update/lib.xml，其中master.home需要在JRE环境变量或web.xml的context-param中定义．<br>
 * 
 * - updater.metadata.secondary<br>
 * 更新检查服务资源备用URL,缺省值为${master.home}/update/lib.xml，其中master.home需要在JRE环境变量或web.xml的context-param中定义．<br>
 * 
 * - app.class<br>
 * {@link WebApp}实现类，缺省为com.logicbus.backend.server.LogicBusApp<br>
 * 
 * @author duanyy
 * @version 1.0.2 <br>
 * - class {@link com.anysoft.webloader.WebApp WebApp} is changed.<br>
 * @version 1.0.3 [20140325 by duanyy] <br> 
 * - Add some varibles to global settings.<br>
 * @version 1.0.17 [20140630 duanyy] <br>
 * - contextInitialized期间可以读取系统定义变量
 */
public class WebAppContextListener implements ServletContextListener {
	protected static Logger logger = LogManager.getLogger(WebAppContextListener.class);
	protected WebApp app = null;
	
	
	public void contextDestroyed(ServletContextEvent e) {
		if (app != null){
			app.destroy(e.getServletContext());
		}
	}

	
	public void contextInitialized(ServletContextEvent e) {
		ServletContext sc = e.getServletContext();
		DefaultProperties props = new DefaultProperties("Default",new SystemProperties());
		
		//20140325 duanyy 将ServletContext里面的一些信息写入全局变量
		props.SetValue("webcontext.path", sc.getContextPath());
		props.SetValue("webcontext.realPath", sc.getRealPath("/"));
		
		logger.info("Get parameters from ServletContext..");
		Enumeration<String> names = sc.getInitParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String value = sc.getInitParameter(name);
			props.SetValue(name, value);
			logger.info(name + "=" + value);
		}
		logger.info("Get parameters from ServletContext.. OK!");
		
		WebUpdater updater = new WebUpdater(props);
		
		updater.update();
		
		ClassLoader classLoader = updater.getLibClassLoader();
		
		sc.setAttribute("classLoader", classLoader);
		
		String appClass = PropertiesConstants.getString(props, "app.class", "com.logicbus.backend.server.LogicBusApp");
		
		try {
			app = (WebApp) classLoader.loadClass(appClass).newInstance();
			app.init(props,e.getServletContext());
		} catch (Exception ex){
			logger.error("Can not init app",ex);
		}
	}
}
