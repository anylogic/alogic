package com.alogic.remote.xscript.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.remote.Response;
import com.alogic.remote.xscript.ResponseHandler;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

/**
 * 将结果作为Json对象输出
 * @author yyduan
 * @since 1.6.10.3
 */
public class AsJson extends ResponseHandler {
	protected String masterId = "masterId";
	protected String arrayTag = "array";
	/**
	 * 子节点
	 */
	protected List<Logiclet> children = new ArrayList<Logiclet>(); // NOSONAR
	protected static JsonProvider provider = null;	
	static {
		provider = JsonProviderFactory.createProvider();
	}
	
	public AsJson(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		masterId = PropertiesConstants.getString(p,"masterId",masterId,true);
		arrayTag  = PropertiesConstants.getString(p,"arrayTag",arrayTag,true);
	}
	
	@Override
	public void configure(Element element, Properties props) {
		XmlElementProperties p = new XmlElementProperties(element, props);
		NodeList nodeList = element.getChildNodes();
		
		for (int i = 0 ; i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				//只处理Element节点
				continue;
			}
			
			Element e = (Element)n;
			String xmlTag = e.getNodeName();		
			Logiclet statement = createLogiclet(xmlTag, this);
			
			if (statement != null){
				statement.configure(e, p);
				children.add(statement);
			}
		}
		
		configure(p);
	}		
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onExecute(final Response res,
			final XsObject root,final XsObject current, final LogicletContext ctx,
			final ExecuteWatcher watcher) {
		String body = null;
		try {
			body = res.asString();
			Object json = provider.parse(body);
			
			Map<String,Object> jsonData = null;
			
			if (json instanceof Map){
				jsonData = (Map<String,Object>)(json);
			}else{
				jsonData = new HashMap<String,Object>();
				jsonData.put(arrayTag, json);
			}
			
			//将current转成context对象
			ctx.setObject(masterId, current);
			
			try {
				XsObject doc = new JsonObject("root",jsonData);
				for (Logiclet child:children){
					child.execute(doc, doc, ctx, watcher);
				}
			}finally{
				ctx.removeObject(masterId);
			}
		}catch (IOException ex) {
			throw new BaseException("core.e1004",ExceptionUtils.getStackTrace(ex));
		}
	}
}
