package com.anysoft.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;


/**
 * 对象持有者
 * 
 * @author duanyy
 *
 * @param <object>
 * 
 * @since 1.5.0
 *  
 * @version 1.2.9.2 [20141017 duanyy] <br>
 * - 增加{@link #getObjectCnt()} <br>
 * 
 * @version 1.6.0.2 [20141108 duanyy] <br>
 * - 增加Reportable实现 <br>
 * 
 * @version 1.6.3.3 [20150227 duanyy] <br>
 * - 修正Report为Json时的问题 <br>
 */
public class Holder<object extends Reportable> implements XMLConfigurable, AutoCloseable,Reportable {
	
	/**
	 * 对象列表
	 */
	protected Hashtable<String,object> pools = new Hashtable<String,object>();
	
	/**
	 * a logger of log4j
	 */
	protected final static Logger logger = LogManager.getLogger(Holder.class);
	
	/**
	 * 对象在配置XML节点中的tag名
	 */
	protected String objName = "object";
	
	public Holder(String _dftClass,String name){
		dftClass = _dftClass;
		objName = name;
	}
	
	/**
	 * 对象的缺省类名
	 */
	protected String dftClass;
	
	/**
	 * 关闭
	 */
	public void close() throws Exception {
		Collection<object> values = pools.values();
		
		for (object p:values){
			if (p instanceof AutoCloseable){
				IOTools.close((AutoCloseable)p);
			}
		}
		pools.clear();
	}

	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		NodeList rcps = XmlTools.getNodeListByPath(_e, objName);
		
		TheFactory<object> factory = new TheFactory<object>();
		
		for (int i = 0 ; i < rcps.getLength() ; i ++){
			Node n = rcps.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element)n;
			
			String id = e.getAttribute("id");
			if (id == null || id.length() <= 0){
				continue;
			}
			
			try {
				object obj = factory.newInstance(e, p,"module",dftClass);
				if (obj != null){
					pools.put(id, obj);
				}
			}catch (Exception ex){
				logger.error("Can not create object instance,check your xml configurations.",ex);
			}
			
		}
	}

	public object get(String id){
		return pools.get(id);
	}
	
	public int getObjectCnt(){
		return pools.size();
	}
	
	public static class TheFactory<object> extends Factory<object>{
		
	}

	public void report(Element xml) {
		if (xml != null){
			Document doc = xml.getOwnerDocument();
			
			Enumeration<object> iterator = pools.elements();
			
			while (iterator.hasMoreElements()){
				object obj = iterator.nextElement();
				Element _obj = doc.createElement(objName);
				obj.report(_obj);
				xml.appendChild(_obj);
			}
		}
	}


	public void report(Map<String, Object> json) {
		if (json != null){
			List<Object> _objs = new ArrayList<Object>(getObjectCnt());
			
			Enumeration<object> iterator = pools.elements();
			while (iterator.hasMoreElements()){
				object obj = iterator.nextElement();
				Map<String,Object> _obj = new HashMap<String,Object>();
				obj.report(_obj);
				_objs.add(_obj);
			}
			
			json.put(objName,_objs);
		}
	}
}
