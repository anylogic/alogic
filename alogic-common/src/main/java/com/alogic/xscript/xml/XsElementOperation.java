package com.alogic.xscript.xml;

import java.util.Stack;
import org.w3c.dom.Element;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 针对Element的操作
 * @author yyduan
 * @since 1.6.11.38
 */
public abstract class XsElementOperation extends AbstractLogiclet{
	protected String pid = "$xml-stack";
	
	public XsElementOperation(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
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
				onExecute(elem,root,current,ctx,watcher);
			}
		}
	}

	protected abstract void onExecute(Element elem, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher);	
}
