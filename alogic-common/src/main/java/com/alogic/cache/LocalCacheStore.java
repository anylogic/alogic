package com.alogic.cache;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.load.Loader;
import com.alogic.load.Store;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 本地Hash实现的CacheStore
 * 
 * @author yyduan
 * @since 1.6.11.6
 * 
 * @version 1.6.11.29 [20180510 duanyy] <br>
 * - 增加on-load事件处理; <br>
 * 
 * @version 1.6.11.58 [20180829 duanyy] <br>
 * - 修正on-load之后，所注销对象的变量id <br>
 */
public class LocalCacheStore extends Store.HashStore<CacheObject>{
	/**
	 * 加载事件脚本
	 */
	protected Logiclet onLoad = null;
	protected String cacheObjectId = "$cache-object";
	
	@Override
	public CacheObject newObject(String id) {
		return new CacheObject.Simple(id);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		cacheObjectId = PropertiesConstants.getString(p,"cacheObjectId",cacheObjectId,true);
	}
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, getSinkTag());
		Factory<Loader<CacheObject>> factory = new Factory<Loader<CacheObject>>();
		String scope = PropertiesConstants.getString(p, "ketty.scope", "runtime");
		
		for (int i = 0 ;i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			
			if (Node.ELEMENT_NODE != n.getNodeType()){
				continue;
			}
			
			Element elem = (Element)n;
			
			String itemScope = XmlTools.getString(elem, "scope", "");
			if (StringUtils.isNotEmpty(itemScope) && !itemScope.equals(scope)){
				continue;
			}
			
			try {
				Loader<CacheObject> loader = factory.newInstance(elem, props, "module");
				if (loader != null){
					loaders.add(loader);
				}
			}catch (Exception ex){
				LOG.error("Can not create loader from element:" + XmlTools.node2String(elem));
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		
		Element onLoadElem = XmlTools.getFirstElementByPath(e, "on-load");
		if (onLoadElem != null){
			onLoad = Script.create(onLoadElem, props);
		}
	}	
	
	@Override
	protected void onLoad(String id, CacheObject cache) {
		if (onLoad != null){
			LogicletContext logicletContext = new LogicletContext(Settings.get());
	
			try {
				logicletContext.setObject(cacheObjectId, cache);
				XsObject doc = new JsonObject("root",new HashMap<String,Object>());
				onLoad.execute(doc,doc, logicletContext, null);
			}catch (Exception ex){
				LOG.info("Failed to execute onload script" + ExceptionUtils.getStackTrace(ex));
			}finally{
				logicletContext.removeObject(cacheObjectId);
			}
		}
	}	
}
