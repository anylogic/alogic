package com.logicbus.dbcp.impl;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.loadbalance.DefaultCounter;
import com.anysoft.loadbalance.Load;
import com.anysoft.loadbalance.LoadCounter;
import com.anysoft.util.JsonSerializer;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlSerializer;

/**
 * 只读的数据源
 * 
 * @author duanyy
 *
 */
public class ReadOnlySource extends DefaultCounter implements Load,JsonSerializer,XmlSerializer{
	protected String id;
	protected int weight = 1;
	protected int priority = 1;
	
	public ReadOnlySource(Properties p) {
		super(p);
	}

	
	public void report(Element xml) {
		toXML(xml);
		
		super.report(xml);
	}

	
	public void report(Map<String, Object> json) {
		toJson(json);
		
		super.report(json);
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

	
	public LoadCounter getCounter(boolean create) {
		return this;
	}

	
	public void toJson(Map<String, Object> json) {
		if (json != null){
			json.put("id", id);
			json.put("weight", weight);
			json.put("priority", priority);
			
			json.put("loadbalance.cycle", cycle);
			json.put("loadbalance.maxtimes", maxErrorTimes);
			json.put("loadbalance.retryinterval", retryInterval);
		}
	}

	
	public void fromJson(Map<String, Object> json) {
		if (json != null){
			id = JsonTools.getString(json, "id", "");
			weight = JsonTools.getInt(json, "weight", weight);
			priority = JsonTools.getInt(json, "priority",priority);
			
			cycle = JsonTools.getLong(json,"loadbalance.cycle",cycle);
			maxErrorTimes = JsonTools.getInt(json,"loadbalance.maxtimes",maxErrorTimes);
			retryInterval = JsonTools.getInt(json,"loadbalance.retryinterval",retryInterval);
		}
	}

	
	public void toXML(Element xml) {
		if (xml != null){
			xml.setAttribute("id", id);
			xml.setAttribute("weight", String.valueOf(weight));
			xml.setAttribute("priority", String.valueOf(priority));
			
			xml.setAttribute("loadbalance.cycle", String.valueOf(cycle));
			xml.setAttribute("loadbalance.maxtimes", String.valueOf(maxErrorTimes));
			xml.setAttribute("loadbalance.retryinterval", String.valueOf(retryInterval));
		}
	}

	
	public void fromXML(Element xml) {
		if (xml != null){
			XmlElementProperties p = new XmlElementProperties(xml,null);
			
			id = PropertiesConstants.getString(p, "id", "");
			weight = PropertiesConstants.getInt(p, "weight", weight);
			priority = PropertiesConstants.getInt(p, "priority", priority);
			
			cycle = PropertiesConstants.getLong(p,"loadbalance.cycle",cycle);
			maxErrorTimes = PropertiesConstants.getInt(p,"loadbalance.maxtimes",maxErrorTimes);
			retryInterval = PropertiesConstants.getInt(p,"loadbalance.retryinterval",retryInterval);			
		}
	}
}
