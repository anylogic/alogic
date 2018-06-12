package com.alogic.poi.xscript;

import org.apache.poi.ss.usermodel.Sheet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 设置列宽
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsColumnWidth extends Segment{
	protected String pid = "$worksheet";
	protected String $col = "0";
	protected String $width = "0";
	
	public XsColumnWidth(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid);
		$col = PropertiesConstants.getRaw(p,"col",$col);
		$width = PropertiesConstants.getRaw(p,"width",$width);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Sheet sheet = ctx.getObject(pid);
		if (sheet == null){
			throw new BaseException("core.e1001","It must be in a worksheet context,check your together script.");
		}
		
		int col = PropertiesConstants.transform(ctx, $col, 0);
		int width = PropertiesConstants.transform(ctx, $width, 0);
		if (width > 0){
			sheet.setColumnWidth(col,width);
		}else{
			sheet.autoSizeColumn(col, true);
		}
	}	
}
