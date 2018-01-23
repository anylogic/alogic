package com.alogic.vfs.xscript;

import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import com.alogic.vfs.core.VirtualFileSystem;
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
 * 将上传的文件存储到VFS
 * 
 * @author yyduan
 *
 */
public class UploadToVFS extends AbstractLogiclet{
	protected String pid = "$upload";
	protected String pVfsId = "$vfs";
	protected String $path;
	protected String id;
	protected int bufferSize = 10 * 1024;
	
	public UploadToVFS(String tag, Logiclet p) {
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
		
	}

	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		FileItem fileItem = ctx.getObject(pid);
		if (fileItem == null){
			throw new BaseException("core.e1001","It must be in a upload-scan context,check your together script.");
		}
		
		VirtualFileSystem vfs = ctx.getObject(pVfsId);
		if (vfs == null){
			throw new BaseException("core.e1001",String.format("Can not find vfs:%s", pid));
		}
		
		String path = PropertiesConstants.transform(ctx, $path, "");
		if (StringUtils.isNotEmpty(path)){
			OutputStream out = null;
			InputStream in = null;
			try {				
				in = fileItem.getInputStream();
				out = vfs.writeFile(path);
				
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
				IOTools.close(in);
				vfs.finishWrite(path, out);
			}
		}else{
			ctx.SetValue(id, "false");
		}
	}
}
