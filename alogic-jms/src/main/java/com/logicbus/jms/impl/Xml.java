package com.logicbus.jms.impl;

import java.io.InputStream;
import java.util.Hashtable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.Watcher;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.logicbus.jms.JmsModel;

/**
 * 基于XML文件的Provider 
 * 
 * @author duanyy
 * @since 1.2.6.1
 * @version 1.2.9.1 [20141017 duanyy]
 * - 淘汰ChangeAware模型，转为更为通用的Watcher模型
 */
public class Xml implements JmsModelProvider {
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(Xml.class);
	
	
	public JmsModel load(String id) {
		return load(id,true);
	}

	
	public JmsModel load(String id, boolean cacheAllowed) {
		return models.get(id);
	}

	
	public void addWatcher(Watcher<JmsModel> listener) {
	}

	
	public void removeWatcher(Watcher<JmsModel> listener) {
	}

	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		String configFile = p.GetValue("xrc.master", "${jms.xml.master}");
		configFile = configFile != null && configFile.length() > 0 ?
				configFile : "java:///com/logicbus/jms/jms.xml#com.logicbus.jms.JmsSource";
		
		String secondaryConfigFile = p.GetValue("xrc.secondary","${jms.xml.secondary}");
		secondaryConfigFile = secondaryConfigFile != null && secondaryConfigFile.length() > 0 ?
				secondaryConfigFile : "java:///com/logicbus/jms/jms.xml#com.logicbus.jms.JmsSource";
		
		Document doc = loadDocument(configFile,secondaryConfigFile);		
		load(doc.getDocumentElement());		
	}
	

	private void load(Element root) {
		NodeList contexts = XmlTools.getNodeListByPath(root, "context");
		
		for (int i = 0 ;i < contexts.getLength() ; i ++){
			Node n = contexts.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element)n;
			
			String id = e.getAttribute("id");
			if (id == null || id.length() <= 0){
				continue;
			}
			
			JmsModel model = new JmsModel(id);
			model.fromXML(e);
			
			addModel(id,model);
		}
	}

	/**
	 * 增加Model
	 * @param id
	 * @param model
	 */
	public void addModel(String id, JmsModel model) {
		models.put(id, model);
	}	

	/**
	 * modles
	 */
	protected Hashtable<String,JmsModel> models = new Hashtable<String,JmsModel>();	
	
	/**
	 * 从主/备地址中装入文档
	 * 
	 * @param master 主地址
	 * @param secondary 备用地址
	 * @return XML文档
	 */
	protected static Document loadDocument(String master,String secondary){
		ResourceFactory rm = Settings.getResourceFactory();
		Document ret = null;
		InputStream in = null;
		try {
			in = rm.load(master,secondary, null);
			ret = XmlTools.loadFromInputStream(in);		
		} catch (Exception ex){
			logger.error("Error occurs when load xml file,source=" + master, ex);
		}finally {
			IOTools.closeStream(in);
		}		
		return ret;
	}
		
}
