package com.alogic.cube.xscript;

import org.w3c.dom.Element;

import com.alogic.cube.mdr.DataReport;
import com.alogic.cube.mdr.DataTable;
import com.alogic.cube.mdr.DimensionRow;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 输出报告
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsReport extends AbstractLogiclet{
	
	protected String pid = "$cube-table";
	protected String cid = "$cube-row";
	
	protected Logiclet onHeader = null;
	protected Logiclet onTotal = null;
	protected Logiclet onRow = null;
	protected Logiclet onRowStart = null;
	protected Logiclet onRowEnd = null;
	
	public XsReport(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);		
		pid = PropertiesConstants.getString(p, "pid", pid,true);
		cid = PropertiesConstants.getString(p, "cid", cid,true);
	}	
	
	@Override
	public void configure(Element element, Properties props) {
		XmlElementProperties p = new XmlElementProperties(element, props);
		
		Element header = XmlTools.getFirstElementByPath(element, "on-header");
		if (header != null){
			onHeader = this.createLogiclet("segment", this);
			if (onHeader != null){
				onHeader.configure(header, p);
			}
		}

		Element total = XmlTools.getFirstElementByPath(element, "on-total");
		if (total != null){
			onTotal = this.createLogiclet("segment", this);
			if (onTotal != null){
				onTotal.configure(total, p);
			}
		}
		
		Element row = XmlTools.getFirstElementByPath(element, "on-row");
		if (row != null){
			onRow = this.createLogiclet("segment", this);
			if (onRow != null){
				onRow.configure(row, p);
			}
		}	
		
		Element rowStart = XmlTools.getFirstElementByPath(element, "on-row-start");
		if (rowStart != null){
			onRowStart = this.createLogiclet("segment", this);
			if (onRowStart != null){
				onRowStart.configure(rowStart, p);
			}
		}	
		
		Element rowEnd = XmlTools.getFirstElementByPath(element, "on-row-end");
		if (rowEnd != null){
			onRowEnd = this.createLogiclet("segment", this);
			if (onRowEnd != null){
				onRowEnd.configure(rowEnd, p);
			}
		}		
		
		configure(p);
	}	
	
	@Override
	protected void onExecute(final XsObject root,final XsObject current, final LogicletContext ctx, final ExecuteWatcher watcher) {
		DataTable table = ctx.getObject(pid);
		if (table != null){
			table.report(new DataReport(){

				@Override
				public void onHeader(DimensionRow headRow) {
					if (onHeader != null){
						try {
							ctx.SetValue("$rowId", headRow.getId());
							ctx.SetValue(headRow.getDimId(), headRow.getId());
							ctx.setObject(cid, headRow);
							onHeader.execute(root, current, ctx, watcher);
						}finally{
							ctx.removeObject(cid);
						}
					}
				}

				@Override
				public void onTotal(DimensionRow totalRow) {
					if (onTotal != null){
						try {
							ctx.SetValue("$rowId", totalRow.getId());
							ctx.SetValue(totalRow.getDimId(), totalRow.getId());
							ctx.setObject(cid, totalRow);
							onTotal.execute(root, current, ctx, watcher);
						}finally{
							ctx.removeObject(cid);
						}
					}
				}

				@Override
				public void onRow(DimensionRow dataRow) {
					if (onRow != null){
						try {
							ctx.SetValue("$rowId", dataRow.getId());
							ctx.SetValue(dataRow.getDimId(), dataRow.getId());
							ctx.setObject(cid, dataRow);
							onRow.execute(root, current, ctx, watcher);
						}finally{
							ctx.removeObject(cid);
						}					
					}
				}

				@Override
				public void onRowStart(String dimId,String id) {
					ctx.SetValue(dimId, id);
				}

				@Override
				public void onRowEnd(String dimId,String id) {
					ctx.SetValue(dimId, "");
				}});
		}
	}	
}
