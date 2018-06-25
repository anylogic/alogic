package com.alogic.xscript.xml;

import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;

/**
 * 查询当前节点的子节点
 * 
 * @author yyduan
 * @since 1.6.11.38
 */
public class XsChildren extends Segment{
	protected String pid = "$xml-stack";
	protected String $path = "";
	
	public XsChildren(String tag, Logiclet p) {
		super(tag, p);
	}
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		$path = getPathPattern(p);
	}
	
	protected String getPathPattern(Properties p){
		return PropertiesConstants.getRaw(p,"path",$path);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {		
		Stack<Element> stack = ctx.getObject(pid);
		if (stack == null){
			throw new BaseException("core.e1001","It must be in a xml-element context,check your together script.");
		}
		
		if (!stack.isEmpty()){
			Element elem = stack.peek();
			if (elem != null){
				String path = PropertiesConstants.transform(ctx, $path, "");
				if (StringUtils.isNotEmpty(path)){
					NodeList nodeList = XmlTools.getNodeListByPath(elem, path);
					for (int i = 0; i < nodeList.getLength() ; i ++){
						Node n = nodeList.item(i);
						if (n.getNodeType() != Node.ELEMENT_NODE){
							continue;
						}
						Element e = (Element)n;
						try {
							stack.add(e);
							super.onExecute(root, current, ctx, watcher);
						}finally{
							stack.pop();
						}						
					}
				}
			}
		}
	}
}
