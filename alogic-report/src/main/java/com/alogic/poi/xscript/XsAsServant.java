package com.alogic.poi.xscript;

import java.io.IOException;
import java.io.OutputStream;
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
import com.logicbus.backend.Context;

/**
 * 作为服务数据写出
 * 
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsAsServant extends AbstractLogiclet{
	protected String pid = "$workbook";
	protected String contextId = "$context";
	protected String $contentType = "application/vnd.ms-excel";
	
	public XsAsServant(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid);
		contextId = PropertiesConstants.getString(p,"contextId",contextId);
		$contentType = PropertiesConstants.getRaw(p,"contentType",$contentType);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Workbook workbook = ctx.getObject(pid);
		if (workbook == null){
			throw new BaseException("core.e1001","It must be in a workbook context,check your together script.");
		}

		Context serviceContext = ctx.getObject(contextId);
		if (serviceContext == null){
			throw new BaseException("core.e1001","It must be in a DownloadTogetherServant servant,check your together script.");
		}
		
		serviceContext.setResponseContentType(PropertiesConstants.transform(ctx, $contentType, "application/vnd.ms-excel"));
		OutputStream output = null;
		try {
			output = serviceContext.getOutputStream();
			workbook.write(output);
		} catch (IOException e) {
			throw new BaseException("core.e1004","Can not write excel file.");
		}finally{
			IOTools.close(output);
		}
	}	
}

