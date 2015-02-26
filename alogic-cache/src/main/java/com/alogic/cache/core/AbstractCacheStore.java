package com.alogic.cache.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * CacheStore的虚基类
 * 
 * @author duanyy
 * @since 1.6.3.3
 * 
 */
abstract public class AbstractCacheStore implements CacheStore {
	/**
	 * a logger of log4j
	 */
	protected Logger logger = LogManager.getLogger(CacheStore.class);
	
	/**
	 * cache的过期策略
	 */
	protected ExpirePolicy expirePolicy = null;
	
	/**
	 * cache的Provider
	 */
	protected MultiFieldObjectProvider provider = null;
	
	public MultiFieldObject load(String id) {
		return load(id,true);
	}

	public void configure(Element _e, Properties _properties)
			throws BaseException {
		Properties p = new XmlElementProperties(_e,_properties);
		
		id = PropertiesConstants.getString(p,"id", "");
		name = PropertiesConstants.getString(p,"name","");
		note = PropertiesConstants.getString(p,"note", "");
		
		Element eExpire = XmlTools.getFirstElementByPath(_e, "policy");
		if (eExpire != null){
			Factory<ExpirePolicy> factory = new Factory<ExpirePolicy>();
			
			try {
				expirePolicy = factory.newInstance(eExpire, p, "module", ExpirePolicy.Default.class.getName());
			}catch (Exception ex){
				logger.error("Can not create Expire Policy,use default",ex);
				expirePolicy = new ExpirePolicy.Default();
			}
		}else{
			expirePolicy = new ExpirePolicy.Default();
		}
		
		Element eProvider = XmlTools.getFirstElementByPath(_e, "provider");
		if (eProvider != null){
			Factory<MultiFieldObjectProvider> factory = new Factory<MultiFieldObjectProvider>();
			try {
				provider = factory.newInstance(eProvider, p, "module");
			}catch(Exception ex){
				logger.error("Can not create provider",ex);
			}
		}
		
		onConfigure(_e,p);
	}

	/**
	 * 处理Configure时间
	 * @param _e XML的Element
	 * @param p 变量集
	 */
	abstract protected void onConfigure(Element _e, Properties p);

	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("id", id);
			xml.setAttribute("name",name);
			xml.setAttribute("note", note);
			Document doc = xml.getOwnerDocument();
			
			if (expirePolicy != null){	
				Element ePolicy = doc.createElement("policy");
				expirePolicy.report(ePolicy);
				xml.appendChild(ePolicy);
			}
			if (provider != null){	
				Element eProvider = doc.createElement("provider");
				provider.report(eProvider);
				xml.appendChild(eProvider);
			}
		}
	}

	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json, "id", id);
			JsonTools.setString(json, "name", name);
			JsonTools.setString(json, "note", note);
			
			if (expirePolicy != null){
				Map<String,Object> map = new HashMap<String,Object>();
				expirePolicy.report(map);
				json.put("policy", map);
			}
			if (provider != null){
				Map<String,Object> map = new HashMap<String,Object>();
				provider.report(map);
				json.put("provider", map);
			}
		}
	}

	public String id() {
		return id;
	}

	public String name() {
		return name;
	}

	public String note() {
		return note;
	}

	/**
	 * id
	 */
	protected String id;
	
	/**
	 * name
	 */
	protected String name;
	
	/**
	 * note
	 */
	protected String note;
}
