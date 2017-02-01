package com.alogic.naming.context;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.naming.Context;
import com.alogic.naming.util.XmlObjectList;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.Settings;
import com.anysoft.util.Watcher;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 基于外部的XML文档的配置环境
 * 
 * @author duanyy
 *
 * @param <O>
 * 
 * @since 1.6.6.8
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public abstract class XmlOutter<O extends Reportable> implements Context<O> {
	
	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(XmlOutter.class);
	
	/**
	 * 对象列表,基本操作委托给对象列表去做
	 */
	protected XmlObjectList<O> objectList = null;	
	
	protected String configFile = "";
	
	protected String secondaryConfigFile = "";
	
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
	
	/**
	 * 获取缺省的配置文件路径
	 * @return 缺省的文件路径
	 */
	public abstract String getDefaultXrc();
	
	@Override
	public void configure(Element root, Properties props) {
		XmlElementProperties p = new XmlElementProperties(root,props);
		configure(p);
		String defaultXrc = getDefaultXrc();
		
		configFile = p.GetValue("xrc.master", defaultXrc); // NOSONAR
		secondaryConfigFile = p.GetValue("xrc.secondary",defaultXrc); // NOSONAR
		
		Document doc = loadDocument(configFile,secondaryConfigFile);		
		
		if (doc != null && doc.getDocumentElement() != null){
			objectList = new XmlObjectList<O>(getDefaultClass(),getObjectName()); // NOSONAR
			objectList.configure(doc.getDocumentElement(), props);
		}			
	}

	@Override
	public void close(){
		if (objectList != null){
			objectList.close();
		}
	}

	@Override
	public void report(Element xml){
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
			xml.setAttribute("dftClass", getDefaultClass());
			xml.setAttribute("objName", getObjectName());
			
			xml.setAttribute("xrc.master", configFile);
			xml.setAttribute("xrc.secondary", secondaryConfigFile);
			xml.setAttribute("objCnt", String.valueOf(objectList != null ? objectList.getObjectCnt():0));
			
			if (objectList != null && objectList.getObjectCnt() > 0){
				objectList.report(xml);
			}
		}
	}
	
	@Override
	public void report(Map<String,Object> json){
		if (json != null){
			json.put("module", getClass().getName());
			json.put("dftClass", getDefaultClass());
			json.put("objName", getObjectName());
			
			json.put("xrc.master", configFile);
			json.put("xrc.secondary", secondaryConfigFile);
			json.put("objCnt", String.valueOf(objectList != null ? objectList.getObjectCnt():0));
			
			if (objectList != null && objectList.getObjectCnt() > 0){
				objectList.report(json);
			}
		}
	}

	@Override
	public O lookup(String name) {
		return (objectList == null ? null : objectList.get(name));
	}

	@Override
	public void addWatcher(Watcher<O> watcher) {
		// nothing to do
	}

	@Override
	public void removeWatcher(Watcher<O> watcher) {
		// nothing to do
	}

	/**
	 * 从主/备地址中装入文档
	 * 
	 * @param master 主地址
	 * @param secondary 备用地址
	 * @return XML文档
	 */
	protected static Document loadDocument(String master,String secondary){
		ResourceFactory rm = Settings.getResourceFactory();
		Document ret = null;
		InputStream in = null;
		try {
			in = rm.load(master,secondary, null);
			ret = XmlTools.loadFromInputStream(in);		
		} catch (Exception ex){
			LOG.error("Error occurs when load xml file,source=" + master, ex);
		}finally {
			IOTools.closeStream(in);
		}		
		return ret;
	}	
	
}
