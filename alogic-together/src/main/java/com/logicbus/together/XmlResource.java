package com.logicbus.together;

import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.cache.Cacheable;
import com.anysoft.cache.CacheManager;
import com.anysoft.cache.Provider;
import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Settings;
import com.anysoft.util.Watcher;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 可缓存的XML资源
 * 
 * <br>
 * XML资源按照URI进行缓存
 * 
 * @author duanyy
 * 
 * @since 1.1.0
 * @version 1.2.5.3 [20140731 duanyy]
 * -  基础包的Cacheable接口修改
 * 
 * @version 1.2.8 [20140912 duanyy]
 * - JsonSerializer中Map参数化
 * 
 * @version 1.2.9.1 [20141017 duanyy]
 * - 淘汰ChangeAware模型，转为更为通用的Watcher模型
 */
public class XmlResource implements Cacheable{
	
	/** 
	 * a logger of log4j
	 * 
	 */
	protected static Logger logger = LogManager.getLogger(XmlResourceProvider.class);
		
	protected String xmlURI;
	
	protected Document xmlDoc = null;
	
	public XmlResource(String id,Document doc){
		xmlURI = id;
		xmlDoc = doc;
	}
	
	public Document getDocument(){return xmlDoc;}
	
	
	public void toXML(Element e) {
		e.setAttribute("uri", xmlURI);
	}

	
	public void fromXML(Element e) {
		xmlURI = e.getAttribute("uri");
	}

	
	public void toJson(Map<String,Object> json) {
		JsonTools.setString(json, "uri", xmlURI);
	}

	
	public void fromJson(Map<String,Object> json) {
		xmlURI = JsonTools.getString(json, "uri", "");
	}

	
	public String getId() {
		return xmlURI;
	}

	
	public boolean isExpired() {
		return false;
	}
	/**
	 * XML资源管理器
	 * 
	 * @author duanyy
	 *
	 */
	public static class Manager extends CacheManager<XmlResource> {
		public Manager(){
			super(new XmlResourceProvider());
		}

	}

	/**
	 * XML资源提供者
	 * 
	 * @author duanyy
	 *
	 */
	public static class XmlResourceProvider implements Provider<XmlResource> {		
		
		public XmlResource load(String id) {
			Settings profile = Settings.get();
			ResourceFactory rm = (ResourceFactory) profile.get("ResourceFactory");
			if (null == rm){
				rm = new ResourceFactory();
			}
			
			Document doc = null;
			InputStream in = null;
			try {
				in = rm.load(id, null);
				doc = XmlTools.loadFromInputStream(in);
				if (doc != null){
					return new XmlResource(id,doc);
				}
			} catch (Exception ex){
				logger.error("Error occurs when load xml file,source=" + id, ex);
			}finally {
				IOTools.closeStream(in);
			}
			return null;
		}

		
		public void addWatcher(Watcher<XmlResource> listener) {
			// do nothing
		}
		
		
		public void removeWatcher(Watcher<XmlResource> listener) {
			// do nothing
		}

		
		public XmlResource load(String id, boolean cacheAllowed) {
			return load(id);
		}
	}
	
	
	public static void main(String [] args){
		Manager xrcm = new Manager();
		
		XmlResource xrc = xrcm.get("java:///com/logicbus/together/Demo.xml#com.logicbus.together.XmlResourceManager");
		
		if (xrc == null){
			logger.error("Can not load xrc..");
			return ;
		}
		
		Document doc = xrc.getDocument();
		
		try {
			Logiclet logiclet = Compiler.compile(doc.getDocumentElement(), Settings.get(),null);
			
			if (logiclet == null){
				logger.error("Can not compile the document.");
			}
			
			//新的文档
			Document result = XmlTools.newDocument("root");
			
			Element target = result.getDocumentElement();
			
			logiclet.execute(target, null, null,null);
			
			XmlTools.saveToOutputStream(result, System.out);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
