package com.alogic.poi.xscript;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 保存到文件
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsAsLocalFile extends AbstractLogiclet{
	protected String pid = "$workbook";
	
	protected String $path = "";
	
	public XsAsLocalFile(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid);
		$path = PropertiesConstants.getRaw(p,"path",$path);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Workbook workbook = ctx.getObject(pid);
		if (workbook == null){
			throw new BaseException("core.e1001","It must be in a workbook context,check your together script.");
		}
		String path = PropertiesConstants.transform(ctx, $path, "");
		if (StringUtils.isNotEmpty(path)){
			File file = new File(path);
			if (file.exists()){
				file.delete();
			}
			
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(file);
				workbook.write(output);
			} catch (IOException e) {
				throw new BaseException("core.e1004","Can not output file:" + path);
			}finally{
				IOTools.close(output);
			}
		}
	}	
}
