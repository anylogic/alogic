package com.alogic.lucene.xscript;

import org.apache.lucene.document.Document;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 创建一个lucene文档
 * @author yyduan
 * @since 1.6.11.31
 */
public class XsDoc extends Segment{

	protected String cid = "$lucene-doc";
	
	public XsDoc(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		cid = PropertiesConstants.getString(p,"pid",cid,true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		ctx.setObject(cid, new Document());
		try {
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}		
	}	
}
