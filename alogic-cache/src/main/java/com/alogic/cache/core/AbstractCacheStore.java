package com.alogic.cache.core;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.cache.core.MultiFieldObjectProvider.Null;
import com.anysoft.util.Factory;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Watcher;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * CacheStore的虚基类
 * 
 * @author duanyy
 * @since 1.6.3.3
 * @version 1.6.4.5 [20150910 duanyy] <br>
 * - Report输出时输出module <br>
 * 
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @deprecated
 */
public abstract class AbstractCacheStore implements CacheStore {
	/**
	 * a logger of log4j
	 */
	protected Logger logger = LoggerFactory.getLogger(CacheStore.class);
	
	/**
	 * cache的过期策略
	 */
	protected ExpirePolicy expirePolicy = null;
	
	/**
	 * cache的Provider
	 */
	protected MultiFieldObjectProvider provider = null;
	
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
	
	@Override
	public void addWatcher(Watcher<MultiFieldObject> watcher) {
		if (provider != null){
			provider.addWatcher(watcher);
		}
	}

	@Override
	public void removeWatcher(Watcher<MultiFieldObject> watcher) {
		if (provider != null){
			provider.addWatcher(watcher);
		}
	}
	
	public MultiFieldObject load(String id) {
		return load(id,true);
	}

	@Override
	public void configure(Element element, Properties props){
		Properties p = new XmlElementProperties(element,props);
		
		id = PropertiesConstants.getString(p,"id", "");
		name = PropertiesConstants.getString(p,"name","");
		note = PropertiesConstants.getString(p,"note", "");
		
		Element eExpire = XmlTools.getFirstElementByPath(element, "policy"); // NOSONAR
		if (eExpire != null){
			Factory<ExpirePolicy> factory = new Factory<ExpirePolicy>(); // NOSONAR
			
			try {
				expirePolicy = factory.newInstance(eExpire, p, "module", ExpirePolicy.Default.class.getName()); // NOSONAR
			}catch (Exception ex){
				logger.error("Can not create Expire Policy,use default",ex);
				expirePolicy = new ExpirePolicy.Default();
			}
		}else{
			expirePolicy = new ExpirePolicy.Default();
		}
		
		Element eProvider = XmlTools.getFirstElementByPath(element, "provider"); // NOSONAR
		if (eProvider != null){
			Factory<MultiFieldObjectProvider> factory = new Factory<MultiFieldObjectProvider>(); // NOSONAR
			try {
				provider = factory.newInstance(eProvider, p, "module",Null.class.getName());
			}catch(Exception ex){
				provider = new MultiFieldObjectProvider.Null();
				logger.error("Can not create provider,use default:" + provider.getClass().getName(),ex);
			}
		}else{
			provider = new MultiFieldObjectProvider.Null();
		}
		
		onConfigure(element,p);
	}

	/**
	 * 处理Configure时间
	 * @param e XML的Element
	 * @param p 变量集
	 */
	protected abstract void onConfigure(Element e, Properties p);

	@Override
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("id", id);
			xml.setAttribute("name",name);
			xml.setAttribute("note", note);
			xml.setAttribute("module", getClass().getName());
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

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json, "id", id);
			JsonTools.setString(json, "name", name);
			JsonTools.setString(json, "note", note);
			JsonTools.setString(json, "module", getClass().getName());
			
			if (expirePolicy != null){
				Map<String,Object> map = new HashMap<String,Object>(); // NOSONAR
				expirePolicy.report(map);
				json.put("policy", map);
			}
			if (provider != null){
				Map<String,Object> map = new HashMap<String,Object>(); // NOSONAR
				provider.report(map);
				json.put("provider", map);
			}
		}
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String note() {
		return note;
	}


}
