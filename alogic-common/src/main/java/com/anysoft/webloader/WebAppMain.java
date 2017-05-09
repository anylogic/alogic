package com.anysoft.webloader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * WebApp主入口
 * 
 * <p>用于替代WebAppContextListener，在WebAppContextListener的基础上增加下列功能:<br>
 * - 可加载附加的ServletContextListener<br>
 * - 可加载附加的Servlet<br>
 * 
 * @author duanyy
 * 
 * @since 1.6.0.1 <br>
 * @version 1.6.4.17 [20151216 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 * 
 * @version 1.6.4.24 [20150115 duanyy] <br>
 * - 增加对SessionListener的支持 <br>
 */
public class WebAppMain extends WebAppContextListener implements HttpSessionListener{
	protected List<ServletContextListener> listeners = null;
	protected List<HttpSessionListener> sessionListeners = null;
	
	@Override
	public void onContextDestroyed(ServletContextEvent e) {
		//销毁所创建的ServletContextListener
		if (listeners != null){
			for (ServletContextListener l:listeners){
				if (l != null){
					l.contextDestroyed(e);
				}
			}
		}
	}

	@Override
	public void onContextInitialized(ServletContextEvent e) {
		Settings settings = Settings.get();
		
		String addons = settings.GetValue("webcontext.addons", "");
		if (addons != null && addons.length() > 0){
			logger.info("Load addons...");
			
			ResourceFactory rm = Settings.getResourceFactory();
			InputStream in = null;
			try {
				in = rm.load(addons, null);
				Document doc = XmlTools.loadFromInputStream(in);	
				if (doc != null){
					//装入context-param
					loadContextParams(settings,doc.getDocumentElement(),e.getServletContext());
					//装入listener
					loadContextListeners(settings,doc.getDocumentElement(),e);	
					//装入SessionListener
					loadSessionListeners(settings,doc.getDocumentElement());
					//在装入其他
					WebXMLLoader loader = createXMLLoader(settings);
					if (loader != null){ // NOSONAR
							loader.load(settings,doc.getDocumentElement(), e.getServletContext());
					}
				}
			}catch (Exception ex){
				logger.error("Error occurs when load xml file,source=" + addons, ex);
			}finally {
				IOTools.closeStream(in); 
			}
		}
	}
	
	protected void loadSessionListeners(Settings settings,Element root) {
		NodeList nodeList = XmlTools.getNodeListByPath(root, "session");
		if (nodeList != null && nodeList.getLength() > 0){
			sessionListeners = new ArrayList<HttpSessionListener>();
			for (int i = 0 ;i < nodeList.getLength() ; i ++){
				Node n = nodeList.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element e = (Element)n;
				
				String clazz = e.getAttribute("listener-class");
				if (clazz == null || clazz.length() <= 0){
					continue;
				}
				
				Factory<HttpSessionListener> factory = new Factory<HttpSessionListener>();
				try {
					HttpSessionListener _listener = factory.newInstance(clazz);
					if (_listener != null){
						logger.info("Session listener is found,module=" + clazz);
						sessionListeners.add(_listener);
					}
				}catch (Exception ex){
					logger.error("Can not create context listener,module=" + clazz,ex);
				}
			}
		}
	}

	/**
	 * 装入ContextListener，可支持多个ContextListener
	 * 
	 * @param settings
	 * @param root
	 * @param event
	 */
	protected void loadContextListeners(Properties settings,Element root,ServletContextEvent event) {
		NodeList nodeList = XmlTools.getNodeListByPath(root, "listener");
		if (nodeList != null && nodeList.getLength() > 0){
			listeners = new ArrayList<ServletContextListener>(nodeList.getLength());
			
			for (int i = 0 ;i < nodeList.getLength() ; i ++){
				Node n = nodeList.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element e = (Element)n;
				
				String clazz = e.getAttribute("listener-class");
				if (clazz == null || clazz.length() <= 0){
					continue;
				}
				
				Factory<ServletContextListener> factory = new Factory<ServletContextListener>();
				try {
					ServletContextListener listner = factory.newInstance(clazz);
					if (listner != null){
						logger.info("Init context listener,module=" + clazz);
						listner.contextInitialized(event);
						listeners.add(listner);
					}
				}catch (Exception ex){
					logger.error("Can not create context listener,module=" + clazz,ex);
				}
			}
		}
	}


	/**
	 * 创建WebXMLLoader实例
	 * 
	 * <p>通过环境变量webcontext.xmlloader指定的类名来创建WebXMLLoader实例。缺省为DefaultWebXMLLoader。
	 * 如果无法创建成功，返回空。
	 * 
	 * @param settings
	 * @return WebXMLLoader实例
	 */
	protected WebXMLLoader createXMLLoader(Properties settings){
		String loader = settings.GetValue("webcontext.xmlloader",DefaultWebXMLLoader.class.getName());
		
		WebXMLLoader.TheFactory factory = new WebXMLLoader.TheFactory();
		try{
			return factory.newInstance(loader, settings);
		}catch (Exception ex){
			logger.error("Can not create a webxml loader.",ex);
			return null;
		}
	}

	/**
	 * 装入context-param信息
	 * 
	 * @param settings
	 * @param root
	 * @param sc
	 */
	protected void loadContextParams(Properties settings,Element root, ServletContext sc) {
		NodeList params = XmlTools.getNodeListByPath(root, "context-param");
		
		if (params != null && params.getLength() > 0){
			for (int i = 0 ; i < params.getLength() ; i ++){
				Node n = params.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element e = (Element)n;
				
				String name = e.getAttribute("param-name");
				String pattern = e.getAttribute("param-value");
				if (name != null && pattern != null && name.length() > 0 && pattern.length() > 0){
					String value = settings.transform(pattern);
					logger.info("Init parameter:" + name + "=" + value);
					sc.setInitParameter(name, value);
				}
			}
		}
	}

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		if (sessionListeners != null){
			for (HttpSessionListener l:sessionListeners){
				if (l != null){
					l.sessionCreated(se);
				}
			}
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		if (sessionListeners != null){
			for (HttpSessionListener l:sessionListeners){
				if (l != null){
					l.sessionDestroyed(se);
				}
			}
		}
	}	
}
