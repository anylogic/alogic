package com.alogic.together.plugins;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.together.AbstractLogiclet;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.alogic.together.util.MapProperties;
import com.anysoft.selector.Selector;
import com.anysoft.selector.impl.Constants;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;

/**
 * 条件语句
 * @author duanyy
 *
 */
public class Switch extends AbstractLogiclet{

	/**
	 * 子语句
	 */
	protected Map<String,Logiclet> children = new HashMap<String,Logiclet>();
	
	protected Selector selector = null;
	
	public Switch(String tag, Logiclet p) {
		super(tag, p);
	}	
	
	@Override
	public void configure(Element element, Properties props) {
		XmlElementProperties p = new XmlElementProperties(element, props);
		
		selector = Selector.newInstanceWithDefault(element, p, Constants.class.getName());
		
		NodeList nodeList = element.getChildNodes();
		
		for (int i = 0 ; i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				//只处理Element节点
				continue;
			}
			
			Element e = (Element)n;
			String caseValue = e.getAttribute(STMT_CASE);
			if (StringUtils.isNotEmpty(caseValue)){
				String xmlTag = e.getNodeName();		
				Logiclet statement = createLogiclet(xmlTag, this);
				
				if (statement != null){
					statement.configure(e, p);
					children.put(caseValue, statement);
				}
			}
		}
		
		configure(p);
	}	

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String caseValue = "default";
		if (selector != null){
			MapProperties p = new MapProperties(current,ctx);
			caseValue = selector.select(p);
		}
		
		Logiclet stmt = children.get(caseValue);
		if (stmt == null){
			if (!caseValue.equals(STMT_DEFAULT)){
				stmt = children.get(STMT_DEFAULT);
			}
		}
		
		if (stmt != null){
			stmt.execute(root, current, ctx, watcher);
		}
	}	
	
}