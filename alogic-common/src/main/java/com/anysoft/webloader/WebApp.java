package com.anysoft.webloader;

import com.anysoft.util.DefaultProperties;
import javax.servlet.ServletContext;

/**
 * Web应用
 * 
 * <br>
 * Web应用是部署在应用服务器(如tomcat,jetty等)中一个context.<br>
 * 
 * 设想一下这样的需求：<br>
 * 
 * 当在应用服务器中构建一个Context的时候，我们希望在远程存放这个Context所需的jar库文件，
 * 并希望应用服务器能够下载并动态加载这些库文件，以达到版本集中发布和更新的目的。<br>
 * 
 * 于是有了webloader这个工具，这个工具的功能包括：<br>
 * - 将{@link javax.servlet.ServletContextListener ServletContextListener}功能映射到{@link WebApp}<br>
 * - 将{@link javax.servlet.http.HttpServlet HttpServlet}功能映射到{@link ServletHandler}</br>
 * - 自动下载指定的目录，并动态加载<br>
 * 
 * ### ServletContextListener的配置<br>
 * 
 * 在web.xml中配置Listener，见{@link WebAppContextListener}.<br>
 * 
 * ### SevletHandler的配置 <br>
 * 
 * 在web.xml中配置Servlet，见{@link ServletAgent}.<br>
 * 
 * ### 库文件服务端配置<br>
 * 
 * 首先，需要将库文件存放在一个公共服务器之上，可以通过标准URL进行访问，例如file,http等.<br>
 * 
 * 接着，在管理服务器上发布一个更新信息服务，更新信息服务返回XML文档，记录库文件的名称，下载地址，MD5校验码等。例如:<br>
 * 
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <?xml version="1.0" encoding="utf-8" standalone="no"?>
 * <root>
 *   <module jar="anyLogicbus.jar" md5="937a43fd16c480b9e4d221d6828cb467" 
 *   url="file:///D:\ecloud\18923882238\logicbus\libs\anyLogicbus.jar"
 *   />
 * </root>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * 
 * @version 1.0.2 [20140319 by duanyy] <br>
 * - add servletContext parameter to {@link com.anysoft.webloader.WebApp#init(DefaultProperties, ServletContext) WebApp.init()} 
 *   and  {@link com.anysoft.webloader.WebApp#destroy(ServletContext) WebApp.destroy()}<br>
 * 
 * @author duanyy
 *
 */
public interface WebApp {
	/**
	 * 初始化应用
	 * @param props 初始化参数
	 */
	public void init(DefaultProperties props,ServletContext servletContext);
	
	/**
	 * 销毁应用
	 */
	public void destroy(ServletContext servletContext);
}
