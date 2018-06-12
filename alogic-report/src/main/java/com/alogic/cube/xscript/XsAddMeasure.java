package com.alogic.cube.xscript;

import com.alogic.cube.mdr.DataTable;
import com.alogic.cube.mdr.Measure;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 增加量度
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsAddMeasure extends AbstractLogiclet implements Measure{
	protected String pid = "$cube-table";
	protected String id = "";
	protected String $value = "";
	protected String style = "";
	/**
	 * 计算方法
	 */
	protected Method method;	
	
	public XsAddMeasure(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p, "pid", pid,true);
		id = PropertiesConstants.getString(p,"id","",true);
		$value = PropertiesConstants.getRaw(p,"value","");
		style = PropertiesConstants.getString(p,"style","");
		method = Method.valueOf(PropertiesConstants.getString(p, "method","sum"));		
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		DataTable table = ctx.getObject(pid);
		if (table != null){
			table.addMeasure(this);
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getValue(Properties provider) {
		return PropertiesConstants.transform(provider, $value, 0L);
	}

	@Override
	public String getStyle() {
		return style;
	}
	
	@Override
	public Method getMethod() {
		return method;
	}
}