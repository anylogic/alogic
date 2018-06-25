package com.alogic.xscript.xml;

import java.util.Stack;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;

/**
 * 从内容中解析出xml文档
 * 
 * @author yyduan
 * @since 1.6.11.38
 */
public class XsDocLoad extends NS{
	protected String cid = "$xml-stack";
	protected static String DEFAULT_CONTENT = "<?xml version='1.0'?><root/>";
	protected String $content = DEFAULT_CONTENT;
	
	public XsDocLoad(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		cid = PropertiesConstants.getString(p, "cid", cid,true);
		$content = PropertiesConstants.getRaw(p,"content",$content);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {		
		String content = PropertiesConstants.transform(ctx, $content, DEFAULT_CONTENT);
		Stack<Element> stack = new Stack<Element>();
		try {			
			Document doc = XmlTools.loadFromContent(content);
			stack.add(doc.getDocumentElement());
			ctx.setObject(cid, stack);
			super.onExecute(root, current, ctx, watcher);
		}catch (Exception ex){
			logger.info(ExceptionUtils.getStackTrace(ex));
			throw new BaseException("core.e1018","Xml file is not valid:" + content);
		}finally{
			stack.pop();
			ctx.removeObject(cid);
		}
	}
}
