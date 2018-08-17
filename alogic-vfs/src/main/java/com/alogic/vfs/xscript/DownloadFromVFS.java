package com.alogic.vfs.xscript;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

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
import com.anysoft.util.Settings;
import com.logicbus.backend.Context;
import com.logicbus.backend.server.http.HttpCacheTool;

/**
 * 从vfs下载
 * 
 * @author yyduan
 * @since 1.6.11.12
 * 
 * @version 1.6.11.48 [20180807 duanyy] <br>
 * - 优化缓存相关的http控制头的输出 <br>
 * 
 * @version 1.6.11.49 [20180808 duanyy] <br>
 * - 修正下载中文名的乱码问题 <br>
 * 
 * @version 1.6.11.53 [20180817 duanyy] <br>
 * - 删除无用代码 <br>
 */
public class DownloadFromVFS extends AbstractLogiclet{
	protected String pid = "$context";
	protected String pVfsId = "$vfs";
	protected String $path;
	protected String id;
	protected int bufferSize = 10 * 1024;
	protected String $cacheEnable = "true";
	protected String $filename = "";
	protected String $contentType = "";	
	protected HttpCacheTool cacheTool = null;
	protected String encoding = "utf-8";
	
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
		$cacheEnable = PropertiesConstants.getRaw(p, "cacheEnable", $cacheEnable);
		$filename = PropertiesConstants.getRaw(p, "filename", $filename);
		$contentType = PropertiesConstants.getRaw(p, "contentType", $contentType);		
		cacheTool = Settings.get().getToolkit(HttpCacheTool.class);
		encoding = PropertiesConstants.getString(p,"encoding",encoding);
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
				if (PropertiesConstants.transform(ctx, $cacheEnable, true)){
					cacheTool.cacheEnable(serviceContext);
				}else{
					cacheTool.cacheDisable(serviceContext);			
				}
				
				String filename = PropertiesConstants.transform(ctx, $filename,"");
				if (StringUtils.isNotEmpty(filename)){
					filename = URLEncoder.encode(filename, encoding);
					serviceContext.setResponseHeader("Content-Disposition", 
						String.format("attachment; filename=%s;filename*=%s''%s",filename,encoding,filename));
				}
				
				String contentType = PropertiesConstants.transform(ctx, $contentType,"");
				if (StringUtils.isNotEmpty(contentType)){
					serviceContext.setResponseContentType(contentType);
				}
				
				in = vfs.readFile(path);
				if (in == null){
					throw new BaseException("core.e1001",String.format("Can not find file:%s", path));
				}
				
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
	
	public static void main(String[] args){
		System.out.println(String.format("%s/%s/%s","a","d","d"));
		System.out.println(String.format("%s/%s/%s","a","d","1","2"));
	}
}