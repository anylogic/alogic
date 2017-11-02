package com.logicbus.kvalue.xscript;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.context.KValueSource;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.kvalue.core.Table;

/**
 * 打开一个KValue的table
 * 
 * @author duanyy
 *
 */
public class KVTable extends KVNS {

	/**
	 * table所在的schema
	 */
	protected String schema = "";
	
	/**
	 * table名
	 */
	protected String table = "";
	
	/**
	 * table的上下文id
	 */
	protected String cid = "$kv-table";
	
	/**
	 * 父节点的上下文id
	 */
	protected String pid = "$kv-schema";
	
	public KVTable(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		schema = PropertiesConstants.getString(p,"schema","",true);
		table = PropertiesConstants.getString(p, "table",table);
		cid = PropertiesConstants.getString(p, "cid",cid,true);
		pid = PropertiesConstants.getString(p, "pid",pid,true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Schema s = ctx.getObject(pid);
		if (s == null){
			s = KValueSource.getSchema(schema);
			if (s == null){
				log(String.format("Can not find the schema[%s]",schema), "error");
				return ;
			}
		}
		
		Table t = s.getTable(table);
		if (t == null){
			log(String.format("Can not find the table [%s/%s]",s.getId(),table),"error");
			return ;
		}
	
		try {
			ctx.setObject(cid, t);
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}

}
