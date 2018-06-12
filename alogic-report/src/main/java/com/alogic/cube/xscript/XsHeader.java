package com.alogic.cube.xscript;

import com.alogic.cube.mdr.Dimension;
import com.alogic.cube.mdr.DimensionRow;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 处理header
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsHeader extends Segment{
	protected String pid = "$cube-row";
	
	public XsHeader(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
	}
	
	@Override
	protected void onExecute(final XsObject root,final XsObject current, final LogicletContext ctx, final ExecuteWatcher watcher) {
		DimensionRow row = ctx.getObject(pid);
		if (row != null){
			String[] columns = row.getHeaderColumns();		
			Dimension dim = row.getColDimension();
			
			for (String column:columns){
				ctx.SetValue("$column", column);
				ctx.SetValue("$columnStyle", dim.getStyle());
				super.onExecute(root, current, ctx, watcher);
			}
		}
	}
}
