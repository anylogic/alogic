package com.alogic.poi.xscript;

import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.resource.ResourceFactory;

/**
 * xslx文档
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsXSSFWorkbook extends NS{
	protected String cid = "$workbook";
	protected String $path = "";
	
	public XsXSSFWorkbook(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		cid = PropertiesConstants.getString(p, "cid", cid);
		$path = PropertiesConstants.getRaw(p,"path","");
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Workbook workbook = null;
		String path = PropertiesConstants.transform(ctx, $path, "");
		if (StringUtils.isNotEmpty(path)){
			workbook = loadWorkbook(path);
		}else{
			workbook = new XSSFWorkbook();
		}
		try {
			ctx.setObject(cid, workbook);			
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}

	private static Workbook loadWorkbook(String path) {
		ResourceFactory rf = Settings.getResourceFactory();		
		InputStream in = null;
		try {
			in = rf.load(path, null, null);
			return new XSSFWorkbook(in);
		}catch (Exception ex){
			throw new BaseException("core.e1008","Can not load xls file from path:" + path);
		}finally{
			IOTools.close(in);
		}
	}	
}