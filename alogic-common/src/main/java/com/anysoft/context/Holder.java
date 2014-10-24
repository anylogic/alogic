package com.anysoft.context;

import java.util.Collection;
import java.util.Hashtable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
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
 * @version 1.2.9.2 [20141017 duanyy]
 * - 增加{@link #getObjectCnt()}
 * 
 */
public class Holder<object> implements XMLConfigurable, AutoCloseable {
	
	protected Hashtable<String,object> pools = new Hashtable<String,object>();
	
	protected final static Logger logger = LogManager.getLogger(Holder.class);
	
	protected String objName = "object";
	
	public Holder(String _dftClass,String name){
		dftClass = _dftClass;
		objName = name;
	}
	
	protected String dftClass;
	
	
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
}
