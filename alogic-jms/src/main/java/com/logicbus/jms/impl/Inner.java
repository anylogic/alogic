package com.logicbus.jms.impl;

import java.util.Hashtable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.Watcher;
import com.anysoft.util.XmlTools;
import com.logicbus.jms.JmsModel;
import com.logicbus.jms.JmsModelFactory;

/**
 * Source配置文件内置实现
 * @author duanyy
 * 
 * @since 1.2.6.1
 *
 */
public class Inner implements JmsModelFactory {

	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		load(_e);
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

	
	public JmsModel loadModel(String id) {
		return models.get(id);
	}

	
	public void addWatcher(Watcher<JmsModel> watcher) {
	}

	
	public void removeWatcher(Watcher<JmsModel> watcher) {
	}

	/**
	 * modles
	 */
	protected Hashtable<String,JmsModel> models = new Hashtable<String,JmsModel>();
}
