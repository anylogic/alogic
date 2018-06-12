package com.alogic.poi.xscript;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

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
 * 数据行
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsRow extends Segment{
	protected String pid = "$worksheet";
	protected String cid = "$workrow";
	protected String $row = "0";
	protected String $style = "";
	protected String smId = "$stylemanager";
	protected String $height = "0";
	
	public XsRow(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid);
		cid = PropertiesConstants.getString(p,"cid",cid);
		$row = PropertiesConstants.getRaw(p,"row",$row);
		$height = PropertiesConstants.getRaw(p,"height",$height);
		smId = PropertiesConstants.getString(p,"smId",smId);
		$style = PropertiesConstants.getRaw(p,"style",$style);		
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Sheet sheet = ctx.getObject(pid);
		if (sheet == null){
			throw new BaseException("core.e1001","It must be in a worksheet context,check your together script.");
		}
		int index = PropertiesConstants.transform(ctx, $row, 0);
		
		Row row = sheet.getRow(index);
		if (row == null){
			row = sheet.createRow(index);
		}
		
		String style = PropertiesConstants.transform(ctx, $style, "");
		if (StringUtils.isNotEmpty(style)){
			StyleManager sm = ctx.getObject(smId);
			if (sm != null){
				CellStyle cellStyle = sm.getStyle(style);
				if (cellStyle != null){
					row.setRowStyle(cellStyle);
				}
			}
		}
		
		int height = PropertiesConstants.transform(ctx,$height,0);
		if (height > 0){
			row.setHeightInPoints(height);
		}
		
		try {
			ctx.setObject(cid, row);			
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}	
}