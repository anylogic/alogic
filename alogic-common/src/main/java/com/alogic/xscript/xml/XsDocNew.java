package com.alogic.xscript.xml;

import java.util.Stack;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;

/**
 * 新建一个xml文档
 * 
 * @author yyduan
 * @since 1.6.11.38
 */
public class XsDocNew extends NS{
	protected String cid = "$xml-stack";
	protected String $root = "root";
	
	public XsDocNew(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		cid = PropertiesConstants.getString(p, "cid", cid,true);
		$root = PropertiesConstants.getRaw(p,"root",$root);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {		
		Stack<Element> stack = new Stack<Element>();
		try {
			Document doc = XmlTools.newDocument(PropertiesConstants.transform(ctx, $root, "root"));			
			stack.add(doc.getDocumentElement());
			ctx.setObject(cid, stack);
			super.onExecute(root, current, ctx, watcher);
		}catch (Exception ex){
			logger.info(ExceptionUtils.getStackTrace(ex));
		}finally{
			stack.pop();
			ctx.removeObject(cid);
		}
	}
}
