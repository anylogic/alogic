package com.anysoft.webloader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
 */
public class WebAppMain extends WebAppContextListener {
	protected List<ServletContextListener> listeners = null;
	
	@Override
	public void contextDestroyed(ServletContextEvent e) {
		super.contextDestroyed(e);
		
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
	public void contextInitialized(ServletContextEvent e) {
		super.contextInitialized(e);
		
		Settings settings = Settings.get();
		
		String _addons = settings.GetValue("webcontext.addons", "");
		if (_addons != null && _addons.length() > 0){
			logger.info("Load addons...");
			
			ResourceFactory rm = Settings.getResourceFactory();
			InputStream in = null;
			try {
				in = rm.load(_addons, null);
				Document doc = XmlTools.loadFromInputStream(in);	
				if (doc != null){
					//装入context-param
					{
						loadContextParams(settings,doc.getDocumentElement(),e.getServletContext());
					}
					//装入listener
					{
						loadContextListeners(settings,doc.getDocumentElement(),e);
					}
					//在装入其他
					{
						WebXMLLoader loader = createXMLLoader(settings);
						if (loader != null){
							loader.load(settings,doc.getDocumentElement(), e.getServletContext());
						}
					}
				}
			}catch (Exception ex){
				logger.error("Error occurs when load xml file,source=" + _addons, ex);
			}finally {
				IOTools.closeStream(in); 
			}
		}
	}
	
	/**
	 * 装入ContextListener，可支持多个ContextListener
	 * 
	 * @param settings
	 * @param root
	 * @param e
	 */
	protected void loadContextListeners(Properties settings,Element root,ServletContextEvent e) {
		NodeList _listeners = XmlTools.getNodeListByPath(root, "listener");
		if (_listeners != null && _listeners.getLength() > 0){
			listeners = new ArrayList<ServletContextListener>(_listeners.getLength());
			
			for (int i = 0 ;i < _listeners.getLength() ; i ++){
				Node n = _listeners.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element _e = (Element)n;
				
				String _class = _e.getAttribute("listener-class");
				if (_class == null || _class.length() <= 0){
					continue;
				}
				
				Factory<ServletContextListener> factory = new Factory<ServletContextListener>();
				try {
					ServletContextListener _listener = factory.newInstance(_class);
					if (_listener != null){
						logger.info("Init context listener,module=" + _class);
						_listener.contextInitialized(e);
						listeners.add(_listener);
					}
				}catch (Exception ex){
					logger.error("Can not create context listener,module=" + _class,ex);
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
		String _loader = settings.GetValue("webcontext.xmlloader",DefaultWebXMLLoader.class.getName());
		
		WebXMLLoader.TheFactory factory = new WebXMLLoader.TheFactory();
		try{
			return factory.newInstance(_loader, settings);
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
		NodeList _params = XmlTools.getNodeListByPath(root, "context-param");
		
		if (_params != null && _params.getLength() > 0){
			for (int i = 0 ; i < _params.getLength() ; i ++){
				Node n = _params.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element e = (Element)n;
				
				String name = e.getAttribute("param-name");
				String value = e.getAttribute("param-value");
				if (name != null && value != null && name.length() > 0 && value.length() > 0){
					String _value = settings.transform(value);
					logger.info("Init parameter:" + name + "=" + _value);
					sc.setInitParameter(name, _value);
				}
			}
		}
	}	
}
