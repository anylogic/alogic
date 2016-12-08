package com.alogic.naming.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 基于XML文档节点的配置环境列表
 * 
 * @author duanyy
 *
 * @param <O>
 * 
 * @since 1.6.6.8
 * 
 */
public class XmlObjectList<O extends Reportable> implements XMLConfigurable, AutoCloseable,Reportable {
	
	/**
	 * 对象列表
	 */
	protected Hashtable<String,O> pools = new Hashtable<String,O>(); // NOSONAR
	
	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LogManager.getLogger(XmlObjectList.class);
	
	/**
	 * 对象在配置XML节点中的tag名
	 */
	protected String objName = "object";
	
	/**
	 * 对象的缺省类名
	 */
	protected String dftClass;	
	
	public XmlObjectList(String defaultClass,String name){
		dftClass = defaultClass;
		objName = name;
	}

	/**
	 * 关闭
	 */
	@Override
	public void close(){
		Collection<O> values = pools.values();
		
		for (O p:values){
			if (p instanceof AutoCloseable){
				IOTools.close((AutoCloseable)p);
			}
		}
		pools.clear();
	}

	@Override
	public void configure(Element root, Properties props){
		XmlElementProperties p = new XmlElementProperties(root,props);
		
		NodeList rcps = XmlTools.getNodeListByPath(root, objName);
		
		TheFactory<O> factory = new TheFactory<O>(); // NOSONAR
		
		for (int i = 0 ; i < rcps.getLength() ; i ++){ // NOSONAR
			Node n = rcps.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element)n;
			
			String id = e.getAttribute("id");
			if (StringUtils.isEmpty(id)){
				continue;
			}
			
			try {
				O obj = factory.newInstance(e, p,"module",dftClass);
				if (obj != null){
					pools.put(id, obj);
				}
			}catch (Exception ex){
				LOG.error("Can not create object instance,check your xml configurations.",ex);
			}
			
		}
	}

	public O get(String id){
		return pools.get(id);
	}
	
	public int getObjectCnt(){
		return pools.size();
	}
	
	public static class TheFactory<O> extends Factory<O>{
		
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			Document doc = xml.getOwnerDocument();
			
			Enumeration<O> iterator = pools.elements();
			
			while (iterator.hasMoreElements()){
				O obj = iterator.nextElement();
				Element element = doc.createElement(objName);
				obj.report(element);
				xml.appendChild(element);
			}
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			List<Object> list = new ArrayList<Object>(getObjectCnt());
			
			Enumeration<O> iterator = pools.elements();
			while (iterator.hasMoreElements()){
				O obj = iterator.nextElement();
				Map<String,Object> map = new HashMap<String,Object>();
				obj.report(map);
				list.add(map);
			}
			
			json.put(objName,list);
		}
	}
}