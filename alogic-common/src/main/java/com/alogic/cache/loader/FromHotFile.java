package com.alogic.cache.loader;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.cache.CacheObject;
import com.alogic.cache.CacheObject.Simple;
import com.alogic.load.Loader;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 从HotFile中加载通用缓存对象
 * 
 * @author yyduan
 * @since 1.6.11.59 [20180911 duanyy]
 */
public class FromHotFile extends Loader.HotFile<CacheObject>{

	@Override
	protected String getObjectDftClass() {
		return MyCacheObject.class.getName();
	}

	/**
	 * 自实现的子类
	 * @author yyduan
	 *
	 */
	public static class MyCacheObject extends Simple implements XMLConfigurable,Configurable{
		protected String delimeter = "";
		
		@Override
		public void configure(Properties p) {
			id = PropertiesConstants.getString(p,"id","",true);
			delimeter = PropertiesConstants.getString(p,"delimeter","",true);
		}

		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
			
			NodeList hashAttrs = XmlTools.getNodeListByPath(e, "hash");
			if (hashAttrs != null){
				loadHash(hashAttrs,props);
			}
			
			NodeList setAttrs = XmlTools.getNodeListByPath(e, "set");
			if (setAttrs != null){
				loadSet(setAttrs,props);
			}		
		}

		protected void loadSet(NodeList nodeList,Properties p) {
			for (int i = 0 ; i < nodeList.getLength() ; i ++){
				Node node = nodeList.item(i);
				if (Node.ELEMENT_NODE != node.getNodeType()){
					continue;
				}
				
				Properties props = new XmlElementProperties((Element)node,p);		
				String group = PropertiesConstants.getString(props, "group", DEFAULT_GROUP);
				
				if (StringUtils.isNotEmpty(delimeter)){
					sAdd(group, PropertiesConstants.getString(props, "members", "").split(delimeter));
				}else{
					sAdd(group, PropertiesConstants.getString(props, "members", ""));
				}
			}
		}

		protected void loadHash(NodeList nodeList,Properties p) {
			for (int i = 0 ; i < nodeList.getLength() ; i ++){
				Node node = nodeList.item(i);
				if (Node.ELEMENT_NODE != node.getNodeType()){
					continue;
				}
				
				Properties props = new XmlElementProperties((Element)node,p);		
				String group = PropertiesConstants.getString(props, "group", DEFAULT_GROUP);
				
				String key = PropertiesConstants.getString(props, "key", "");
				String value = PropertiesConstants.getString(props, "value", "");
				if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)){
					hSet(group, key, value, true);
				}
			}
		}
		
	}
}