package com.alogic.xscript.xml;

import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
 * 在XML中定位到某个Element
 * @author yyduan
 * @since 1.6.11.38
 */
public class XsLocation extends Segment{
	protected String pid = "$xml-stack";
	protected String $path = "";
	
	public XsLocation(String tag, Logiclet p) {
		super(tag, p);
	}
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		$path = getPathPattern(p);
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
					Node found = XmlTools.getNodeByPath(elem, path);
					if (found != null && found instanceof Element){
						Element child = (Element)found;
						try {						
							stack.add(getSinkElement(child));
							onChildBegin(child,ctx);
							super.onExecute(root, current, ctx, watcher);
						}finally{
							onChildEnd(child,ctx);
							stack.pop();
						}					
					}
				}
			}
		}
	}
	
	protected Element getSinkElement(Element e){
		return e;
	}
	
	protected String getPathPattern(Properties p){
		return PropertiesConstants.getRaw(p,"path",$path);
	}
			
	protected void onChildEnd(Element child, LogicletContext ctx) {
		// nothing to do
	}
	protected void onChildBegin(Element child, LogicletContext ctx) {
		// nothing to do
	}	
}