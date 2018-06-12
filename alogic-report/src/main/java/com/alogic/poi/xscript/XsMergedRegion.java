package com.alogic.poi.xscript;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.alogic.poi.util.StyleManager;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 合并区域
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsMergedRegion extends AbstractLogiclet{
	protected String pid = "$worksheet";
	
	protected String $left = "0";
	protected String $right = "";
	protected String $top = "0";
	protected String $bottom = "";
	
	protected String $style = "";
	protected String smId = "$stylemanager";
	
	public XsMergedRegion(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid);
		$left = PropertiesConstants.getRaw(p,"left",$left);
		$right = PropertiesConstants.getRaw(p,"right",$right);
		$top = PropertiesConstants.getRaw(p,"top",$top);
		$bottom = PropertiesConstants.getRaw(p,"bottom",$bottom);
		smId = PropertiesConstants.getString(p,"smId",smId);
		$style = PropertiesConstants.getRaw(p,"style",$style);			
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Sheet sheet = ctx.getObject(pid);
		if (sheet == null){
			throw new BaseException("core.e1001","It must be in a worksheet context,check your together script.");
		}
		int left = PropertiesConstants.transform(ctx, $left, 0);
		int right = PropertiesConstants.transform(ctx, $right, left + 1);
		int top = PropertiesConstants.transform(ctx, $top, 0);
		int bottom = PropertiesConstants.transform(ctx, $bottom, top + 1);		
		String style = PropertiesConstants.transform(ctx, $style, "");
		if (StringUtils.isNotEmpty(style)){
			StyleManager sm = ctx.getObject(smId);
			if (sm != null){
				CellStyle cellStyle = sm.getStyle(style);
				if (cellStyle != null){
					for (int i = top ; i <= bottom ; i ++){
						Row row = sheet.getRow(i);
						if (row == null){
							row = sheet.createRow(i);
						}
						for (int j = left ; j <= right ; j++){
							Cell cell = row.getCell(j);
							if (cell == null){
								cell = row.createCell(j);
							}
							cell.setCellStyle(cellStyle);
						}
					}
				}
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(top, bottom, left, right));
	}	
}
