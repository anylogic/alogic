package com.alogic.vfs.xscript;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang3.StringUtils;

import com.alogic.vfs.core.VirtualFileSystem;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;

/**
 * 从vfs下载
 * 
 * @author yyduan
 * @since 1.6.11.12
 */
public class DownloadFromVFS extends AbstractLogiclet{
	protected String pid = "$context";
	protected String pVfsId = "$vfs";
	protected String $path;
	protected String id;
	protected int bufferSize = 10 * 1024;
	protected String $contentType = "text/plain";
	
	public DownloadFromVFS(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		$path = PropertiesConstants.getRaw(p,"path",$path);
		id = PropertiesConstants.getString(p,"id","$" + getXmlTag(),true);
		pVfsId = PropertiesConstants.getString(p,"pVfsId",pVfsId,true);
		bufferSize = PropertiesConstants.getInt(p, "bufferSize", bufferSize);
		$contentType = PropertiesConstants.getRaw(p, "contentType", $contentType);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		Context serviceContext = ctx.getObject(pid);
		if (serviceContext == null){
			throw new BaseException("core.e1001","It must be in a DownloadTogetherServant servant,check your together script.");
		}
		
		VirtualFileSystem vfs = ctx.getObject(pVfsId);
		if (vfs == null){
			throw new BaseException("core.e1001",String.format("Can not find vfs:%s", pid));
		}
		
		String path = PropertiesConstants.transform(ctx, $path, "");
		if (StringUtils.isNotEmpty(path)){
			InputStream in = null;
			try {				
				in = vfs.readFile(path);
				if (in == null){
					throw new BaseException("core.e1001",String.format("Can not find file:%s", path));
				}
				serviceContext.setResponseContentType(PropertiesConstants.transform(ctx, $contentType, "text/plain"));
				OutputStream out = serviceContext.getOutputStream();
				int size = 0;
				byte [] buffer = new byte[bufferSize];
				while ((size = in.read(buffer)) != -1) {
					out.write(buffer, 0, size);
				}
				out.flush();
				ctx.SetValue(id, "true");
			}catch (Exception ex){
				ctx.SetValue(id, "false");
				throw new BaseException("core.e1004",ex.getMessage());
			}finally{
				vfs.finishRead(path, in);
			}
		}else{
			ctx.SetValue(id, "false");
		}
	}
}