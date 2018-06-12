package com.alogic.cube.xscript;

import com.alogic.cube.mdr.DataTable;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 创建一个数据表
 * 
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsTable extends NS{

	protected String cid = "$cube-table";
	
	public XsTable(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		cid = PropertiesConstants.getString(p,"cid",cid,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		try {
			DataTable table = new DataTable();
			ctx.setObject(cid, table);
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}	
}
