package com.alogic.cube.xscript;

import java.util.List;

import com.alogic.cube.mdr.DataCell;
import com.alogic.cube.mdr.DataRow;
import com.alogic.cube.mdr.DataValue;
import com.alogic.cube.mdr.Measure;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 数据值
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsValue extends Segment{
	protected String pid = "$cube-cell";
	protected String rowId = "$cube-row";
	
	public XsValue(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		rowId = PropertiesConstants.getString(p,"rowId",rowId,true);
	}
	
	@Override
	protected void onExecute(final XsObject root,final XsObject current, final LogicletContext ctx, final ExecuteWatcher watcher) {
		DataCell cell = ctx.getObject(pid);
		if (cell != null){
			List<DataValue> values = cell.getValues();
			for (DataValue val:values){
				ctx.SetValue("$value", String.valueOf(val.getValue()));
				ctx.SetValue("$count", String.valueOf(val.getCount()));
				ctx.SetValue("$measure", val.getMeasure().getId());
				ctx.SetValue("$style", val.getMeasure().getStyle());
				super.onExecute(root, current, ctx, watcher);
			}
		}else{
			DataRow row = ctx.getObject(rowId);
			if (row != null){
				List<Measure> measures = row.getMeasures();
				for (Measure measure:measures){
					ctx.SetValue("$value", "0");
					ctx.SetValue("$count", "0");
					ctx.SetValue("$measure", measure.getId());
					ctx.SetValue("$style", measure.getStyle());
					super.onExecute(root, current, ctx, watcher);				
				}
			}
		}
	}
}
