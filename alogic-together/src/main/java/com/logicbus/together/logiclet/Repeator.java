package com.logicbus.together.logiclet;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.together.AbstractLogiclet;
import com.logicbus.together.ExecuteWatcher;
import com.logicbus.together.Logiclet;
import com.logicbus.together.LogicletFactory;


/**
 * Repeator
 * 
 * <br>
 * 针对目标节点,进行循环调用
 * 
 * @author duanyy
 * @since 1.1.0
 * 
 * @version 1.2.0 增加对JSON支持
 */
public class Repeator extends AbstractLogiclet {

	
	protected void onCompile(Element config, Properties myProps,LogicletFactory factory)
			throws ServantException {
		targetPath = PropertiesConstants.getString(myProps, "target", "");
		
		Element childElement = XmlTools.getFirstElementByTagName(config, "logiclet");
		if (childElement != null){
			String module = childElement.getAttribute("module");
			if (module != null && module.length() > 0){
				if (factory != null){
					child = factory.newLogiclet(module);
				}
				if (child == null){
					child = newLogiclet(module);
				}
				if (child != null){
					child.compile(childElement, myProps, this,factory);
				}
			}
		}
	}

	
	protected void onExecute(Element target, Message msg, Context ctx,ExecuteWatcher watcher)
			throws ServantException {
		if (child == null){
			//no child ,do nothing
			return ;
		}
		
		NodeList nodeList = XmlTools.getNodeListByPath(target, targetPath);
		
		if (nodeList.getLength() > 0){
			for (int i = 0 ,length = nodeList.getLength(); i < length ; i ++){
				Node n = nodeList.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}				
				child.execute((Element)n,msg,ctx,watcher);
			}
		}else{
			child.execute(target, msg, ctx,watcher);
		}
	}
	
	/**
	 * 目标节点路径
	 */
	protected String targetPath;
	
	/**
	 * 子logiclet
	 */
	protected Logiclet child = null;

	@SuppressWarnings("rawtypes")
	
	protected void onExecute(Map target, Message msg, Context ctx,
			ExecuteWatcher watcher) throws ServantException {
		if (child == null){
			//no child ,do nothing
			return ;
		}
		
		Object obj = target.get(targetPath);
		if (obj != null && obj instanceof List){
			List list = (List)obj;
			for (int i = 0 ; i < list.size() ; i ++){
				Object _child = list.get(i);
				if (_child instanceof Map){
					child.execute((Map)_child,msg,ctx,watcher);
				}
			}
		}else{
			child.execute(target, msg, ctx,watcher);
		}
	}
}
