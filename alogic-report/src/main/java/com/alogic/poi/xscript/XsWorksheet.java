package com.alogic.poi.xscript;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 定位到一个worksheet
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsWorksheet extends Segment{
	protected String pid = "$workbook";
	protected String cid = "$worksheet";
	
	protected String $name = "sheet1";
	
	public XsWorksheet(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid);
		cid = PropertiesConstants.getString(p,"cid",cid);
		$name = PropertiesConstants.getRaw(p,"name",$name);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Workbook workbook = ctx.getObject(pid);
		if (workbook == null){
			throw new BaseException("core.e1001","It must be in a workbook context,check your together script.");
		}
		String name = PropertiesConstants.transform(ctx, $name, "sheet1");
		
		Sheet worksheet = workbook.getSheet(name);
		if (worksheet == null){
			worksheet = workbook.createSheet(name);
		}
		
		try {
			ctx.setObject(cid, worksheet);			
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}	
}
