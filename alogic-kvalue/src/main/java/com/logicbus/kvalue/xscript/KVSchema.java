package com.logicbus.kvalue.xscript;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.context.KValueSource;
import com.logicbus.kvalue.core.Schema;

/**
 * Schema
 * 
 * @author duanyy
 *
 */
public class KVSchema extends KVNS{

	/**
	 * 指定的schema
	 */
	protected String schema = "";

	/**
	 * 上下文id
	 */
	protected String cid = "$kv-schema";
	
	public KVSchema(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		schema = PropertiesConstants.getString(p,"schema","",true);
		cid = PropertiesConstants.getString(p, "cid", cid,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Schema s = KValueSource.getSchema(schema);
		if (s == null){
			log(String.format("Can not find the schema[%s]",schema), "error");
			return ;
		}
	
		try {
			ctx.setObject(cid, s);
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}	
}
