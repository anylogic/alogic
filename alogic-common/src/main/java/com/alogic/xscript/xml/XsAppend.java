package com.alogic.xscript.xml;

import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 增加子节点
 * @author yyduan
 * @since 1.6.11.38
 */
public class XsAppend extends Segment{
	protected String pid = "$xml-stack";
	protected String $tag = "";
	
	public XsAppend(String tag, Logiclet p) {
		super(tag, p);
	}
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		$tag = getElementTag(p);
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
				String tag = PropertiesConstants.transform(ctx, $tag, "");
				if (StringUtils.isNotEmpty(tag)){
					Document doc = elem.getOwnerDocument();
					Element child = doc.createElement(tag);					
					try {						
						onChildBegin(child,ctx);
						stack.add(getSinkElement(child));
						super.onExecute(root, current, ctx, watcher);
					}finally{
						onChildEnd(child,ctx);
						elem.appendChild(child);
						stack.pop();
					}
				}
			}
		}
	}
	
	protected Element getSinkElement(Element e){
		return e;
	}
	
	protected String getElementTag(Properties p){
		return PropertiesConstants.getRaw(p,"tag",$tag);
	}
			
	protected void onChildEnd(Element child, LogicletContext ctx) {
		// nothing to do
	}
	protected void onChildBegin(Element child, LogicletContext ctx) {
		// nothing to do
	}
}
