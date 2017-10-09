package com.alogic.remote.xscript.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.remote.Request;
import com.alogic.remote.xscript.RequestHandler;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;


/**
 * 通过构造的JSON文档发送消息体
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public class ByJson extends RequestHandler {
	protected static JsonProvider provider = null;	
	static {
		provider = JsonProviderFactory.createProvider();
	}

	/**
	 * 子节点
	 */
	protected List<Logiclet> children = new ArrayList<Logiclet>(); // NOSONAR
	
	public ByJson(String tag, Logiclet p) {
		super(tag, p);
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
	
	@Override
	protected void onExecute(final Request req, final XsObject root,final XsObject current, final LogicletContext ctx,
			final ExecuteWatcher watcher) {
		Map<String,Object> jsonData = new HashMap<String,Object>();
		XsObject doc = new JsonObject("root",jsonData);
		for (Logiclet child:children){
			child.execute(doc, doc, ctx, watcher);
		}
		
		String text = provider.toJson(jsonData);
		
		req.setBody(text);
	}
}
