package com.logicbus.kvalue.xscript;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.context.KValueSource;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.kvalue.core.Table;

/**
 * 从table中查找row
 * 
 * @author duanyy
 *
 */
public class KVRow extends KVNS{
	/**
	 * kvtable的cid
	 */
	protected String pid = "$kv-table";
	protected String cid = "$kv-row";
	protected String schemaId = "$kv-schema";
	protected String schema = "";
	protected String table = "";
	protected String key = "";
	protected boolean enableRWSplit = true;
	
	public KVRow(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p){
		super.configure(p);
		schema = PropertiesConstants.getString(p,"schema", schema,true);
		table = PropertiesConstants.getString(p,"table", table,true);
		pid = PropertiesConstants.getString(p,"pid", pid,true);
		cid = PropertiesConstants.getString(p,"cid", cid,true);
		key = PropertiesConstants.getRaw(p, "key", key);
		enableRWSplit = PropertiesConstants.getBoolean(p,"enableRWSplit", enableRWSplit);		
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		Table t = ctx.getObject(pid);
		if (t == null){
			//自己创建schema和table
			Schema s = ctx.getObject(schemaId);
			if (s == null){
				s = KValueSource.getSchema(schema);
				if (s == null){
					log(String.format("Can not find the schema[%s]",schema), "error");
					return ;
				}
			}
			t = s.getTable(table);
			if (t == null){
				log(String.format("Can not find the table [%s/%s]",schema,table),"error");
				return ;
			}
		}
		
		String rowKey = ctx.transform(key);
		KeyValueRow row = t.select(rowKey, enableRWSplit);		
		try {
			ctx.setObject(cid, row);
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}
}