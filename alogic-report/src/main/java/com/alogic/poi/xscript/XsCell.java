package com.alogic.poi.xscript;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import com.alogic.poi.util.StyleManager;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 单元格
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsCell  extends Segment{
	protected String pid = "$workrow";	
	protected String $col = "0";
	protected String $value = "";
	protected String $style = "";
	protected String smId = "$stylemanager";
	
	public XsCell(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid);
		$col = PropertiesConstants.getRaw(p,"col",$col);
		$value = PropertiesConstants.getRaw(p,"value",$value);
		smId = PropertiesConstants.getString(p,"smId",smId);
		$style = PropertiesConstants.getRaw(p,"style",$style);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Row row = ctx.getObject(pid);
		if (row == null){
			throw new BaseException("core.e1001","It must be in a row context,check your together script.");
		}
		
		int index = PropertiesConstants.transform(ctx, $col, 0);
		
		Cell cell = row.getCell(index);
		if (cell == null){
			cell = row.createCell(index);
		}		
		
		String style = PropertiesConstants.transform(ctx, $style, "");
		if (StringUtils.isNotEmpty(style)){
			StyleManager sm = ctx.getObject(smId);
			if (sm != null){
				CellStyle cellStyle = sm.getStyle(style);
				if (cellStyle != null){
					cell.setCellStyle(cellStyle);
				}
			}
		}
		setCellValue(cell,ctx,$value);
	}	
	
	protected void setCellValue(Cell cell,Properties p,String pattern){
		cell.setCellValue(PropertiesConstants.transform(p, pattern, ""));
	}
	
	/**
	 * 设置数字值
	 * @author yyduan
	 *
	 */
	public static class NumericValue extends XsCell{

		public NumericValue(String tag, Logiclet p) {
			super(tag, p);
		}
		
		protected void setCellValue(Cell cell,Properties p,String pattern){
			cell.setCellValue(PropertiesConstants.transform(p, pattern, 0.0));
		}		
	}
}