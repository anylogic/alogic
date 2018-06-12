package com.alogic.cube.xscript;

import com.alogic.cube.mdr.DataTable;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 采集数据
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsSum extends AbstractLogiclet{
	protected String pid = "$cube-table";
	
	public XsSum(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p, "pid", pid,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		DataTable table = ctx.getObject(pid);
		if (table != null){
			table.sum(ctx);
		}
	}
}
