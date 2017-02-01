package com.anysoft.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * 对象持有者
 * 
 * @author duanyy
 *
 * @param <O>
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
 * 
 * @version 1.6.4.20 [20151222 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class Holder<O extends Reportable> implements XMLConfigurable, AutoCloseable,Reportable {
	
	/**
	 * 对象列表
	 */
	protected Hashtable<String,O> pools = new Hashtable<String,O>(); // NOSONAR
	
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LoggerFactory.getLogger(Holder.class);
	
	/**
	 * 对象在配置XML节点中的tag名
	 */
	protected String objName = "object";
	
	/**
	 * 对象的缺省类名
	 */
	protected String dftClass;	
	
	public Holder(String defaultClass,String name){
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
				logger.error("Can not create object instance,check your xml configurations.",ex);
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
