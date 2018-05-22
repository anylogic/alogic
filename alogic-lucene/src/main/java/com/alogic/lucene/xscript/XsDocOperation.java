package com.alogic.lucene.xscript;

import org.apache.lucene.document.Document;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 基于Document的操作
 * 
 * @author yyduan
 * @since 1.6.11.31
 */
public abstract class XsDocOperation extends Segment{
	/**
	 * 父节点的上下文id
	 */
	protected String pid = "$lucene-doc";
		
	public XsDocOperation(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Document doc = ctx.getObject(pid);
		if (doc == null){
			throw new BaseException("core.e1001","It must be in a lucene-doc context,check your together script.");
		}

		onExecute(doc,root,current,ctx,watcher);
	}

	protected abstract void onExecute(Document doc, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher);
}
