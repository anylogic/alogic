package com.alogic.remote.route.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlTools;
import com.alogic.remote.backend.AppBackends;
import com.alogic.remote.backend.Backend;
import com.alogic.remote.backend.BackendProvider;
import com.alogic.remote.route.Route;

/**
 * 简单路由模式
 * 
 * @author duanyy
 * @since 1.6.8.12
 */
public class Simple extends Route.Abstract{
	
	public Simple(){
		
	}
	
	public Simple(String id,BackendProvider provider){
		super(id,provider);
	}
	
	@Override
	public List<Backend> select(String app, String route) {
		AppBackends appBackends = get(app);
		return appBackends == null ? null : appBackends.getBackends();
	}

	@Override
	public List<Backend> select(String app, Properties p) {
		AppBackends appBackends = get(app);
		return appBackends == null ? null : appBackends.getBackends();
	}

	@Override
	public void rebuild(AppBackends app) {
		// nothing to do
	}
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			super.report(xml);
			
			boolean detail = XmlTools.getBoolean(xml, "detail", false);
			if (detail){
				String app = XmlTools.getString(xml, "app", "");
				if (StringUtils.isNotEmpty(app)){
					AppBackends appBackends = get(app);
					if (appBackends != null){
						Document doc = xml.getOwnerDocument();
						Element item = doc.createElement("item");
						appBackends.report(item);
						xml.appendChild(item);						
					}
				}
			}				
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			super.report(json);
			
			boolean detail = JsonTools.getBoolean(json, "detail", false);
			if (detail){
				String app = JsonTools.getString(json, "app", "");
				if (StringUtils.isNotEmpty(app)){
					AppBackends appBackends = get(app);
					if (appBackends != null){
						List<Object> items = new ArrayList<Object>();							
							Map<String,Object> item = new HashMap<String,Object>();							
							appBackends.report(item);
							items.add(item);
						json.put("item", items);						
					}
				}
			}				
		}
	}		
}