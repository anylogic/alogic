package com.logicbus.remote.impl.http;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.loadbalance.DefaultCounter;
import com.anysoft.loadbalance.Load;
import com.anysoft.loadbalance.LoadCounter;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 服务调用目标
 * 
 * @author duanyy
 *
 */
public class HttpDestination implements Load,XMLConfigurable{

	/**
	 * ID
	 */
	protected String id;
	
	/**
	 * 权重
	 */
	protected int weight = 1;
	
	/**
	 * 优先级
	 */
	protected int priority = 1;
	
	/**
	 * URI
	 */
	protected String uri = "";
	
	/**
	 * 计数器
	 */
	protected LoadCounter counter = null;
	
	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		id = PropertiesConstants.getString(p,"id", "" , true);
		
		weight = PropertiesConstants.getInt(p,"weight",weight,true);
		priority = PropertiesConstants.getInt(p,"priority",priority,true);
		
		uri = PropertiesConstants.getString(p,"uri", "" , true);
		
		counter = new DefaultCounter(p);
	}
	
	
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("id", id);
			xml.setAttribute("weight", String.valueOf(weight));
			xml.setAttribute("priority", String.valueOf(priority));
			xml.setAttribute("uri", uri);
			
			if (counter != null){
				Document doc = xml.getOwnerDocument();
				
				Element _counter = doc.createElement("counter");
				
				counter.report(_counter);
				
				xml.appendChild(_counter);
			}
		}
	}

	
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("id", id);
			json.put("weight", weight);
			json.put("priority", priority);
			json.put("uri", uri);
			
			if (counter != null){
				Map<String,Object> _counter = new HashMap<String,Object>();
				
				counter.report(_counter);
				
				json.put("counter", _counter);
			}
		}
	}

	
	public String getId() {
		return id;
	}

	
	public int getWeight() {
		return weight;
	}

	
	public int getPriority() {
		return priority;
	}

	public String getURI(){
		return uri;
	}
	
	
	public LoadCounter getCounter(boolean create) {
		return counter;
	}

	
	public void count(long _duration, boolean error) {
		if (counter != null){
			counter.count(_duration,error);
		}
	}

	
	public boolean isValid() {
		if (uri == null || uri.length() <= 0){
			return false;
		}
		return counter != null ? counter.isValid() : true;
	}
}
