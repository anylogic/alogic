package com.logicbus.backend.acm;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.cache.XMLResourceSimpleModelProvider;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

/**
 * 基于XMLResource的ACMProvider
 * @author duanyy
 * @since 1.2.3
 * 
 * @version 1.2.5.3 [20140731 duanyy]
 * -  基础包的Cacheable接口修改
 */
public class XMLResourceACMProvider extends XMLResourceSimpleModelProvider<AccessControlModel>{

	public XMLResourceACMProvider(Properties props) {
		super(props);
	}

	
	protected Document loadResource(Properties props) {
		String xrcURI = props.GetValue("acm.master","java:///com/logicbus/backend/acm/acm.default.xml#com.logicbus.backend.acm.AccessControlModel");
		String xrcURI2 = props.GetValue("acm.secondary","java:///com/logicbus/backend/acm/acm.default.xml#com.logicbus.backend.acm.AccessControlModel");		
		return loadDocument(xrcURI,xrcURI2);
	}

	
	protected AccessControlModel newModel(String id, Element e) {
		return new AccessControlModel(id,e);
	}

	public static void main(String [] args){
		Settings settings = Settings.get();
		
		XMLResourceACMProvider provider = new XMLResourceACMProvider(settings);
		
		AccessControlModel model = provider.load("Default");
		if (model != null){
			Map<String,Object> map = new HashMap<String,Object>();
			
			model.toJson(map);
			
			JsonProvider jsonProvider = JsonProviderFactory.createProvider();
			
			System.out.println(jsonProvider.toJson(map));
		}
	}
}
