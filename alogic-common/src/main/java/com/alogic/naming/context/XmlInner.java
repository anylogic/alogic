package com.alogic.naming.context;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.naming.Context;
import com.alogic.naming.util.XmlObjectList;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.Watcher;
import com.anysoft.util.XmlElementProperties;

/**
 * 基于同一XML配置文档内部的配置环境
 * 
 * @author duanyy
 * @since 1.6.6.8
 */
public abstract class XmlInner<O extends Reportable> implements Context<O>{
	
	/**
	 * 对象列表,基本操作委托给对象列表去做
	 */
	protected XmlObjectList<O> objectList = null;
	
	/**
	 * 获取对象的XMLtag名称
	 * @return tag名
	 */
	public abstract String getObjectName();
	
	/**
	 * 获取对象的缺省类名
	 * @return 缺省类名
	 */
	public abstract String getDefaultClass();	
	
	@Override
	public void configure(Element e, Properties p) {
		objectList = new XmlObjectList<O>(getDefaultClass(),getObjectName()); // NOSONAR
		objectList.configure(e, p);
		
		XmlElementProperties props = new XmlElementProperties(e,p);
		configure(props);
	}

	@Override
	public void close(){
		if (objectList != null){
			objectList.close();
		}
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
			xml.setAttribute("dftClass", getDefaultClass());
			xml.setAttribute("objName", getObjectName());
			
			xml.setAttribute("objCnt", String.valueOf(objectList != null ? objectList.getObjectCnt():0));
			
			if (objectList != null && objectList.getObjectCnt() > 0){
				objectList.report(xml);
			}
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("module", getClass().getName());
			json.put("dftClass", getDefaultClass());
			json.put("objName", getObjectName());
			
			json.put("objCnt", String.valueOf(objectList != null ? objectList.getObjectCnt():0));
			
			if (objectList != null && objectList.getObjectCnt() > 0){
				objectList.report(json);
			}
		}
	}

	@Override
	public O lookup(String name) {
		return (objectList == null)?null:objectList.get(name);
	}

	@Override
	public void addWatcher(Watcher<O> watcher) {
		// nothing to do
	}

	@Override
	public void removeWatcher(Watcher<O> watcher) {
		// nothing to do
	}

}
