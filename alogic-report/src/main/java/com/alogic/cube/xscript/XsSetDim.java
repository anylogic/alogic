package com.alogic.cube.xscript;

import com.alogic.cube.mdr.DataTable;
import com.alogic.cube.mdr.Dimension;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/***
 * 设置行维度
 * 
 * @author yyduan
 * @since 1.6.11.35
 */
public abstract class XsSetDim extends Segment implements Dimension{
	protected String pid = "$cube-table";
	protected String id = "";
	protected String $value = "";
	protected String style = "";
	/**
	 * 子维度
	 */
	protected Dimension next = null;
	
	public XsSetDim(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p, "pid", pid,true);
		id = PropertiesConstants.getString(p,"id","",true);
		$value = PropertiesConstants.getRaw(p,"value","");
		style = PropertiesConstants.getString(p,"style","");
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		DataTable table = ctx.getObject(pid);
		if (table != null){
			setDimension(table);			
			super.onExecute(root, current, ctx, watcher);
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getValue(Properties provider) {
		return PropertiesConstants.transform(provider, $value, id);
	}

	@Override
	public String getStyle() {
		return style;
	}
	
	@Override
	public Dimension next() {
		return next;
	}

	@Override
	public void append(Dimension dim) {
		if (next != null){
			next.append(dim);
		}else{
			next = dim;
		}
	}	
	
	abstract protected void setDimension(DataTable table);
	
	/**
	 * 设置列维度
	 * @author yyduan
	 *
	 */
	public static class Column extends XsSetDim{

		public Column(String tag, Logiclet p) {
			super(tag, p);
		}

		@Override
		protected void setDimension(DataTable table) {
			table.addColDimension(this);
		}
	}
	
	/**
	 * 设置行维度
	 * @author yyduan
	 *
	 */
	public static class Row extends XsSetDim{

		public Row(String tag, Logiclet p) {
			super(tag, p);
		}

		@Override
		protected void setDimension(DataTable table) {
			table.addRowDimension(this);
		}
	}	
}
